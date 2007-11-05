package net.sf.alchim.spoon.contrib.launcher.artifact;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class DomHelper {

    protected static Node read(InputStream xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(xml).getDocumentElement();
    }

    protected static Node read(File xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(xml).getDocumentElement();
    }

    /**
     * Simple value finder
     *
     * @param fromnode node from when we start deep search
     * @param name the node name
     * @return the value of the first matching node or null
     */
    protected static String findFirstValue(Node fromnode, String name) {
        Node node = findFirst(fromnode, name);
        return (node == null)?null:node.getFirstChild().getNodeValue().trim();
    }

    /**
     * Simple value finder
     *
     * @param fromnode node from when we start deep search
     * @param name the node name
     * @return the value of the first matching node or null
     */
    protected static Element findFirst(Node fromnode, String name) {
        NodeList nodelist = fromnode.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node node = nodelist.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (name.equals(node.getNodeName())) {
                    return (Element)node;
                }
                Element el = findFirst(node, name);
                if (el != null) {
                    return el;
                }
            }
        }
        return null;
    }

    /**
     * Simple DOM elements finder.
     *
     * @param fromnode node from when we start deep search
     * @param name the node name to search
     * @return the list of elements matching the seeken name
     */
    protected static List<Element> findElements(Node fromnode, String name) {
        NodeList nodelist = fromnode.getChildNodes();
        List<Element> list = new ArrayList<Element>();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node node = nodelist.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (name.equals(node.getNodeName())) {
                    list.add((Element)node);
                }
                list.addAll(findElements(node, name));
            }
        }
        return list;
    }
}
