package net.sf.alchim.spoon.contrib.maven;

import java.io.File;
import java.util.List;

/**
 * Apply a set of spoonlet on test source.
 * 
 * @goal test-run
 * @phase generate-test-sources
 * @requiresDependencyResolution test
 */
public class SpoonTestCompileMojo extends AbstractSpoonMojo {
    /**
     * The directory for generated java files.
     * 
     * @parameter expression="${project.build.directory}/generated-test-sources/net.sf.alchim.spoon"
     * @required
     * @readonly
     */
    private File outputDirectory;

    @SuppressWarnings("unchecked")
    @Override()
    protected List<String> getSourceRoots() throws Exception {
        return (List<String>) project.getTestCompileSourceRoots();
    }

    @Override
    protected File getOutputDir() throws Exception {
        return outputDirectory;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<String> getCompileDependencies() throws Exception {
        return (List<String>) project.getTestClasspathElements();
    }

}
