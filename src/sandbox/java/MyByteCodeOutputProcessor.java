package net.sf.alchim.spoon.contrib.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;

import spoon.processing.AbstractProcessor;
import spoon.processing.FileGenerator;
import spoon.reflect.Factory;
import spoon.reflect.declaration.CtSimpleType;
import spoon.support.JavaOutputProcessor;
import spoon.support.util.ClassFileUtil;
import spoon.support.util.JDTCompiler;

public class MyByteCodeOutputProcessor extends AbstractProcessor<CtSimpleType<?>>
        implements FileGenerator<CtSimpleType<?>> {

    /**
     * Extension for class files (.class).
     */
    public static final String CLASS_EXT = ".class";

    private File outputDir;

    private List<ICompilationUnit> units = new ArrayList<ICompilationUnit>();

    private List<File> printed = new ArrayList<File>();

    private List<String> printedTypes = new ArrayList<String>();

    private JavaOutputProcessor javaPrinter;

    /**
     * Creates a new processor for generating Java source files.
     *
     * @param outputDirectory
     *            the root output directory
     */
    public MyByteCodeOutputProcessor(JavaOutputProcessor javaPrinter,
            File outputDirectory) {
        outputDir = outputDirectory;
        this.javaPrinter = javaPrinter;
    }

    public List<File> getCreatedFiles() {
        return printed;
    }

    public File getOutputDirectory() {
        return outputDir;
    }

    /**
     * Tells if the source is Java 1.4 or lower.
     */
    public long getJavaCompliance() {
        switch (getFactory().getEnvironment().getComplianceLevel()) {
        case 1:
            return ClassFileConstants.JDK1_1;
        case 2:
            return ClassFileConstants.JDK1_2;
        case 3:
            return ClassFileConstants.JDK1_3;
        case 4:
            return ClassFileConstants.JDK1_4;
        case 5:
            return ClassFileConstants.JDK1_5;
        case 6:
            return ClassFileConstants.JDK1_6;
        }
        return ClassFileConstants.JDK1_5;
    }

    public void process(CtSimpleType<?> element) {
        if (!element.isTopLevel()) {
            return;
        }
        // Create Java code and create ICompilationUnit
        javaPrinter.getCreatedFiles().clear();
        javaPrinter.createJavaFile(element);

        for (File f : javaPrinter.getCreatedFiles()) {
            try {
                units.add(JDTCompiler.getUnit(element.getQualifiedName(), f));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void processingDone() {
        try {
            // Do compilation
            JDTCompiler compiler = new JDTCompiler();
            compiler.getCompilerOption().sourceLevel = getJavaCompliance();
            compiler.getCompilerOption().targetJDK = getJavaCompliance();
            compiler.compile(units.toArray(new ICompilationUnit[0]));

            getOutputDirectory().mkdirs();

            for (ClassFile f : compiler.getClassFiles()) {
                String fileName = new String(f.fileName()).replace('/',
                        File.separatorChar)
                        + CLASS_EXT;
//              System.out.println("--- adjusting "+fileName);
                ClassFileUtil.adjustLineNumbers(f.getBytes(), f.headerOffset
                        + f.methodCountOffset - 1, javaPrinter
                        .getLineNumberMappings().get(
                                new String(f.fileName()).replace('/', '.')));
                ClassFileUtil.writeToDisk(true, getOutputDirectory()
                        .getAbsolutePath(), fileName, f.getBytes());

                printed.add(new File(getOutputDirectory(), fileName));
                printedTypes.add(new String(f.fileName()).replace('/', '.'));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setOutputDirectory(File directory) {
        outputDir = directory;
    }
    public void updateFactory(Factory v) throws Exception {
        super.setFactory(v);
        javaPrinter.setFactory(v);
    }

}
