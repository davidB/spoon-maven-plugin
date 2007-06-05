package spoon.contrib.maven;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import spoon.processing.Builder;
import spoon.processing.Environment;
import spoon.processing.ProcessingManager;
import spoon.reflect.Factory;
import spoon.support.DefaultCoreFactory;
import spoon.support.QueueProcessingManager;
import spoon.support.builder.CtFile;
import spoon.support.builder.CtFolder;
import spoon.support.builder.CtResource;
import spoon.support.builder.SpoonBuildingManager;
import spoon.support.builder.support.CtFileFile;
import spoon.support.builder.support.CtFolderFile;
import spoon.support.builder.support.CtFolderZip;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Apply a set of spoonlet and Spoon's processor.
 */
public class Launcher {

    private List<String> sources_ = new ArrayList<String>();;

    private List<String> processors_ = new ArrayList<String>();

    private Environment env_;

    private HashSet<CtResource> ctTemplates_ = new HashSet<CtResource>();

    private Factory factory_;

    /**
     * Create and initialize a Launcher.
     *
     * @param sources list of the source directories containing the sources to be processed.
     * @param processors List of processors' class to apply.
     * @param env the running environnment
     * @throws Exception
     */
    public Launcher(List<String> sources, List<String> processors, Environment env) throws Exception {
        sources_ = new ArrayList<String>();
        sources_.addAll(sources);
        processors_ = new ArrayList<String>();
        processors_.addAll(processors);
        env_ = env;
    }

    public void run() throws Exception {
        prepare();
        build();
        process();

        env_.reportEnd();
    }

    protected void build() throws Exception {
        String oldClasspath = System.getProperty("java.class.path");
        try {
            // align the classpath system property to the current classloader
            // (needed to configure eclise jdtcompiler out from Eclipse IDE)
            // ClasspathHelper.setClasspathProperty(null);
            String classpath = ClasspathHelper.toClasspathString(null);
            env_.debugMessage("classpath :" + classpath);
            System.setProperty("java.class.path", classpath);
            Builder builder = new SpoonBuildingManager();
            for (String path : sources_) {
                File file = new File(path);
                CtResource src = (file.isDirectory())?new CtFolderFile(file):new CtFileFile(file);
                builder.addInputSource(src);
            }
            for (CtResource f : ctTemplates_) {
                builder.addTemplateSource(f);
            }
            builder.build(factory_);
        } finally {
            System.setProperty("java.class.path", oldClasspath);
        }
    }

    /**
     * Processes the built model with the processors.
     */
    protected void process() throws Exception {
        env_.debugMessage("process model");
        // processing (consume all the processors)
        ProcessingManager processing = new QueueProcessingManager(factory_);
        for (String processorName : processors_) {
            env_.debugMessage("loading processor " + processorName);
            //processing.addProcessor((Processor)Class.forName(processorName, true, Thread.currentThread().getContextClassLoader()).newInstance());
            processing.addProcessor(processorName);
        }
        if (env_.getDefaultFileGenerator() != null) {
            processing.addProcessor(env_.getDefaultFileGenerator());
        }
        processing.process();
    }

    /**
     * Load content of spoonlet file (search in the current classpath).
     */
    protected void searchSpoonlets() throws Exception {
        List<URL> urls = ClasspathHelper.scan(null, false);
        for (URL url : urls) {
            File f = new File(url.toURI());
            if (f.getName().endsWith(".jar")) {
                searchSpoonlets(f);
            }
        }
    }

    /**
     * Load content of spoonlet file (template and processor list).
     */
    protected void searchSpoonlets(File jar) throws Exception {
        env_.debugMessage("search spoonlets from :" + jar);
        CtFolder folder = new CtFolderZip(jar);
        List<CtResource> spoonletIndex = new ArrayList<CtResource>();
        CtFile configFile = null;
        for (CtFile file : folder.getAllFile()) {
            if (file.isJava())
                spoonletIndex.add(file);
            else if (file.getName().endsWith("spoon.xml")) {
                // Loading spoonlet properties
                configFile = file;
            }
        }
        if (configFile != null) {
            XMLReader xr = XMLReaderFactory.createXMLReader();
            SpoonletXmlHandler loader = new SpoonletXmlHandler(factory_, processors_, ctTemplates_, spoonletIndex);
            xr.setContentHandler(loader);
            InputStream stream = configFile.getContent();
            xr.parse(new InputSource(stream));
            stream.close();
        }
    }

    protected void prepare() throws Exception {
        factory_ = new Factory(new DefaultCoreFactory(), env_);
        searchSpoonlets();
    }

}
