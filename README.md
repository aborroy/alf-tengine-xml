# Alfresco Metadata Extract Transform Engine for XML

This project provides a *sample* Metadata Extract Transform Engine for XML, designed to be used with Alfresco Transform Service 3.0.0+.

## Features

- Extracts metadata from XML files and maps it to Alfresco properties.
- Compatible with Alfresco Community and Enterprise versions (23.x).
- Provides a test HTML interface for local validation.
- Supports Docker deployment for easy integration.

## Local Testing

### Requirements

Ensure you have the following dependencies installed:

- **Java** 17+
- **Maven** 3.5+

### Building the Application

To build the JAR package, run:

```bash
mvn clean package
```

### Running the Application

Once built, execute the following command:

```bash
java -jar target/alf-tengine-xml-1.0.0.jar
```

### Testing with the HTML Interface

After starting the service, open the test application at [http://localhost:8090](http://localhost:8090). Use the following input values:

- **file**: Upload an XML file following the format specified in [`sample.xml`](src/main/resources/sample.xml).
- **sourceMimetype**: `text/xml` (Alternatively, `application/xml` is also accepted).
- **targetMimetype**: `alfresco-metadata-extract`.

Click the **Transform** button to process the XML file. The extracted metadata will be returned as a JSON response.

*Metadata mapping is defined in* [`XmlMetadataExtractor_metadata_extract.properties`](src/main/resources/XmlMetadataExtractor_metadata_extract.properties).

## Building the Docker Image

### Requirements

- **Docker** 4.30+

### Building the Image

From the project root directory, build the Docker image with:

```bash
docker build . -t alfresco-tengine-xml
```

This will create a Docker image named `alfresco-tengine-xml:latest` in your local Docker repository.

## Deploying with Alfresco Community 23.x

Ensure your `compose.yaml` file includes the following configuration:

```yaml
services:
  alfresco:
    environment:
      JAVA_OPTS : >-
        -DlocalTransform.core-aio.url=http://transform-core-aio:8090/
        -DlocalTransform.xml.url=http://transform-xml:8090/

  transform-core-aio:
    image: alfresco/alfresco-transform-core-aio:5.1.6

  transform-xml:
    image: alfresco-tengine-xml:latest
```

**Key Configuration Updates:**
- Add `localTransform.xml.url` to the **Alfresco** service (`http://transform-xml:8090/` by default).
- Define the **transform-xml** service using the custom-built image.

*Ensure you have built the Docker image (`alfresco-tengine-xml`) before running Docker Compose.*

## Deploying with Alfresco Enterprise 23.x

Ensure your `compose.yaml` file includes the following configuration:

```yaml
services:
  alfresco:
    environment:
      JAVA_OPTS : >-
        -Dtransform.service.enabled=true
        -Dtransform.service.url=http://transform-router:8095
        -Dsfs.url=http://shared-file-store:8099/

  transform-router:
    image: quay.io/alfresco/alfresco-transform-router:4.1.4
    environment:
      CORE_AIO_URL: "http://transform-core-aio:8090"
      TRANSFORMER_URL_XML: "http://transform-xml:8090"
      TRANSFORMER_QUEUE_XML: "xml-engine-queue"

  transform-xml:
    image: alfresco-tengine-xml:latest
    environment:
      ACTIVEMQ_URL: "nio://activemq:61616"
      FILE_STORE_URL: >-
        http://shared-file-store:8099/alfresco/api/-default-/private/sfs/versions/1/file
```

**Key Configuration Updates:**
- Register the XML transformer with **transform-router**.
    - URL: `http://transform-xml:8090/` (default).
    - Queue Name: `xml-engine-queue` (defined in `application-default.yaml`).
- Define the **transform-xml** service and link it to ActiveMQ and Shared File Store services.

*Ensure you have built the Docker image (`alfresco-tengine-xml`) before running Docker Compose.*

## Automatic Metadata Extraction

Alfresco Repository automatically invokes the **XmlMetadataExtractor** every time an XML node is **created** or **updated**. As a result, **no folder rules or additional configuration are required** in Alfresco Share:

* The extractor is triggered globally—regardless of the site, library, or folder in which the XML file resides
* Metadata is mapped according to the definitions in [`XmlMetadataExtractor_metadata_extract.properties`](src/main/resources/XmlMetadataExtractor_metadata_extract.properties)

Simply deploy the Transform Engine and upload or modify XML files as usual; the Repository takes care of extracting the common metadata fields for you.

## Contributing

Contributions are welcome! To contribute:
1. Fork this repository.
2. Create a new branch (`feature-branch-name`).
3. Commit your changes.
4. Submit a pull request.

For major changes, please open an issue first to discuss your proposal.

## Support

For issues and feature requests, please open a GitHub issue in this repository.

Happy coding!