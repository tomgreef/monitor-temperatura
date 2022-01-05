FROM java:8
WORKDIR /
EXPOSE 4567 6379
ADD monitor.jar monitor.jar
ENV REDIS_HOST redis
CMD java -jar monitor.jar
