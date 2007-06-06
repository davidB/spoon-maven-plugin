package net.sf.alchim.spoon.contrib.launcher.artifact;

import java.io.File;

import junit.framework.TestCase;

public class MD5HelperTest extends TestCase {
    public void testCheckFromExistingFile() throws Exception {
        String basefile = System.getProperty("user.home") + "/.m2/repository/fr/inria/gforge/spoon/spoon-core/1.2/spoon-core-1.2.jar";
        assertTrue(MD5Helper.checksum(new File(basefile), new File(basefile + ".md5")));
    }
}
