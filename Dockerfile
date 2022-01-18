FROM zookeeper

WORKDIR /home
ADD /server.jar /home/

CMD ["java", "-jar", "server.jar"]
