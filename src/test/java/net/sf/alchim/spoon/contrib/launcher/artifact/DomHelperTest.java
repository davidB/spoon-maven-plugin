package net.sf.alchim.spoon.contrib.launcher.artifact;

import net.sf.alchim.spoon.contrib.launcher.artifact.DomHelper;

import org.w3c.dom.Node;


import junit.framework.TestCase;

public class DomHelperTest extends TestCase{

    public void testFindFirstValue() throws Exception {
        Node doc = DomHelper.read(this.getClass().getResourceAsStream("sample1.xml"));
        assertEquals("value1", DomHelper.findFirstValue(doc, "node1"));
        assertEquals("value3.1", DomHelper.findFirstValue(doc, "node2"));
    }
}
