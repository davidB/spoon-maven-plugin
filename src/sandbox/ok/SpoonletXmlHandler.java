/*
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */

package spoon.contrib.maven;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import spoon.reflect.Factory;
import spoon.support.builder.CtResource;
import spoon.support.processing.XmlProcessorProperties;

/**
 * This class defines the SAX handler to parse a Spoonlet deployment descriptor
 * file.
 */
public class SpoonletXmlHandler extends DefaultHandler {

    private List<CtResource> spoonletIndex_;

    private XmlProcessorProperties prop_;

    private String propName_;

    private List<Object> values_;

    private String buffer_;

    private Collection<CtResource> templates_;

    private List<String> processors_;

    private Factory factory_;

    private String clazz_;

    /**
     * Creates a new handler.
     *
     * @param launcher
     *            the launcher
     * @param spoonletIndex ?
     */
    public SpoonletXmlHandler(Factory factory, List<String> processors, Collection<CtResource> templates, List<CtResource> spoonletIndex) {
        super();
        factory_ = factory;
        processors_ = processors;
        templates_ = templates;
        spoonletIndex_ = spoonletIndex;
    }

    /**
     * Handles XML element ends.
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("processor")) {
            if (!processors_.contains(clazz_)) {
                processors_.add(clazz_);
                factory_.getEnvironment().setProcessorProperties(prop_.getProcessorName(), prop_);
            }
            prop_ = null;
        } else if (localName.equals("property")) {
            if (values_ != null) {
                prop_.addProperty(propName_, values_);
            }
            values_ = null;
            propName_ = null;
        } else if (localName.equals("value")) {
            values_.add(buffer_);
        }
        buffer_ = null;
        super.endElement(uri, localName, qName);
    }

    /**
     * Handles characters.
     */
    @Override
    public void characters(char[] ch, int start, int end) throws SAXException {
        buffer_ = new String(ch, start, end);
    }

    /**
     * Handles XML element starts.
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("processor")) {
            clazz_ = attributes.getValue("class");
            prop_ = new XmlProcessorProperties(factory_, attributes.getValue("class"));
        } else if (localName.equals("template")) {
            String foldername = attributes.getValue("path");
            for (CtResource r : spoonletIndex_) {
                if (r.getName().startsWith(foldername)) {
                    templates_.add(r);
                }
            }
        } else if (localName.equals("property")) {
            propName_ = attributes.getValue("name");
            if (attributes.getValue("value") != null) {
                prop_.addProperty(propName_, attributes.getValue("value"));
            } else {
                values_ = new ArrayList<Object>();
            }
        }
    }
}
