package org.alfresco.transform.xml;

import org.alfresco.transform.base.TransformEngine;
import org.alfresco.transform.base.probes.ProbeTransform;
import org.alfresco.transform.config.TransformConfig;
import org.alfresco.transform.config.reader.TransformConfigResourceReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class XmlTransformEngine implements TransformEngine {

    @Autowired
    private TransformConfigResourceReader transformConfigResourceReader;

    @Override
    public String getTransformEngineName() {
        return "xml";
    }

    @Override
    public String getStartupMessage() {
        return "Startup "+getTransformEngineName()+"\nNo 3rd party licenses";
    }

    @Override
    public TransformConfig getTransformConfig() {
        return transformConfigResourceReader.read("classpath:xml_engine_config.json");
    }

    @Override
    public ProbeTransform getProbeTransform() {
        return new ProbeTransform("text.xml",
                "application/xml", "alfresco-metadata-extract", Map.of("test","test"),
                60, 16, 400, 10240, 60 * 30 + 1, 60 * 15 + 20 );
    }
}
