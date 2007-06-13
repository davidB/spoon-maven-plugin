package net.sf.alchim.spoon.contrib.maven;

import java.io.File;
import java.util.List;

/**
 * Apply a set of spoonlet on test source.
 *
 * @goal test-generate
 * @phase generate-test-sources
 * @requiresDependencyResolution test
 */
public class SpoonTestGenSrcMojo extends AbstractSpoonMojo {
    /**
     * The directory for generated java files.
     *
     * @parameter expression="${project.build.directory}/generated-test-sources/spoon"
     * @required
     * @readonly
     */
    private File srcOutputDirectory;

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
        return null;
    }

}
