package net.sf.alchim.spoon.contrib.misc;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

public class ClasspathHelper {

    public static void dump(ClassLoader cl, boolean scanJar) throws Exception {
        if (cl == null) {
            cl = Thread.currentThread().getContextClassLoader();
        }
        System.out.println("java.class.path " + System.getProperty("java.class.path"));
        System.out.println("dump list of resources available from " + cl + "(scanJar :" + scanJar +")");
        List<URL> l = scan(cl, scanJar);
        for(URL url : l) {
            System.out.println("\t" + url);
        }
    }

    public static void setClasspathProperty(ClassLoader cl) throws Exception {
        System.setProperty("java.class.path", toClasspathString(cl));
    }

    public static String toClasspathString(ClassLoader cl) throws Exception {
        if (cl == null) {
            cl = Thread.currentThread().getContextClassLoader();
        }
        StringBuilder back = new StringBuilder();
        while (cl != null) {
            if (cl instanceof URLClassLoader) {
                URLClassLoader ucl = (URLClassLoader) cl;
                URL[] urls = ucl.getURLs();
                for (URL url: urls) {
                    if (back.length() != 0) {
                        back.append(File.pathSeparatorChar);
                    }
                    back.append(url.getFile());
                }
            }
            cl = cl.getParent();
        }
        return back.toString();
    }

    public static List<URL> scan(ClassLoader cl, boolean scanJar) throws Exception {
        if (cl == null) {
            cl = Thread.currentThread().getContextClassLoader();
        }
        List<URL> resources = new ArrayList<URL>();
        while (cl != null) {
            if (cl instanceof URLClassLoader) {
                URLClassLoader ucl = (URLClassLoader) cl;
                URL[] urls = ucl.getURLs();
                for (URL url: urls) {
                    resources.add(url);
                    if (url.getFile().endsWith(".jar")) {
                        if (scanJar) {
                            listJarResources(new URL("jar:" + url.toExternalForm() + "!/"), resources);
                        }
                    } else if (url.getProtocol().equals("file")) {
                        File file = new File(url.getFile());
                        if (file.isDirectory()) {
                            listDirResources(file, resources);
                        }
                    }
                }
            }
            cl = cl.getParent();
        }
        return resources;
    }

    private static void listDirResources(File dir, List<URL> resources) throws Exception {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            resources.add(file.toURL());
            if (file.isDirectory()) {
                listDirResources(file, resources);
            }
        }
    }

    private static void listJarResources(URL jarUrl, List<URL> resources) throws Exception {
        JarURLConnection jarConnection = (JarURLConnection) jarUrl.openConnection();

        for (Enumeration<JarEntry> entries = jarConnection.getJarFile().entries(); entries.hasMoreElements();) {
            JarEntry entry = entries.nextElement();
            resources.add(new URL(jarUrl, entry.getName()));
        }
    }

}
