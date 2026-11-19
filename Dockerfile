FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY TRACK/pom.xml ./pom.xml
COPY TRACK/src ./src

RUN mvn clean package -DskipTests

RUN mkdir -p /tmp/postgresql && mvn dependency:copy -Dartifact=org.postgresql:postgresql:42.7.7 -DoutputDirectory=/tmp/postgresql

FROM quay.io/wildfly/wildfly:42.0.0.Final-jdk21

USER root

RUN mkdir -p /opt/jboss/wildfly/modules/org/postgresql/main

COPY --from=build /tmp/postgresql/postgresql-42.7.7.jar /opt/jboss/wildfly/modules/org/postgresql/main/postgresql.jar

RUN printf '%s\n' \
'<?xml version="1.0" encoding="UTF-8"?>' \
'<module xmlns="urn:jboss:module:1.9" name="org.postgresql">' \
'<resources><resource-root path="postgresql.jar"/></resources>' \
'<dependencies><module name="jakarta.api"/><module name="javax.api"/><module name="java.sql"/></dependencies>' \
'</module>' \
> /opt/jboss/wildfly/modules/org/postgresql/main/module.xml

COPY --from=build /app/target/track.war /opt/jboss/wildfly/track.war

COPY docker-entrypoint.sh /opt/jboss/wildfly/docker-entrypoint.sh

RUN sed -i '1s/^\xEF\xBB\xBF//' /opt/jboss/wildfly/docker-entrypoint.sh \
    && sed -i 's/\r$//' /opt/jboss/wildfly/docker-entrypoint.sh \
    && chmod +x /opt/jboss/wildfly/docker-entrypoint.sh \
    && chown -R jboss:root /opt/jboss/wildfly/modules/org/postgresql /opt/jboss/wildfly/standalone/deployments /opt/jboss/wildfly/docker-entrypoint.sh

USER jboss

EXPOSE 10000

ENTRYPOINT ["/opt/jboss/wildfly/docker-entrypoint.sh"]
