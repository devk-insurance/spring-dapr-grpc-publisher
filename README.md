# spring-dapr-grpc-publisher

The publisher opens a redis stream "MAIN" and and sends all the data from a grpc client to this topic. All service communication is implemented with grpc.

## howto make it work

  - install and initialize Dapr
  - mvn clean package
  - dapr run --protocol grpc --app-id publisher --app-port 12301 -- java -jar dapr-publish.jar
