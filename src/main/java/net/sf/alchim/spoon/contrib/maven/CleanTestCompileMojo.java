package net.sf.alchim.spoon.contrib.maven;

import java.util.List;

/**
 * Restore sourceDirectory to original.
 *
 * @goal test-clean
 * @phase package
 *
 * @author dwayne
 */
public class CleanTestCompileMojo extends AbstractCleanMojo {

    @SuppressWarnings({ "unchecked", "cast" })
    @Override()
    protected List<String> getSourceRoots() throws Exception {
        return (List<String>)project.getTestCompileSourceRoots();
    }

}
