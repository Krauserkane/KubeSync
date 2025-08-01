FROM container-registry.oracle.com/java/openjdk:latest

ARG JAR_FILE=xyz
COPY ${JAR_FILE} producer.jar

ENTRYPOINT ["java","-jar","/producer.jar"]
