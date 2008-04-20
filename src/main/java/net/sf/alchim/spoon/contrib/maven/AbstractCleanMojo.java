    package net.sf.alchim.spoon.contrib.maven;


import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import net.sf.alchim.spoon.contrib.launcher.Launcher;
import net.sf.alchim.spoon.contrib.misc.PathHelper;

/**
 * Restore sourceDirectory to original.
 *
 * @author dwayne
 */
public abstract class AbstractCleanMojo extends AbstractMojo {
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

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
