# spring-dapr-grpc-publisher

The publisher opens a redis stream "MAIN" and and sends all the data from a grpc client to this topic. All service communication is implemented with grpc.

## howto make it work

  - install and initialize [Dapr](https://github.com/dapr/dapr)
  - make sure the [Subscriber](https://github.com/devk-insurance/spring-dapr-grpc-subscriber) is running
  - mvn clean package
  - dapr run --protocol grpc --app-id publisher --app-port 12301 -- java -jar dapr-publish.jar
  - grpcurl -plaintext -d '{"key": "foo", "value": "bar"}' 127.0.0.1:12301 dapr.DaprProxyService.SaveKV
