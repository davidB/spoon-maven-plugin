package net.sf.alchim.spoon.contrib.launcher;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;

import spoon.processing.Environment;
import spoon.processing.Severity;

import net.sf.alchim.spoon.contrib.launcher.artifact.Artifact;
import net.sf.alchim.spoon.contrib.launcher.artifact.Repository;
import net.sf.alchim.spoon.contrib.misc.ClasspathHelper;
import net.sf.alchim.spoon.contrib.misc.PathHelper;


/**
 * Apply a set of spoonlet and Spoon's processor.
 */
public class Launcher {

    public final static String SPOON_CFG = "spoon.cfg";

    public static final String SRC_ROOTS = "spoon.src.roots";

    private List<String> sources_ = new ArrayList<String>();

    private Environment env_;

    /**
     * initialize and run a LauncherImpl.
     *
     * @param sources
     *            list of the source directories containing the sources to be
     *            processed.
     * @param processors
     *            List of processors' class to apply.
     * @param env
     *            the running environnment
     * @throws Exception
     */
    public void run(File cfg, List<String> sources, List<String> dependencies, Environment env) throws Throwable {
        if (cfg == null) {
            cfg = searchConfig();
        }

        if (sources == null) {
            sources = searchSources();
        }

        sources_ = new ArrayList<String>();
        sources_.addAll(sources);
        env_ = env;

        List<Artifact> spoonletArtifacts = new ArrayList<Artifact>();
        if ((cfg != null) && cfg.exists()) {
            log("load config from :" + cfg);
            spoonletArtifacts = loadSpoonletsArtifact(cfg);
        }

        List<File> spoonlets = new ArrayList<File>(spoonletArtifacts.size());
        for(Artifact artifact: spoonletArtifacts) {
            spoonlets.add(artifact.getJar());
        }
        // load local spoonlet
        for (String dep : dependencies) {
            File depFile = new File(dep);
            log("search if spoon.xml in :" + depFile + " " + depFile.isDirectory());
            if (depFile.isDirectory()) {
                if (new File(depFile, "spoon.xml").exists()) {
                    log("add local from :" + depFile);
                    spoonlets.add(depFile);
                }
            }
        }

        ClassRealm spoonletsRealm = defClassRealm(spoonletArtifacts, dependencies);

        String classpath0 = ClasspathHelper.toClasspathString(Thread.currentThread().getContextClassLoader());
        String classpath1 = ClasspathHelper.toClasspathString(spoonletsRealm.getClassLoader());
        StringBuilder classpath4compile = new StringBuilder();
        for (String str : dependencies) {
            classpath4compile.append(str).append(File.pathSeparatorChar);
        }

        log("classpath-currentThread :" + classpath0);
        log("classpath-spoonlets     :" + classpath1);
        log("classpath-compile       :" + classpath4compile);
        System.setProperty("java.class.path", classpath4compile.toString() + File.pathSeparator + classpath0 + File.pathSeparator +  classpath1);

        Thread.currentThread().setContextClassLoader( spoonletsRealm.getClassLoader() );

        Class<?> subLauncherClass = spoonletsRealm.loadClass("net.sf.alchim.spoon.contrib.launcher.SubLauncher");
        SubLauncher sl = (SubLauncher) subLauncherClass.newInstance();
        sl.run(sources, env_, cfg, spoonlets);
    }

    /**
     * Load content of spoonlet file (search in the current classpath).
     */
    protected File searchConfig() throws Exception {
        String cfgPath = System.getProperty(SPOON_CFG);
        if (cfgPath == null) {
            cfgPath = System.getProperty("user.dir") + "/spoon.cfg.xml";
        }
        File cfg = new File(cfgPath);
        if (!cfg.exists()) {
            env_.report(null, Severity.MESSAGE, "no config file found");
            return null;
        } else {
            env_.report(null, Severity.MESSAGE, "config file :" + cfg.getAbsolutePath());
        }
        return cfg;
    }

    protected List<String> searchSources() throws Exception {
        return PathHelper.split(System.getProperty(Launcher.SRC_ROOTS));
    }

    protected List<Artifact> loadSpoonletsArtifact(File cfg) throws Exception {
        List<Artifact> spoonletArtifacts = new ArrayList<Artifact>();
        List<URL> remoteRoots = new ArrayList<URL>();
        SpoonCfgXmlHandler.searchSpoonlets(cfg, remoteRoots, spoonletArtifacts);

        Repository artifactRepo = new Repository();
        artifactRepo.addRemoteRoot(remoteRoots);
        for (Artifact spoonlet : spoonletArtifacts) {
            artifactRepo.load(spoonlet);
        }
        return spoonletArtifacts;
    }

    private void loadDependencies(Artifact artifact, Collection<Artifact> dependencies) throws Exception {
        List<Artifact> children = artifact.getRuntimeDependencies();
        Iterator<Artifact> it = children.iterator();
        while (it.hasNext()) {
            Artifact dep = it.next();
            dependencies.add(dep);
            loadDependencies(dep, dependencies);
        }
    }

    protected ClassRealm defClassRealm(List<Artifact> spoonletArtifacts, List<String> dependencies) throws Exception {
        HashSet<Artifact> allArtifacts = new HashSet<Artifact>();
        for (Artifact spoonlet : spoonletArtifacts) {
            allArtifacts.add(spoonlet);
            loadDependencies(spoonlet, allArtifacts);
        }

        ClassWorld world = new ClassWorld();
        // use the existing ContextClassLoader in a realm of the classloading
        // space
        ClassRealm realm = world.newRealm("spoon.container", Thread.currentThread().getContextClassLoader());
        // create another realm for just the jars we have downloaded on-the-fly
        // and make
        // sure it is in a child-parent relationship with the current
        // ContextClassLoader
        ClassRealm spoonletsRealm = realm.createChildRealm("spoon.spoonlets");
        spoonletsRealm.setParent(realm);

        // add all the jars we just downloaded to the new child realm
        spoonletsRealm.addConstituent(env_.getDefaultFileGenerator().getOutputDirectory().toURL());
        List<URL> alreadyInLoader = ClasspathHelper.scan(Thread.currentThread().getContextClassLoader(), false);
        List<File> fileInLoader = new ArrayList<File>(alreadyInLoader.size());
        for (URL url : alreadyInLoader) {
            File file = new File(url.getFile());
            log("thread.classLoader :" + file.getName() + " ("+ file +")");
            fileInLoader.add(file);
        }

        // regexp to ignore other spoon and spoon-core jar
        Pattern pattern = Pattern.compile("^spoon-(core-)?\\d.*");
        for (Artifact artifact : allArtifacts) {
            File file = artifact.getJar();
            if (!fileInLoader.contains(file) && !pattern.matcher(file.getName()).find()) {
                log("spoonlets.add :" + file.getName() + " ("+ file +")");
                spoonletsRealm.addConstituent(file.toURL());
                fileInLoader.add(file);
            } else {
                log("spoonlets.ignored (already in) :" + file.getName() + " ("+ file +")");
            }
        }

        for (String dep : dependencies) {
            File file = new File(dep);
            if (!fileInLoader.contains(file) && !pattern.matcher(file.getName()).find()) {
                log("compile.add :" + file.getName() + " ("+ file +")");
                spoonletsRealm.addConstituent(file.toURL());
                fileInLoader.add(file);
            } else {
                log("compile.ignored (already in) :" + file.getName() + " ("+ file +")");
            }
        }

        return spoonletsRealm;
    }

    private void log(String str) throws Exception {
        env_.debugMessage(str);
        //System.out.println(str);
    }

}
