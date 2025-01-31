package org.alfresco.transform.xml;

import org.alfresco.transform.base.TransformEngine;
import org.alfresco.transform.base.probes.ProbeTransform;
import org.alfresco.transform.config.TransformConfig;
import org.alfresco.transform.config.reader.TransformConfigResourceReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * XML Transformation Engine implementation for Alfresco.
 * This class is responsible for handling XML transformations
 * and loading the corresponding transformation configurations.
 */
@Component
public class XmlTransformEngine implements TransformEngine {

    private static final String ENGINE_NAME = "xml";
    private static final String CONFIG_PATH = "classpath:xml_engine_config.json";

    private final TransformConfigResourceReader transformConfigResourceReader;

    /**
     * Constructor-based dependency injection for TransformConfigResourceReader.
     *
     * @param transformConfigResourceReader Reader for transformation configuration resources.
     */
    @Autowired
    public XmlTransformEngine(TransformConfigResourceReader transformConfigResourceReader) {
        this.transformConfigResourceReader = transformConfigResourceReader;
    }

    /**
     * Returns the name of the transformation engine.
     *
     * @return the engine name ("xml").
     */
    @Override
    public String getTransformEngineName() {
        return ENGINE_NAME;
    }

    /**
     * Provides a startup message for logging purposes.
     *
     * @return a message indicating startup and absence of third-party licenses.
     */
    @Override
    public String getStartupMessage() {
        return String.format("Startup %s\nNo 3rd party licenses", ENGINE_NAME);
    }

    /**
     * Loads and returns the transformation configuration for the XML engine.
     *
     * @return an instance of TransformConfig containing the transformation settings.
     */
    @Override
    public TransformConfig getTransformConfig() {
        return transformConfigResourceReader.read(CONFIG_PATH);
    }

    /**
     * Provides a probe transformation for testing and validation.
     *
     * @return a ProbeTransform configured for XML metadata extraction testing.
     */
    @Override
    public ProbeTransform getProbeTransform() {

        return new ProbeTransform(
                "sample.xml", "text/xml", "alfresco-metadata-extract", Map.of(),
                2384, 16, 400, 10240,
                (60 * 30) + 1, (60 * 15) + 20
        );
    }
}