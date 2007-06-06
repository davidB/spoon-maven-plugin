package net.sf.alchim.spoon.contrib.maven;

import java.io.File;
import java.util.List;

import spoon.processing.Environment;
import spoon.processing.FileGenerator;
import spoon.support.JavaOutputProcessor;

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
     * @parameter expression="${project.build.directory}/generated-sources/spoon"
     * @required
     * @readonly
     */
    private File outputSrcDirectory;

    /**
     * The directory for generated java files.
     *
     * @parameter expression="${project.build.directory}/classes"
     * @required
     * @readonly
     */
    private File outputDirectory;

    @SuppressWarnings("unchecked")
    @Override
    protected List<String> getSourceRoots() throws Exception {
        return project.getCompileSourceRoots();
    }

    @Override
    protected File getOutputDir() throws Exception {
        return outputSrcDirectory;
    }

    @Override
    protected Environment newEnvironment() throws Exception {
        Environment environment = super.newEnvironment();
        FileGenerator<?> printer = environment.getDefaultFileGenerator();
        environment.setDefaultFileGenerator(new MyByteCodeOutputProcessor((JavaOutputProcessor) printer, outputDirectory));
        return environment;
    }

    @Override
    protected void updateSourceRoots() throws Exception {
    }

}
