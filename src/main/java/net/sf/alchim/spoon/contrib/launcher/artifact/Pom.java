package net.sf.alchim.spoon.contrib.launcher.artifact;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

class Pom {
    private final File file_;

    private Node rootNode_; // lazy loading

    protected Pom(File pom) throws Exception {
        file_ = pom;
    }

    protected String findRelocation() throws Exception {
        Node relocation = DomHelper.findFirst(getRootNode(), "relocation");
        if (relocation != null) {
            String newGroupId = DomHelper.findFirstValue(relocation, "groupId");
            if (newGroupId != null) {
                return newGroupId;
            }
            throw new UnsupportedOperationException("only groupId relocation is supported");
        }
        return null;
    }

    private Node getRootNode() throws Exception {
        if (rootNode_ == null) {
            rootNode_ = DomHelper.read(file_);
        }
        return rootNode_;
    }

    private String findGroupId() throws Exception {
        return DomHelper.findFirstValue(getRootNode(), "groupId");
    }

    private String findVersion() throws Exception {
        return DomHelper.findFirstValue(getRootNode(), "version");
    }

    protected Artifact findParent() throws Exception {
        Element element = DomHelper.findFirst(getRootNode(), "parent");
        element.normalize();
        String groupId = DomHelper.findFirstValue(element, "groupId");
        String artifactId = DomHelper.findFirstValue(element, "artifactId");
        String version = DomHelper.findFirstValue(element, "version");
        return new Artifact(groupId, artifactId, version);
    }

    protected List<Artifact> findDirectDependencies() throws Exception {
        Element dependenciesNode = DomHelper.findFirst(getRootNode(), "dependencies");
        if (dependenciesNode == null) {
            return new ArrayList<Artifact>(0);
        }
        List<Element> elements = DomHelper.findElements(dependenciesNode, "dependency");
        List<Artifact> dependencyList = new ArrayList<Artifact>(elements.size());
        for (Element element : elements) {
            element.normalize();
            String scope = DomHelper.findFirstValue(element, "scope");
            if ("test".equals(scope) || "provided".equals(scope) || "system".equals(scope)) {
                continue;
            }

            String groupId = DomHelper.findFirstValue(element, "groupId");
            if (groupId.equals("${project.groupId}")) {
                groupId = findGroupId();
            }

            String artifactId = DomHelper.findFirstValue(element, "artifactId");

            String version = DomHelper.findFirstValue(element, "artifactId");
            if (version.equals("${project.version}")) {
                version = findVersion();
            } else if (null == version) {
                throw new IllegalStateException("no version for " + groupId + ':' + artifactId + " in POM (" + file_ + ") ");
            }
            Artifact dep = new Artifact(groupId, artifactId, version);
            dependencyList.add(dep);
        }
        return dependencyList;
    }

    public File getFile() {
        return file_;
    }
}
