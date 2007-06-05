package net.sf.alchim.spoon.contrib.misc;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class PathHelper {
    
    public static String join(List<String> pathList) throws Exception {
        StringBuilder buff = new StringBuilder();
        for(String path: pathList) {
            buff.append(path).append(File.pathSeparatorChar); //work on Unix and windows
        }
        return buff.toString();
    }
    
    public static List<String> split(String pathList) throws Exception {
        String[] back = pathList.split(File.pathSeparator);
        return Arrays.asList(back);
    }
}
