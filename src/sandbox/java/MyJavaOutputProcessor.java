package spoon.contrib.maven;

import java.io.File;

import spoon.reflect.declaration.CtSimpleType;
import spoon.support.JavaOutputProcessor;

public class MyJavaOutputProcessor extends JavaOutputProcessor {
    int count;

    public MyJavaOutputProcessor(File arg0) {
        super(arg0);
    }

    public void process(CtSimpleType<?> type) {
        System.err.println("count :" + count++);
        if (type.isTopLevel()) {
            System.err.println("create :" + type.getQualifiedName());
            createJavaFile(type);
        }
    }

}
