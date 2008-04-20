package net.sf.alchim.spoon.contrib.maven;

import java.util.List;

/**
 * Restore sourceDirectory to original.
 * @goal clean
 * @phase compile
 *
 * @author dwayne
 */
public class CleanCompileMojo extends AbstractCleanMojo {

    @SuppressWarnings({ "unchecked", "cast" })
    @Override
    protected List<String> getSourceRoots() throws Exception {
        return (List<String>)project.getCompileSourceRoots();
    }

}
