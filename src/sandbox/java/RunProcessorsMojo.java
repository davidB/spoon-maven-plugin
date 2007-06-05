package spoon.contrib.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.codehaus.classworlds.ClassRealm;

import spoon.processing.Environment;
import spoon.support.JavaOutputProcessor;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Apply a set of spoonlet and Spoon's processor.
 *
 * @goal run
 * @requiresDependencyResolution compile
 */
public class RunProcessorsMojo extends AbstractMojo {

    /**
     * The source directories containing the sources to be compiled.
     *
     * @parameter expression="${project.compileSourceRoots}"
     * @required
     * @readonly
     */
    private List<String> compileSourceRoots;

    /**
     * Project classpath.
     *
     * @parameter expression="${project.compileClasspathElements}"
     * @required
     * @readonly
     */
    private List<String> classpathElements;

    /**
     * The directory for compiled classes.
     *
     * @parameter expression="${project.build.directory}/generated-sources"
     * @required
     * @readonly
     */
    private File outputDirectory;

    /**
     * List of processors' class to apply.
     *
     * @parameter
     */
    public List<String> processors = new ArrayList<String>();

    public int compliance = 5;

    /**
     * Set to true to include debugging information in the compiled class files.
     * The default value is true.
     *
     * @parameter expression="${maven.spoon.debug}" default-value="false"
     */
    private boolean debug;

    /**
     * Set to true to show messages about what the compiler is doing.
     *
     * @parameter expression="${maven.spoon.verbose}" default-value="true"
     */
    private boolean verbose;

    public void execute() throws MojoExecutionException {
        try {
            for (Object key : getPluginContext().keySet()) {
                System.err.println("key : " + key);
            }
            executeBasic();
        } catch (Exception exc) {
            throw new MojoExecutionException("fail to execute", exc);
        }
    }

    private Environment newEnvironment() throws Exception {
        MavenEnvironment environment = new MavenEnvironment(getLog());
        environment.setComplianceLevel(compliance);
        environment.setVerbose(verbose || debug);
        environment.setDebug(debug);
        // env_.setXmlRootFolder(getArguments().getFile("properties"));
        environment.setDefaultFileGenerator(new JavaOutputProcessor(outputDirectory));
        return environment;
    }

    private void executeBasic() throws Exception {
        Launcher launcher = new LauncherImpl();
        launcher.run(compileSourceRoots, processors, newEnvironment());
    }

    private void executeClassRealm() throws Exception {

        PluginDescriptor pluginDescriptor = (PluginDescriptor) getPluginContext().get("pluginDescriptor");
        ClassRealm currentRealm = pluginDescriptor.getClassRealm();

        List<URL> urls = Arrays.asList(currentRealm.getConstituents());

        //ClassRealm containerRealm    = world.newRealm( "container" );
        //ClassRealm spoonRealm = world.newRealm("spoon");
        //List<URL> urls = ClasspathHelper.scan(Thread.currentThread().getContextClassLoader(), false);
        /*
        for (URL url:urls) {
            spoonRealm.addConstituent(url);
        }
        for (String path : classpathElements) {
            URL url = new File(path).toURL();
            if (!urls.contains(url)) {
                spoonRealm.addConstituent(url);
            }
        }
        */
        Class launcherClass = currentRealm.loadClass("spoon.contrib.maven.LauncherImpl");
        Launcher launcher = (Launcher) launcherClass.newInstance();
        //ClassLoader cl = spoonRealm.getClassLoader();
        //Thread.currentThread().setContextClassLoader(cl);

        //ClassLoader cl = new URLClassLoader(urls.toArray(new URL[urls.size()]), null);
        //Thread.currentThread().setContextClassLoader(cl);

        // environment initialization
        final MavenEnvironment environment = new MavenEnvironment(getLog());
        environment.setComplianceLevel(compliance);
        environment.setVerbose(verbose || debug);
        environment.setDebug(debug);
        // env_.setXmlRootFolder(getArguments().getFile("properties"));
        environment.setDefaultFileGenerator(new JavaOutputProcessor(outputDirectory));

        //LauncherImpl launcher = new LauncherImpl();
        //Class clazz = cl.loadClass("spoon.contrib.maven.LauncherImpl");
        //Runnable launcher = (Runnable) clazz.newInstance();
        //Object launcher = clazz.newInstance();
        //Launcher launcher = (Launcher)clazz.newInstance();
        /*
        LauncherImpl launcher = new LauncherImpl() {
            public void init() throws Exception {
                this.sources = RunProcessorsMojo.this.compileSourceRoots;
                this.processors = RunProcessorsMojo.this.processors;
                this.spoonlets = RunProcessorsMojo.this.spoonlets;
                this.env = environment;
            }
        };
        */
        //if (getLog().isDebugEnabled()) {
            //System.err.println("cl :" + cl + " // " + launcher.getClass().getClassLoader() + " // " + Thread.currentThread().getContextClassLoader());
            System.err.println(launcher.getClass().getClassLoader() + " // " + Thread.currentThread().getContextClassLoader());
            ClasspathHelper.dump(launcher.getClass().getClassLoader(), false);
        //}
        launcher.run(compileSourceRoots, processors, environment);

        /*
        Method m = clazz.getMethod("run", new Class[]{cl.loadClass(List.class.getName()), cl.loadClass(List.class.getName()), cl.loadClass(Environment.class.getName())});
        m.invoke(launcher, new Object[]{compileSourceRoots, processors, environment});
        */
        /*
        Thread run = new Thread(new Runnable(){
           public void run() {
               spoon.contrib.maven.LauncherImpl launcher = new spoon.contrib.maven.LauncherImpl();
               System.err.println("cl :" + cl + " // " + launcher.getClass().getClassLoader() + " // " + Thread.currentThread().getContextClassLoader());
               launcher.sources = compileSourceRoots;
               launcher.processors = processors;
               launcher.spoonlets = spoonlets;
               launcher.env = environment;
               launcher.run();
           }
        });
        run.setContextClassLoader(cl);
        run.start();
        run.join();
        */
    }
}
