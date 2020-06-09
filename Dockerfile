# Start with a base image containing Java runtime
FROM 640233474616.dkr.ecr.ap-southeast-2.amazonaws.com/flitch-k8s-base:latest

# Add Maintainer Info
LABEL maintainer="vineet.garg@boral.com.au"

# Add a volume pointing to /tmp
VOLUME /apptemp

# The application's jar file
ARG JAR_FILE

# Add/Copy the application's jar to the container
COPY ${JAR_FILE} app.jar

# Run the jar file 
ENTRYPOINT ["java","-Djava.io.tmpdir=/apptemp","-Djava.security.egd=file:/dev/./urandom","-XX:+UnlockExperimentalVMOptions","-XX:+UseCGroupMemoryLimitForHeap","-XX:MaxRAMFraction=1","-XX:+UseSerialGC","-jar","/app.jar"]

USER root
