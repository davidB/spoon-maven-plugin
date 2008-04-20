package net.sf.alchim.spoon.contrib.launcher.artifact;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

import org.apache.maven.doxia.util.FileUtil;
import org.codehaus.plexus.util.IOUtil;

class MD5Helper {

    public static byte[] createChecksum(File filename) throws Exception {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filename);
            byte[] buffer = IOUtil.toByteArray(fis);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return md5.digest(buffer);
        } finally {
            IOUtil.close(fis);
        }
    }

    public static String getMD5Checksum(File file) throws Exception {
        byte[] b = createChecksum(file);
        String result = "";
        for (byte element : b) {
            result += Integer.toString((element & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static boolean checksum(File fileToCheck, File expectedMD5File) throws Exception {
        String actualMD5 = getMD5Checksum(fileToCheck);
        String expectedMD5 = FileUtil.loadString(expectedMD5File.getAbsolutePath());//, "UTF-8");
        return expectedMD5.equals(actualMD5);
    }
}
