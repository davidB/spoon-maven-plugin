package net.sf.alchim.spoon.contrib.maven;

import java.io.File;
import java.util.List;

/**
 * Apply a set of spoonlet on main source and generate the java files.
 * @goal generate
 * @phase generate-sources
 * @requiresDependencyResolution compile
 */
public class SpoonGenSrcMojo extends AbstractSpoonMojo {
    /**
     * The directory for generated java files.
     *
     * @parameter expression="${project.build.directory}/generated-sources/net.sf.alchim.spoon"
     * @required
     * @readonly
     */
    private File outputDirectory;

    /**
     * Set to false, if you don't want to generate java sources file
     * (usefull, if only use spoonlet as Analyzer).
     *
     * @parameter expression="${net.sf.alchim.spoon.generate-sources}" default="true"
     * @required
     * @readonly
     */
    private boolean generateSources;

    @SuppressWarnings("unchecked")
    @Override
    protected List<String> getSourceRoots() throws Exception {
        return (List<String>)project.getCompileSourceRoots();
    }

    @Override
    protected File getOutputDir() throws Exception {
        if (generateSources) {
            return outputDirectory;
        }
        return null;
    }
}
