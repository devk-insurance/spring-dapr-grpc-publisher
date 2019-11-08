package com.devk.grpcdemo.service;

import com.google.protobuf.Any;
import com.google.protobuf.Empty;
import com.google.protobuf.InvalidProtocolBufferException;
import io.dapr.CloudEventEnvelope;
import io.dapr.DaprClientGrpc;
import io.dapr.GetTopicSubscriptionsEnvelope;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;


@Slf4j
@GRpcService(interceptors = {LogInterceptor.class})
public class DaprClientServiceImpl extends DaprClientGrpc.DaprClientImplBase {

    private final static String TOPIC_FINISHED = "FINISHED";

    @Autowired
    DaprProxyServiceGrpc.DaprProxyServiceImplBase proxy;

    @Override
    public void getTopicSubscriptions(Empty request, StreamObserver<GetTopicSubscriptionsEnvelope> responseObserver) {
        responseObserver.onNext(GetTopicSubscriptionsEnvelope.newBuilder().addTopics(TOPIC_FINISHED).build());
        responseObserver.onCompleted();
    }


    @Override
    public void onTopicEvent(CloudEventEnvelope request, StreamObserver<Empty> responseObserver) {

        final Any data = request.getData();
        final Any any = AnyUtils.unpackRedisEnvelope(data);

        if (any.is(KVRequest.class)) {

            try {
                final KVRequest unpack = any.unpack(KVRequest.class);

                final String key = unpack.getKey();
                final String value = unpack.getValue();

                log.info("Callback for reading the saved KV: {} : {}", key, value);

            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        } else {
            log.error("Couldnt unpack data from callback!");
        }

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();

    }
}

