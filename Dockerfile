# For Java 8, try this
# FROM openjdk:8-jdk-alpine

# For Java 11, try this
FROM openjdk:11

ARG MAVEN_VERSION=3.8.7
ARG USER_HOME_DIR="/root"
# ARG SHA=a9b2d825eacf2e771ed5d6b0e01398589ac1bfa4171f36154d1b5787879605507802f699da6f7cfc80732a5282fd31b28e4cd6052338cbef0fa1358b48a5e3c8
ARG SHA=99bac5bf83633e3c7399aed725c8415e7b569b54e03e4599e580fc9cdb7c21ab
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries

RUN mkdir -p /usr/share/maven /usr/share/maven/ref
RUN curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz
RUN tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1
RUN rm -f /tmp/apache-maven.tar.gz
RUN ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"
CMD ["mvn"]

# cd /opt/app
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
RUN mvn clean install
# Refer to Maven build -> finalName
ARG JAR_FILE=target/customer-api-1.0.0.jar


# cp target/spring-boot-web.jar /opt/app/app.jar
RUN cp ${JAR_FILE} /usr/src/myapp/app.jar

# java -jar /opt/app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]
