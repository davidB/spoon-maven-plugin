package net.sf.alchim.spoon.contrib.misc;

import java.io.InputStream;

import spoon.support.builder.CtFile;
import spoon.support.builder.CtFolder;

public class CtFile4ResourceStream implements CtFile {

    private String name_;
    private String rpath_;

    public CtFile4ResourceStream(Class clazz) throws Exception {
        this(clazz.getName());
    }

    public CtFile4ResourceStream(String name) throws Exception {
        this(name, name.replace('.', '/')+".java");
    }

    public CtFile4ResourceStream(String name, String rpath) throws IllegalArgumentException {
        if (name == null) {
            name = rpath.substring(0, rpath.indexOf(".java")).replace('\\', '/').replace('/', '.');
        }
        if (Thread.currentThread().getContextClassLoader().getResource(rpath) == null) {
            if (Thread.currentThread().getContextClassLoader().getResource(rpath+".txt") == null) {
                throw new IllegalArgumentException("resource not found for :" + rpath);
            } else {
                rpath = rpath +".txt";
            }
        }
        rpath_ = rpath;
        name_ = name;
    }

    public InputStream getContent() {
        InputStream back = Thread.currentThread().getContextClassLoader().getResourceAsStream(rpath_);
        if (back == null) {
            throw new IllegalStateException("content is null for " + rpath_);
        }
        return back;
    }

    public boolean isJava() {
        return true;
    }

    public String getName() {
        String qname = name_;
        qname = qname.replace('.', '/');
        qname = qname.replace('\\', '/');
        qname = qname + ".java";
        return qname;
    }

    public String getPath() {
        return getName();
    }
    
    public CtFolder getParent() {
        return null;
    }

    public boolean isFile() {
        return true;
    }

}
