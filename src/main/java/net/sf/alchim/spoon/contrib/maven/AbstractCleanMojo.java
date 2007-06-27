    package net.sf.alchim.spoon.contrib.maven;


import net.sf.alchim.spoon.contrib.launcher.Launcher;
import net.sf.alchim.spoon.contrib.misc.PathHelper;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;



import java.util.List;

/**
 * Restore sourceDirectory to original.
 *
 * @author dwayne
 */
public abstract class AbstractCleanMojo extends AbstractMojo {

    abstract protected List<String> getSourceRoots() throws Exception;

    public void execute() throws MojoExecutionException {
        try {
            String pathList = System.getProperty(Launcher.SRC_ROOTS);
            if (pathList == null) {
                getLog().info("nothing to restore");
                return;
            }
            getLog().info("restore source directories to original");
            List<String> l = getSourceRoots();
            if ( l != null ) {
                l.clear();
                l.addAll(PathHelper.split(pathList));
            }
        } catch (Exception exc) {
            throw new MojoExecutionException("fail to execute", exc);
        }
    }

}
