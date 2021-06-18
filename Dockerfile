FROM adoptopenjdk:11-jdk-hotspot
MAINTAINER Ivan Muratov <binakot@gmail.com>

EXPOSE 8080

ADD build/libs/service-with-deps.jar /

CMD echo "Service is starting..." && \
    \
    java -XX:+ExitOnOutOfMemoryError \
    -Djava.security.egd=file:/dev/./urandom $JAVA_OPTIONS \
    -jar /service-with-deps.jar
