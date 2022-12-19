FROM openjdk:8-jdk-alpine as builder
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract
ENV TZ="Asia/Almaty"

FROM openjdk:8-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
ENV TZ="Asia/Almaty"
RUN apk add gcompat
ENV LD_PRELOAD=/lib/libgcompat.so.0
WORKDIR application
RUN chown -R spring:spring /application
RUN chmod -R 777 /application
USER spring:spring
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} org.springframework.boot.loader.JarLauncher ${0} ${@}"]