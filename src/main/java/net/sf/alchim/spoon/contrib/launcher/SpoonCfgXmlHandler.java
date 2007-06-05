/*
 * Spoon - http://net.sf.alchim.spoon.gforge.inria.fr/
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

package net.sf.alchim.spoon.contrib.launcher;

import java.io.File;
import java.net.URL;
import java.util.List;


import net.sf.alchim.spoon.contrib.launcher.artifact.Artifact;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;




/**
 * This class defines the SAX handler to parse a Spoonlet deployment descriptor
 * file.
 */
public class SpoonCfgXmlHandler extends DefaultHandler {

    public static void searchSpoonlets(File file, List<URL> remoteRoots, List<Artifact> artifacts) throws Exception {
        XMLReader xr = XMLReaderFactory.createXMLReader();
        SpoonCfgXmlHandler loader = new SpoonCfgXmlHandler(remoteRoots, artifacts);
        xr.setContentHandler(loader);
        xr.parse(file.getAbsolutePath());
    }

    private List<Artifact> artifacts_;

    private List<URL> repos_;

    /**
     * Creates a new handler.
     * 
     * @param launcher
     *            the launcher
     * @param spoonletIndex ?
     */
    public SpoonCfgXmlHandler(List<URL> repos, List<Artifact> artifacts) {
        repos_ = repos;
        artifacts_ = artifacts;
    }

    /**
     * Handles XML element starts.
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            if (localName.equals("repository")) {
                URL url = new URL(attributes.getValue("url").trim());
                repos_.add(url);
            } else if (localName.equals("spoonlet")) {
                Artifact artifact = new Artifact(attributes.getValue("groupId"), attributes.getValue("artifactId"), attributes.getValue("version"));
                artifacts_.add(artifact);
            }
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }
}
