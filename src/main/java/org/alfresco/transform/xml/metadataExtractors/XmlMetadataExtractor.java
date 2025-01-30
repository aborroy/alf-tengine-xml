package org.alfresco.transform.xml.metadataExtractors;

import org.alfresco.transform.base.TransformManager;
import org.alfresco.transform.base.metadata.AbstractMetadataExtractorEmbedder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.alfresco.transform.base.metadata.AbstractMetadataExtractorEmbedder.Type.EXTRACTOR;

@Component
public class XmlMetadataExtractor extends AbstractMetadataExtractorEmbedder {

    private static final Logger logger = LoggerFactory.getLogger(XmlMetadataExtractor.class);

    protected XmlMetadataExtractor() {
        super(EXTRACTOR, logger);
    }

    @Override
    public String getTransformerName() {
        return "xmlextract";
    }

    @Override
    public void embedMetadata(String sourceMimetype, InputStream inputStream, String targetMimetype,
                              OutputStream outputStream, Map<String, String> transformOptions, TransformManager transformManager)
            throws Exception {
        // Only used for extract, so may be empty.
    }

    @Override
    public Map<String, Serializable> extractMetadata(String sourceMimetype, InputStream inputStream, String targetMimetype,
                                                     OutputStream outputStream, Map<String, String> transformOptions,
                                                     TransformManager transformManager) throws Exception {
        Document xmlDocument = loadXML(inputStream);
        final Map<String, Serializable> rawProperties = new HashMap<>();

        // Updated property paths to match XML structure
        putRawValue("project.number", getProperty(xmlDocument, "project/number"), rawProperties);
        putRawValue("securityClassification", getProperty(xmlDocument, "securityClassification"), rawProperties);
        putRawValue("text", getProperty(xmlDocument, "text"), rawProperties);

        return rawProperties;
    }

    public String getProperty(Document xmlDocument, String xmlPath) {
        try {
            // Split the path using forward slash
            String[] pathElements = xmlPath.split("/");
            Node currentNode = xmlDocument.getDocumentElement();

            // Skip the first navigation if the current node name matches the first path element
            int startIndex = currentNode.getNodeName().equals(pathElements[0]) ? 1 : 0;

            // Navigate through the XML structure
            for (int i = startIndex; i < pathElements.length; i++) {
                String element = pathElements[i];
                NodeList children = currentNode.getChildNodes();
                currentNode = null;

                for (int j = 0; j < children.getLength(); j++) {
                    Node node = children.item(j);
                    if (node.getNodeType() == Node.ELEMENT_NODE &&
                            node.getNodeName().equals(element)) {
                        currentNode = node;
                        break;
                    }
                }

                if (currentNode == null) {
                    logger.debug("Could not find element: {} in path: {}", element, xmlPath);
                    return null;
                }
            }

            return currentNode.getTextContent().trim();
        } catch (Exception e) {
            logger.error("Error getting property for path: {}", xmlPath, e);
            return null;
        }
    }

    private Document loadXML(InputStream xmlInputStream) throws Exception {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document xmlDocument = builder.parse(xmlInputStream);
            xmlDocument.getDocumentElement().normalize();
            return xmlDocument;
        } finally {
            if (xmlInputStream != null) {
                xmlInputStream.close();
            }
        }
    }
}