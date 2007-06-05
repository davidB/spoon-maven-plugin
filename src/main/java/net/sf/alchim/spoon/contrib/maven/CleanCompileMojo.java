package net.sf.alchim.spoon.contrib.maven;

import java.util.List;

import org.apache.maven.project.MavenProject;

/**
 * Restore sourceDirectory to original.
 * @goal clean 
 * @phase compile
 * 
 * @author dwayne
 */
public class CleanCompileMojo extends AbstractCleanMojo {

    /**
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    @SuppressWarnings("unchecked")
    @Override
    protected List<String> getSourceRoots() throws Exception {
        return (List<String>)project.getCompileSourceRoots();
    }

}