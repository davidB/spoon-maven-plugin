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
     * @parameter expression="${maven.spoon.srcOutputDirectory}" default-value="${project.build.directory}/generated-sources/spoon"
     */
    protected File srcOutputDirectory;

    /**
     * Set to false, if you don't want to generate java sources file
     * (usefull, if only use spoonlet as Analyzer).
     *
     * @parameter expression="${maven.spoon.generateSources}" default-value="true"
     */
    protected boolean generateSources;

    @SuppressWarnings("unchecked")
    @Override
    protected List<String> getSourceRoots() throws Exception {
        return project.getCompileSourceRoots();
    }

    @Override
    protected File getSrcOutputDir() throws Exception {
        if (generateSources) {
            return srcOutputDirectory;
        }
        return null;
    }

    @Override
    protected File getClassesOutputDir() throws Exception {
        return null;
    }
}
