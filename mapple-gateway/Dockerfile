FROM openjdk:8-jdk

#复制打好的jar包
COPY target/*.jar /app.jar
RUN  apk add -U tzdata; \
ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime; \
echo 'Asia/Shanghai' >/etc/timezone; \
touch /app.jar;



#EXPOSE 8080
ENV server_port ${server_port}
EXPOSE ${server_port}

ENTRYPOINT [ "sh", "-c", "java -Dfile.encoding=utf8 -Djava.security.egd=file:/dev/./urandom  -jar /app.jar --spring.profiles.active=release --server.port=${server_port}" ]
