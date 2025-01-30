FROM ubuntu:latest

RUN apt-get update \
    && apt-get upgrade -y \
    && apt-get install -y openjdk-17-jdk

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en' LC_ALL='en_US.UTF-8'
ENV JAVA_OPTS=''

# Set default user information
ARG GROUP_NAME=alfresco
ARG GROUP_ID=1001
ARG USER_NAME=alftexml
ARG USER_ID=33005

COPY target/alf-tengine-xml-1.0.0.jar /usr/bin/

RUN groupadd -g ${GROUP_ID} ${GROUP_NAME} && \
    useradd -u ${USER_ID} -G ${GROUP_NAME} ${USER_NAME} && \
    chgrp -R ${GROUP_NAME} /usr/bin/*.jar

EXPOSE 8090

USER ${USER_NAME}

ENTRYPOINT ["/bin/sh", "-c", "java $JAVA_OPTS -jar /usr/bin/alf-tengine-xml-1.0.0.jar"]