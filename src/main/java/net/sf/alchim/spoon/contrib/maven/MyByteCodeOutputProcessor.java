package net.sf.alchim.spoon.contrib.maven;

import java.io.File;

import spoon.reflect.Factory;
import spoon.support.ByteCodeOutputProcessor;
import spoon.support.JavaOutputProcessor;

public class MyByteCodeOutputProcessor extends ByteCodeOutputProcessor{

    private final JavaOutputProcessor javaPrinter_;

    public MyByteCodeOutputProcessor(JavaOutputProcessor javaPrinter, File outputDirectory) {
        super(javaPrinter, outputDirectory);
        javaPrinter_ = javaPrinter;
    }

    public void updateFactory(Factory v) throws Exception {
        super.setFactory(v);
        javaPrinter_.setFactory(v);
    }

}
