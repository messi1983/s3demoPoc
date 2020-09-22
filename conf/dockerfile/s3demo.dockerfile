#
# Build stage
#
FROM maven:3.6-openjdk-8 as build
COPY . /app
WORKDIR /app
RUN mvn -q clean package

#
# Package stage
#
FROM openjdk:11

COPY --from=build /app/target/s3demo-0.0.1-SNAPSHOT.jar /data/app.jar
COPY --from=build /app/src/main/resources/application.properties /data/application.properties

RUN apk add --update --quiet bash
VOLUME /data

# Install terraform
ENV TERA_VERSION=0.12.24
RUN apk add --quiet --update unzip wget && \
    wget --quiet "https://releases.hashicorp.com/terraform/${TERA_VERSION}/terraform_${TERA_VERSION}_linux_amd64.zip" && \
    unzip terraform_${TERA_VERSION}_linux_amd64.zip && \
    chmod +x terraform && \
    mv terraform /usr/local/bin/.

# Run the jar file
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar",  \
    "-agentlib:jdwp=transport=dt_socket,address=8080,server=y,suspend=n", "/data/app.jar", \
    "--spring.config.location=file:/data/application.properties"]