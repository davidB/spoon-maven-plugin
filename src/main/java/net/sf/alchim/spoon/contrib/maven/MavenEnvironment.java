package net.sf.alchim.spoon.contrib.maven;

import org.apache.maven.plugin.logging.Log;

import spoon.processing.FileGenerator;
import spoon.processing.Processor;
import spoon.processing.Severity;
import spoon.reflect.declaration.CtElement;
import spoon.support.StandardEnvironment;

@SuppressWarnings("serial")
class MavenEnvironment extends StandardEnvironment {
    private Log logger_;

    public MavenEnvironment(Log log) {
        logger_ = log;
    }

    @Override()
    public void debugMessage(String msg) {
        logger_.debug(msg);
    }

    @Override()
    public void report(Processor processor, Severity severity, CtElement element, String message) {
        String msg = processor.getClass().getSimpleName() + ">>" + element.getPosition().getFile().getAbsolutePath() + "@[" + element.getPosition().getLine() + "," + element.getPosition().getColumn() + "]:" + message;
        if (Severity.ERROR.equals(severity)) {
            logger_.error(msg);
        } else if (Severity.WARNING.equals(severity)) {
            logger_.warn(msg);
        } else {
            logger_.info(msg);
        }
    }

    public FileGenerator<? extends CtElement> getDefaultFileGenerator() {
        return (FileGenerator<? extends CtElement>) super.getDefaultFileGenerator();
    }

}
