package net.sf.alchim.spoon.contrib.launcher.artifact;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

class UrlHelper {

    // TODO
    public static boolean exists(URL url) throws Exception {
        if ("http".equals(url.getProtocol())) {
            HttpURLConnection cnx = (HttpURLConnection)url.openConnection();
            return (HttpURLConnection.HTTP_OK == cnx.getResponseCode());
        } else if ("file".equals(url.getProtocol())) {
            File f = new File(url.getPath());
            return f.exists();
        }
        throw new UnsupportedOperationException("only 'http' and 'file' protocol supported :" + url.getProtocol());
    }

    public static void download(URL src, File dest) throws Exception {
        System.out.println("download :" + src);
        OutputStream out = null;
        InputStream in = null;
        try {
            out = new FileOutputStream(dest);
            URLConnection cnx = src.openConnection();
            in = cnx.getInputStream();
            IOUtils.copy(in, out);
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }
}
