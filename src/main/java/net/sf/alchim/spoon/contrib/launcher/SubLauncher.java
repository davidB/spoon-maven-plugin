package net.sf.alchim.spoon.contrib.launcher;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import spoon.processing.Builder;
import spoon.processing.Environment;
import spoon.processing.ProcessingManager;
import spoon.processing.Processor;
import spoon.reflect.Factory;
import spoon.support.DefaultCoreFactory;
import spoon.support.QueueProcessingManager;
import spoon.support.builder.CtFile;
import spoon.support.builder.CtFolder;
import spoon.support.builder.CtResource;
import spoon.support.builder.SpoonBuildingManager;
import spoon.support.builder.support.CtFileFile;
import spoon.support.builder.support.CtFolderFile;
import spoon.support.builder.support.CtFolderZip;

/**
 * Apply a set of spoonlet and Spoon's processor.
 */
public class SubLauncher {

    private List<String> sources_ = new ArrayList<String>();

    private final List<String> processors_ = new ArrayList<String>();

    private Environment env_;

    private final HashSet<CtResource> ctTemplates_ = new HashSet<CtResource>();

    private Factory factory_;

    /**
     * initialize and run a LauncherImpl.
     *
     * @param sources list of the source directories containing the sources to
     *            be processed.
     * @param processors List of processors' class to apply.
     * @param env the running environnment
     * @throws Exception
     */
    public void run(List<String> sources, Environment env, File cfg, List<File> spoonlets) throws Throwable {
        sources_ = new ArrayList<String>();
        sources_.addAll(sources);
        env_ = env;
        factory_ = new Factory(new DefaultCoreFactory(), env);
        env_.setFactory(factory_);
        if (env_.getDefaultFileGenerator() != null) {
            env_.getDefaultFileGenerator().setFactory(factory_);
        }

        configureSpoonlets(cfg, spoonlets);
        build();
        process();

        env_.reportEnd();
    }

    protected void build() throws Exception {
        String oldClasspath = System.getProperty("java.class.path");
        try {
            // align the classpath system property to the current classloader
            // (needed to configure eclise jdtcompiler out from Eclipse IDE)
            // ClasspathHelper.setClasspathProperty(null);
            // String classpath = ClasspathHelper.toClasspathString(null);
            // System.setProperty("java.class.path", classpath);
            env_.debugMessage("classpath :" + System.getProperty("java.class.path"));
            Builder builder = new SpoonBuildingManager(factory_);
            for (String path : sources_) {
                File file = new File(path);
                CtResource src = (file.isDirectory()) ? new CtFolderFile(file) : new CtFileFile(file);
                builder.addInputSource(src);
            }
            for (CtResource f : ctTemplates_) {
                builder.addTemplateSource(f);
            }
            if (!builder.build()) {
                StringBuilder str = new StringBuilder();
                for(String v: builder.getProblems()) {
                    str.append("\n\t>> ").append(v);
                }
                throw new Exception("pre build failed :" + str.toString());
            }
        } finally {
            System.setProperty("java.class.path", oldClasspath);
        }
    }

    /**
     * Processes the built model with the processors.
     */
    protected void process() throws Exception {
        env_.debugMessage("process model");
        // processing (consume all the processors)
        ProcessingManager processing = new QueueProcessingManager(factory_);
        for (String processorName : processors_) {
            env_.debugMessage("loading processor " + processorName);
            // processing.addProcessor((Processor) Class.forName(processorName,
            // true,
            // Thread.currentThread().getContextClassLoader()).newInstance());
            Processor<?> p = (Processor<?>) Thread.currentThread().getContextClassLoader().loadClass(processorName).newInstance();
            processing.addProcessor(p);
            // processing.addProcessor(processorName);
        }
        if (env_.getDefaultFileGenerator() != null) {
            processing.addProcessor(env_.getDefaultFileGenerator());
        }
        processing.process();
    }

    /**
     * Load content of spoonlet file (template and processor list).
     */
    protected void configureSpoonlets(File cfg, List<File> spoonletArtifacts) throws Exception {
        for (File spoonlet : spoonletArtifacts) {
            searchSpoonlets(spoonlet);
        }
        // override previous processors and templates configuration
        if ((cfg != null) && cfg.exists()) {
            SpoonletXmlHandler.load(new CtFileFile(cfg), factory_, processors_, ctTemplates_, null);
        }
    }

    /**
     * Load content of spoonlet file (template and processor list).
     */
    protected void searchSpoonlets(File jar) throws Exception {
        env_.debugMessage("search spoonlets from :" + jar);
        CtFolder folder = null;
        if (jar.isFile()) {
            folder = new CtFolderZip(jar);
        } else {
            folder = new CtFolderFile(jar);
        }
        List<CtFile> spoonletIndex = new ArrayList<CtFile>();
        CtFile configFile = null;
        for (CtFile file : folder.getAllFiles()) {
            if (file.isJava()) {
                spoonletIndex.add(file);
            } else if (file.getName().endsWith("spoon.xml")) {
                // Loading spoonlet properties
                configFile = file;
            }
        }
        if (configFile != null) {
            SpoonletXmlHandler.load(configFile, factory_, processors_, ctTemplates_, spoonletIndex);
        }
    }

}
