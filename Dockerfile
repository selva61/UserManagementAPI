#
# Build stage
#
FROM maven:3.8.3-openjdk-17 AS build
VOLUME /tmp
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package
RUN ls -l /home/app/target/ # Added for debugging

#
# Package stage
#
FROM ubuntu/jre:17_edge
COPY --from=build /home/app/target/UserManagementAPI-0.0.1-SNAPSHOT.jar /usr/local/lib/UserManagementAPI.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/UserManagementAPI.jar"]