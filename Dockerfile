FROM amazoncorretto:17
LABEL maintainer="e621777@cargill.com"
LABEL PROJECT_NAME="techhealth-rest-api-service"

RUN yum -y update java-17-amazon-corretto-devel
RUN yum -y update libxml2
RUN yum -y update zlib
RUN yum -y update expat
RUN yum -y update curl
RUN yum -y update vim-data
RUN yum -y update vim-minimal
RUN yum -y update libblkid
RUN yum -y update libcom_err
RUN yum -y update libmount
RUN yum -y update libuuid
RUN yum -y update ncurses
RUN yum -y update ncurses-base
RUN yum -y update ncurses-libs
RUN yum -y update krb5-libs
RUN yum -y update libpng
RUN yum -y update libtasn1
RUN yum -y update sqlite
RUN yum -y update openssl-libs
RUN yum -y update ca-certificates


ARG SPRING_PROFILES_ACTIVE=default

VOLUME /tmp
COPY target/*.jar app.jar
EXPOSE 8010
ENTRYPOINT exec java  $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar /app.jar
