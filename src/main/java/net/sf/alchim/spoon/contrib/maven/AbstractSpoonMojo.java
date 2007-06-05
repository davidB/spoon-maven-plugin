package net.sf.alchim.spoon.contrib.maven;


import net.sf.alchim.spoon.contrib.launcher.Launcher;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import spoon.processing.Environment;
import spoon.support.JavaOutputProcessor;

import java.io.File;
import java.util.List;

/**
 * Apply a set of spoonlet and Spoon's processor.
 */
public abstract class AbstractSpoonMojo extends AbstractMojo {

    /**
     * Set the jdk compliance level.
     * The default value is 5.
     *
     * @parameter expression="${maven.spoon.compliance}" default-value="5"
     */
    protected int compliance = 5;

    /**
     * Set to true to include debugging information in the compiled class files.
     * The default value is true.
     *
     * @parameter expression="${maven.spoon.debug}" default-value="false"
     */
    protected boolean debug;

    /**
     * Set to true to show messages about what the compiler is doing.
     *
     * @parameter expression="${maven.spoon.verbose}" default-value="true"
     */
    protected boolean verbose;

    /**
     * Set to true to show messages about what the compiler is doing.
     *
     * @parameter expression="${maven.spoon.cfg}" default-value="${basedir}/net.sf.alchim.spoon.cfg.xml"
     */
    protected File cfg;
    
    /**
     * @parameter expression="${project}"
     * @required
     */
    protected MavenProject project;
    
    abstract protected List<String> getSourceRoots() throws Exception;
    abstract protected File getOutputDir() throws Exception;
    
    @SuppressWarnings("unchecked")
    protected List<String> getCompileDependencies() throws Exception {
        return (List<String>) project.getCompileClasspathElements();
    }

    public void execute() throws MojoExecutionException {
        try {
            executeBasic();
            updateSourceRoots();
        } catch (Throwable exc) {
            throw new MojoExecutionException("fail to execute", exc);
        }
    }

    protected Environment newEnvironment() throws Exception {
        MavenEnvironment environment = new MavenEnvironment(getLog());
        environment.setComplianceLevel(compliance);
        environment.setVerbose(verbose || debug);
        environment.setDebug(debug);
        File outputdir = getOutputDir();
        if (outputdir != null) {
            // env_.setXmlRootFolder(getArguments().getFile("properties"));
            environment.setDefaultFileGenerator(new JavaOutputProcessor(outputdir));
        }
        return environment;
    }

    protected void updateSourceRoots() throws Exception {
        List<String> l = getSourceRoots();
        if ((getOutputDir() != null) && (l != null)) {
            l.clear();
            l.add(getOutputDir().getAbsolutePath());
        }
    }
    
    private void executeBasic() throws Throwable {
        Launcher launcher = new Launcher();
        launcher.run(cfg, getSourceRoots(), getCompileDependencies(), newEnvironment());
    }

}
