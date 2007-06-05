package spoon.contrib.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import spoon.support.JavaOutputProcessor;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
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
            List<URL> urls = ClasspathHelper.scan(Thread.currentThread().getContextClassLoader(), false);
            for (String path : classpathElements) {
                URL url = new File(path).toURL();
                if (!urls.contains(url)) {
                    urls.add(url);
                }
            }
            ClassLoader cl = new URLClassLoader(urls.toArray(new URL[urls.size()]), null);
            Thread.currentThread().setContextClassLoader(cl);

            // environment initialization
            final MavenEnvironment environment = new MavenEnvironment(getLog());
            environment.setComplianceLevel(compliance);
            environment.setVerbose(verbose || debug);
            environment.setDebug(debug);
            // env_.setXmlRootFolder(getArguments().getFile("properties"));
            environment.setDefaultFileGenerator(new JavaOutputProcessor(outputDirectory));

            Launcher launcher = new Launcher(compileSourceRoots, processors, environment);
            //Class clazz = cl.loadClass("spoon.contrib.maven.Launcher");
            //Runnable launcher = (Runnable) clazz.newInstance();
            //Object launcher = clazz.newInstance();
            /*
            Launcher launcher = new Launcher() {
                public void init() throws Exception {
                    this.sources = RunProcessorsMojo.this.compileSourceRoots;
                    this.processors = RunProcessorsMojo.this.processors;
                    this.spoonlets = RunProcessorsMojo.this.spoonlets;
                    this.env = environment;
                }
            };
            */
            if (getLog().isDebugEnabled()) {
                System.err.println("cl :" + cl + " // " + launcher.getClass().getClassLoader() + " // " + Thread.currentThread().getContextClassLoader());
                ClasspathHelper.dump(launcher.getClass().getClassLoader(), false);
            }
            launcher.run();
            /*
            for (Method m: clazz.getDeclaredMethods()) {
                System.err.println("method :" + m.toString());
            }
            Method m = clazz.getMethod("run", new Class[]{cl.loadClass(List.class.getName()), cl.loadClass(List.class.getName()), cl.loadClass(Environment.class.getName())});
            m.invoke(launcher, new Object[]{compileSourceRoots, processors, environment});
            */
            /*
            Thread run = new Thread(new Runnable(){
               public void run() {
                   spoon.contrib.maven.Launcher launcher = new spoon.contrib.maven.Launcher();
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
        } catch (Exception exc) {
            throw new MojoExecutionException("fail to execute", exc);
        }
    }

}
