package net.sf.alchim.spoon.contrib.maven;


import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import spoon.support.JavaOutputProcessor;

import net.sf.alchim.spoon.contrib.launcher.Launcher;

/**
 * Apply a set of spoonlet and Spoon's processor.
 */
public abstract class AbstractSpoonMojo extends AbstractMojo {

    /**
     * Set the jdk compliance level.
     *
     * @parameter expression="${maven.spoon.compliance}" default-value="5"
     */
    protected int compliance = 5;

    /**
     * Set to true to include debugging information in the compiled class files.
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
     * @parameter expression="${maven.spoon.cfg}" default-value="${basedir}/spoon.cfg.xml"
     */
    protected File cfg;

    /**
     * Set to true to stop the build if spoon generated warnings
     *
     * @parameter expression="${maven.spoon.failOnWarning}" default-value="false"
     */
    protected boolean failOnWarning;

    /**
     * Set to true to stop the build if spoon generated warnings
     *
     * @parameter expression="${maven.spoon.failOnError}" default-value="true"
     */
    protected boolean failOnError;

    /**
     * @parameter expression="${project}"
     * @required
     */
    protected MavenProject project;

    abstract protected List<String> getSourceRoots() throws Exception;
    abstract protected File getOutputDir() throws Exception;

    @SuppressWarnings("unchecked")
    protected List<String> getCompileDependencies() throws Exception {
        return project.getCompileClasspathElements();
    }

    public void execute() throws MojoExecutionException {
        try {
            executeBasic();
            updateSourceRoots();
        } catch (MojoExecutionException exc) {
            throw exc;
        } catch (Throwable exc) {
            throw new MojoExecutionException("fail to execute", exc);
        }
    }

    protected MavenEnvironment newEnvironment() throws Exception {
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
        MavenEnvironment env = newEnvironment();
        launcher.run(cfg, getSourceRoots(), getCompileDependencies(), env);
        if (failOnError && env.hasError()) {
            throw new MojoExecutionException("spoon generate some errors");
        }
        if (failOnWarning && env.hasWarning()) {
            throw new MojoExecutionException("spoon generate some warnings");
        }
    }

}
