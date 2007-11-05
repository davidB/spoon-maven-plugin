package net.sf.alchim.spoon.contrib.maven;

import java.util.List;

import org.apache.maven.project.MavenProject;

/**
 * Restore sourceDirectory to original.
 *
 * @goal test-clean
 * @phase package
 *
 * @author dwayne
 */
public class CleanTestCompileMojo extends AbstractCleanMojo {

    /**
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    @SuppressWarnings({ "unchecked", "cast" })
    @Override()
    protected List<String> getSourceRoots() throws Exception {
        return (List<String>)project.getTestCompileSourceRoots();
    }

}
