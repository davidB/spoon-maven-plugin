package net.sf.alchim.spoon.contrib.maven;

import java.io.File;
import java.util.List;

/**
 * Apply a set of spoonlet on test source.
 *
 * @goal test-recompile
 * @phase test-compile
 * @requiresDependencyResolution test
 */
public class SpoonTestCompileMojo extends AbstractSpoonMojo {
    /**
     * The directory for generated java files.
     *
     * @parameter expression=="${maven.spoon.test.srcOutputDirectory}" default-value="${project.build.directory}/generated-test-sources/spoon"
     */
    protected File srcOutputDirectory;

    /**
     * The directory for generated java files.
     *
     * @parameter expression="${maven.spoon.test.classesOutputDirectory}" default-value="${project.build.directory}/test-classes"
     */
    protected File classesOutputDirectory;


    @SuppressWarnings("unchecked")
    @Override()
    protected List<String> getSourceRoots() throws Exception {
        return project.getTestCompileSourceRoots();
    }

    @Override
    protected File getSrcOutputDir() throws Exception {
        return srcOutputDirectory;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<String> getCompileDependencies() throws Exception {
        return project.getTestClasspathElements();
    }

    @Override
    protected File getClassesOutputDir() throws Exception {
        return classesOutputDirectory;
    }

}
