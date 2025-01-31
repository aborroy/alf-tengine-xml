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

/**
 * XML Metadata Extractor for extracting metadata from XML documents.
 * This class extends {@link AbstractMetadataExtractorEmbedder} and is responsible for
 * extracting specific metadata fields from an XML document.
 */
@Component
public class XmlMetadataExtractor extends AbstractMetadataExtractorEmbedder {

    private static final Logger logger = LoggerFactory.getLogger(XmlMetadataExtractor.class);

    /**
     * Constructor initializing the metadata extractor.
     */
    protected XmlMetadataExtractor() {
        super(EXTRACTOR, logger);
    }

    /**
     * Returns the transformer name used to identify this extractor.
     *
     * @return Transformer name as a string.
     */
    @Override
    public String getTransformerName() {
        return "xmlextract";
    }

    /**
     * Embeds metadata into the XML document. Not implemented as this extractor only extracts metadata.
     */
    @Override
    public void embedMetadata(String sourceMimetype, InputStream inputStream, String targetMimetype,
                              OutputStream outputStream, Map<String, String> transformOptions, TransformManager transformManager)
            throws Exception {
        // No-op: This extractor does not support embedding metadata.
    }

    /**
     * Extracts metadata from an XML document and returns it as a map.
     *
     * @param sourceMimetype  The source MIME type.
     * @param inputStream     The input stream containing the XML document.
     * @param targetMimetype  The target MIME type (not used).
     * @param outputStream    The output stream (not used).
     * @param transformOptions Transform options (not used).
     * @param transformManager Transform manager instance (not used).
     * @return A map containing extracted metadata fields.
     * @throws Exception If an error occurs while processing the XML document.
     */
    @Override
    public Map<String, Serializable> extractMetadata(String sourceMimetype, InputStream inputStream, String targetMimetype,
                                                     OutputStream outputStream, Map<String, String> transformOptions,
                                                     TransformManager transformManager) throws Exception {
        Document xmlDocument = loadXML(inputStream);
        Map<String, Serializable> extractedMetadata = new HashMap<>();

        // Extract metadata fields from XML
        putRawValue("project.number", getProperty(xmlDocument, "project/number"), extractedMetadata);
        putRawValue("securityClassification", getProperty(xmlDocument, "securityClassification"), extractedMetadata);
        putRawValue("text", getProperty(xmlDocument, "text"), extractedMetadata);

        return extractedMetadata;
    }

    /**
     * Retrieves the value of a specific XML node given an XPath-like structure.
     *
     * @param xmlDocument The parsed XML document.
     * @param xmlPath The XML path (e.g., "project/number").
     * @return The extracted text content or null if not found.
     */
    private String getProperty(Document xmlDocument, String xmlPath) {
        try {
            String[] pathElements = xmlPath.split("/");
            Node currentNode = xmlDocument.getDocumentElement();

            // Adjust index to navigate correctly within XML tree
            int startIndex = currentNode.getNodeName().equals(pathElements[0]) ? 1 : 0;

            for (int i = startIndex; i < pathElements.length; i++) {
                String element = pathElements[i];
                NodeList children = currentNode.getChildNodes();
                Node foundNode = null;

                for (int j = 0; j < children.getLength(); j++) {
                    Node node = children.item(j);
                    if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(element)) {
                        foundNode = node;
                        break;
                    }
                }

                if (foundNode == null) {
                    logger.debug("Element '{}' not found in path '{}'.", element, xmlPath);
                    return null;
                }
                currentNode = foundNode;
            }
            return currentNode.getTextContent().trim();
        } catch (Exception e) {
            logger.error("Error extracting property from XML path: {}", xmlPath, e);
            return null;
        }
    }

    /**
     * Loads an XML document from an input stream.
     *
     * @param xmlInputStream The input stream containing XML data.
     * @return The parsed XML {@link Document} object.
     * @throws Exception If an error occurs during XML parsing.
     */
    private Document loadXML(InputStream xmlInputStream) throws Exception {
        try (InputStream inputStream = xmlInputStream) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document xmlDocument = builder.parse(inputStream);
            xmlDocument.getDocumentElement().normalize();
            return xmlDocument;
        }
    }
}