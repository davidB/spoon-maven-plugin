/*
 * BSD License http://pomstrap.prefetch.com/bsd-license.html
 * Copyright (c) 2005, POMStrap Project http://pomstrap.prefetch.com
 * All rights reserved.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the POMStrap project. For more
 * information on the POMStrap project, please see
 * http://pomstrap.prefetch.com
 */
package net.sf.alchim.spoon.contrib.launcher.artifact;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


/**
 * A representation of a Maven2 dependency as described in the project file
 * (pom.xml). Hold method to scan the target dependency related pom.xml file,
 * and by the way, its dependency (if any). The dependency pom.xml file could be
 * in local repository or in a remote (if maven2.repository.url property is
 * provided). The xml is loaded and analysed using basic DOM method provided in
 * current JDK.
 * 
 * @author AlAg
 * @author dwayne
 * @based on pomstart
 */
public class Artifact {
    private String groupId_;

    private String artifactId_;

    private String version_;

    protected Pom pom;

    protected File jar;

    protected File md5;

    protected Repository repository;

    private List<Artifact> childs;

    /**
     * Simple constructor
     */
    public Artifact(String groupId, String artifactId, String version) {
        this.groupId_ = groupId;
        this.artifactId_ = artifactId;
        this.version_ = version;
    }

    /**
     * Simple constructor
     * 
     * @param dependency
     *            Maven2 dependency groupId:artifactId:version (by default scope
     *            is null)
     * @throws Exception
     */
    public Artifact(String groupArtifactVersion) throws Exception {
        StringTokenizer tokenizer = new StringTokenizer(groupArtifactVersion, ":");
        if (tokenizer.countTokens() != 3) {
            throw new IllegalArgumentException("Invalid groupArtifactVersion (groupId:artefactId:version expected): " + groupArtifactVersion);
        }
        this.groupId_ = tokenizer.nextToken();
        this.artifactId_ = tokenizer.nextToken();
        this.version_ = tokenizer.nextToken();
    }

    /**
     * Provides all dependencies (if any) of this dependency. The child list is
     * computed once according to pom file (at first call). Next call will
     * return the same list instance. This list can then be changed to customize
     * the dependency list.
     * 
     * (Artifact from the list are loaded by the same repository)
     * 
     * @return a list of dependencies
     * @throws Exception
     */
    public List<Artifact> getRuntimeDependencies() throws Exception {
        if (childs == null) {
            childs = new ArrayList<Artifact>();
            childs.add(this);
            addRuntimeDependencies(childs);
            childs.remove(this);
        }
        return childs;
    }

    private void addRuntimeDependencies(List<Artifact> list) throws Exception {
        List<Artifact> dependencies = pom.findDirectDependencies();
        for (Artifact dependency : dependencies) {
            repository.load(dependency);
            if (!list.contains(dependency)) {
                list.add(dependency);
                dependency.addRuntimeDependencies(list);
            }
        }
    }

    /*
     * Getter list
     */

    public String getArtifactId() {
        return artifactId_;
    }

    public String getGroupId() {
        return groupId_;
    }

    public String getVersion() {
        return version_;
    }

    public File getJar() throws Exception {
        if (jar == null) {
            throw new IllegalStateException("artifact " + this + " not loaded from a Repository!");
        }
        return jar;
    }

    public URL[] getJarURL() throws Exception {
        return new URL[] { getJar().toURL() };
    }

    public String toString() {
        return groupId_ + ':' + artifactId_ + ':' + version_;
    }

    public int hashCode() {
        return toString().hashCode();
    }

    public boolean equals(Object o) {
        return toString().equals(o.toString());
    }

    public void relocate(String newGroupId) throws Exception {
        if (groupId_.equals(newGroupId)){
            // nothing to do
            return;
        }
        jar = null;
        pom = null;
        md5 = null;
        childs = null;
        groupId_ = newGroupId;
    }
}
