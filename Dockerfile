FROM openjdk
MAINTAINER Ben Moskowitz <bmmoskow@amath.washington.edu>

# copy our application code
ADD Service/target/Service-1.0-SNAPSHOT.jar /bin/Service.jar
ADD Service/config.yml /opt/config.yml

# expose port
EXPOSE 8000
EXPOSE 8001

# start app
CMD [ "java", "-jar", "/bin/Service.jar", "server", "/opt/config.yml" ]