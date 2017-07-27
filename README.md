# Try My Policy

The Try My Policy (TRY) is a service that enables patients to preview the redacted version of their uploaded clinical document based on the privacy preferences of the consent. It calls the [Document Segmentation Service (DSS)](https://github.com/bhits/dss) to (1) segment the patient's clinical document, in the template prescribed by C-CDA-R1, C-CDA-R2, and HITSP C32, and (2) highlight the sections that will be removed in accordance to the patient's consent. Try My Policy transforms the response from DSS into a full XHTML file and provides the Base 64 encoded file in the response JSON. This service is currently utilized in the Consent2Share UI (c2s-ui) for patients to try their policies on their uploaded documents.
	
## Build

### Prerequisites

+ [Oracle Java JDK 8 with Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
+ [Docker Engine](https://docs.docker.com/engine/installation/) (for building a Docker image from the project)

### Commands

This is a Maven project and requires [Apache Maven](https://maven.apache.org/) 3.3.3 or greater to build it. It is recommended to use the *Maven Wrapper* scripts provided with this project. *Maven Wrapper* requires an internet connection to download Maven and project dependencies for the very first build.

To build the project, navigate to the folder that contains `pom.xml` file using the terminal/command line.

+ To build a JAR:
    + For Windows, run `mvnw.cmd clean install`
    + For *nix systems, run `mvnw clean install`
+ To build a Docker Image (this will create an image with `bhitsdev/try-policy:latest` tag):
    + For Windows, run `mvnw.cmd clean package docker:build`
    + For *nix systems, run `mvnw clean package docker:build`

## Run

### Commands

This is a [Spring Boot](https://projects.spring.io/spring-boot/) project and serves the application via an embedded Tomcat instance. Therefore, there is no need for a separate application server to run this service.
+ Run as a JAR file: `java -jar try-policy-x.x.x-SNAPSHOT.jar <additional program arguments>`
+ Run as a Docker Container: `docker run -d bhitsdev/try-policy:latest <additional program arguments>`

*NOTE: In order for this service to fully function as a microservice in C2S Application, it is also required to setup the dependency microservices and support level infrastructure. Please refer to the [C2S Deployment Guide](https://github.com/bhits-dev/consent2share/releases/download/2.1.1/c2s-deployment-guide.pdf) for instructions to setup the C2S infrastructure.*

## Configure

This service utilizes [`Configuration Server`](https://github.com/bhits-dev/config-server) which is based on [Spring Cloud Config](https://github.com/spring-cloud/spring-cloud-config) to manage externalized configuration, which is stored in a `Configuration Data Git Repository`. We provide a [`Default Configuration Data Git Repository`]( https://github.com/bhits-dev/c2s-config-data).

This service can run with the default configuration, which is targeted for a local development environment. Default configuration data is from three places: `bootstrap.yml`, `application.yml`, and the data which `Configuration Server` reads from `Configuration Data Git Repository`. Both `bootstrap.yml` and `application.yml` files are located in the `resources` folder of this source code.

We **recommend** overriding the configuration as needed in the `Configuration Data Git Repository`, which is used by the `Configuration Server`.

Also, please refer to [Spring Cloud Config Documentation](https://cloud.spring.io/spring-cloud-config/spring-cloud-config.html) to see how the config server works, [Spring Boot Externalized Configuration](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) documentation to see how Spring Boot applies the order to load the properties, and [Spring Boot Common Properties](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html) documentation to see the common properties used by Spring Boot.

### Other Ways to Override Configuration

#### Override a Configuration Using Program Arguments While Running as a JAR:

+ `java -jar try-policy-x.x.x-SNAPSHOT.jar --server.port=80 --logging.file=/logs/tryPolicy.log`

#### Override a Configuration Using Program Arguments While Running as a Docker Container:

+ `docker run -d bhitsdev/try-policy:latest --server.port=80 --logging.file=/logs/tryPolicy.log`

+ In a `docker-compose.yml`, this can be provided as:
```yml
version: '2'
services:
...
  try-policy.c2s.com:
    image: "bhitsdev/try-policy:latest"
    command: ["--server.port=80","--logging.file=/logs/tryPolicy.log"]
...
```
*NOTE: Please note that these additional arguments will be appended to the default `ENTRYPOINT` specified in the `Dockerfile` unless the `ENTRYPOINT` is overridden.*

### Configuring Sample C32/C-CDA Documents

The TRY can be configured to provide one or more sample C32 and/or C-CDA documents which will be made available to all patients to use when testing their consents. By default, the TRY is configured to provide a single sample document named `"Sample C-CDA R2 CCD_2 Doc.xml"` to all patients. That sample document is built into the TRY application itself, and the default `application.yml` file is set to use that built-in `"Sample C-CDA R2 CCD_2 Doc.xml"` file as the sample document for patients.

To use your own file(s) as the sample document(s) for patients, override the `application.yml` file's `c2s.try-policy.sample-uploaded-documents` property as follows:
```yml
...
c2s:
  ...
  try-policy:
      sample-uploaded-documents:
        - file-path: "<FULL PATH TO YOUR SAMPLE FILE, INCLUDING FILE NAME>"
          document-name: "<NAME OF DOCUMENT TO SHOW TO USERS>"
...
```

You can also configure TRY to provide more than one sample document. To do so, see the following example:
```yml
...
c2s:
  ...
  try-policy:
      sample-uploaded-documents:
        - file-path: "/usr/local/custom_sample_docs/sample_doc_1.xml"
          document-name: "sample_doc_1.xml"
        - file-path: "/usr/local/custom_sample_docs/sample_doc_2.xml"
          document-name: "sample_doc_2.xml"
...
```
**IMPORTANT NOTES:**
1. For the `file-path` property, you need to specify the entire path to your sample file **and** the file name itself (e.g. `"/usr/local/custom_sample_docs/sample_doc_1.xml"`).
2. For the `document-name` property, this can be any user friendly string to use as the document name which is displayed to users.

### Enable SSL

For simplicity in development and testing environments, SSL is **NOT** enabled by default configuration. SSL can easily be enabled following the examples below:

#### Enable SSL While Running as a JAR

+ `java -jar try-policy-x.x.x-SNAPSHOT.jar --spring.profiles.active=ssl --server.ssl.key-store=/path/to/ssl_keystore.keystore --server.ssl.key-store-password=strongkeystorepassword`

#### Enable SSL While Running as a Docker Container

+ `docker run -d -v "/path/on/dockerhost/ssl_keystore.keystore:/path/to/ssl_keystore.keystore" bhitsdev/try-policy:latest --spring.profiles.active=ssl --server.ssl.key-store=/path/to/ssl_keystore.keystore --server.ssl.key-store-password=strongkeystorepassword`
+ In a `docker-compose.yml`, this can be provided as:
```yml
version: '2'
services:
...
  try-policy.c2s.com:
    image: "bhitsdev/try-policy:latest"
    command: ["--spring.profiles.active=ssl","--server.ssl.key-store=/path/to/ssl_keystore.keystore", "--server.ssl.key-store-password=strongkeystorepassword"]
    volumes:
      - /path/on/dockerhost/ssl_keystore.keystore:/path/to/ssl_keystore.keystore
...
```

*NOTE: As seen in the examples above, `/path/to/ssl_keystore.keystore` is made available to the container via a volume mounted from the Docker host running this container.*

### Override Java CA Certificates Store In Docker Environment

Java has a default CA Certificates Store that allows it to trust well-known certificate authorities. For development and testing purposes, one might want to trust additional self-signed certificates. In order to override the default Java CA Certificates Store in a Docker container, one can mount a custom `cacerts` file over the default one in the Docker image as follows: `docker run -d -v "/path/on/dockerhost/to/custom/cacerts:/etc/ssl/certs/java/cacerts" bhitsdev/try-policy:latest`

*NOTE: The `cacerts` references given in the both sides of volume mapping above are files, not directories.*

[//]: # (## API Documentation)

[//]: # (## Notes)

[//]: # (## Contribute)

## Contact

If you have any questions, comments, or concerns please see [Consent2Share](https://bhits-dev.github.io/consent2share/) project site.

## Report Issues

Please use [GitHub Issues](https://github.com/bhits-dev/try-policy/issues) page to report issues.

[//]: # (License)
