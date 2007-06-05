package spoon.contrib.maven;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtElement;

public class CounterProcessor extends AbstractProcessor<CtElement> {
    public int count = 0;

    public void process(CtElement arg0) {
        if (count++ < 20) {
            System.err.println("counter :" + arg0.getClass());
        }
    }

}
