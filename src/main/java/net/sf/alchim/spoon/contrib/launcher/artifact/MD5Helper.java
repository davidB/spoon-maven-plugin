package net.sf.alchim.spoon.contrib.launcher.artifact;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

class MD5Helper {

    public static byte[] createChecksum(File filename) throws Exception {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filename);
            byte[] buffer = IOUtils.toByteArray(fis);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return md5.digest(buffer);
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    public static String getMD5Checksum(File file) throws Exception {
        byte[] b = createChecksum(file);
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static boolean checksum(File fileToCheck, File expectedMD5File) throws Exception {
        String actualMD5 = getMD5Checksum(fileToCheck);
        String expectedMD5 = FileUtils.readFileToString(expectedMD5File, "UTF-8");
        return expectedMD5.equals(actualMD5);
    }
}
