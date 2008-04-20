package net.sf.alchim.spoon.contrib.maven;

import java.io.File;
import java.util.List;

/**
 * Apply a set of spoonlets after compilation of original java, and recompile the resulting java files.
 *
 * @goal recompile
 * @phase compile
 * @requiresDependencyResolution compile
 */
public class SpoonCompileMojo extends AbstractSpoonMojo {
    /**
     * The directory for generated java files.
     *
     * @parameter expression="${maven.spoon.srcOutputDirectory}" default-value="${project.build.directory}/generated-sources/spoon"
     */
    protected File srcOutputDirectory;

    /**
     * The directory for generated java files.
     *
     * @parameter expression="${maven.spoon.classesOutputDirectory}" default-value="${project.build.directory}/classes"
     */
    protected File classesOutputDirectory;

    @SuppressWarnings("unchecked")
    @Override
    protected List<String> getSourceRoots() throws Exception {
        return project.getCompileSourceRoots();
    }

    @Override
    protected File getSrcOutputDir() throws Exception {
        return srcOutputDirectory;
    }

    @Override
    protected void updateSourceRoots() throws Exception {
    }

    @Override
    protected File getClassesOutputDir() throws Exception {
        return classesOutputDirectory;
    }

}
