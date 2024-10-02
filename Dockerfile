ARG REG_NAME
FROM $REG_NAME/maven-3-adoptopenjdk-11:01 as builder
WORKDIR /application
COPY . /application
RUN mvn dependency:go-offline
RUN mvn package -B -DskipTests
ARG PROJECT_KEY
ARG PROJECT_NAME
ARG RELEASE_TYPE
RUN mvn verify sonar:sonar -Dsonar.projectKey=$PROJECT_KEY-$RELEASE_TYPE -Dsonar.projectName=$PROJECT_NAME-$RELEASE_TYPE -Dsonar.exclusions=src/test/** -Dmaven.test.skip=true
ARG JAR_FILE=/application/target/*.jar

FROM amazoncorretto:11
WORKDIR application
COPY --from=builder application/target/*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8020