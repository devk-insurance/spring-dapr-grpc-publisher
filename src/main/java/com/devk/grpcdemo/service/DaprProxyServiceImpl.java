package com.devk.grpcdemo.service;

import com.devk.grpcdemo.service.DaprProxyServiceGrpc.DaprProxyServiceImplBase;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import io.dapr.DaprGrpc;
import io.dapr.DaprGrpc.DaprBlockingStub;
import io.dapr.PublishEventEnvelope;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@GRpcService(interceptors = {LogInterceptor.class})
public class DaprProxyServiceImpl extends DaprProxyServiceImplBase {

    private final String GRPC_PORT = System.getenv("DAPR_GRPC_PORT");
    private final String HOSTNAME = InetAddress.getLocalHost().getHostName();
    private static final String SAVE_TOPIC = "SAVE";

    public DaprProxyServiceImpl() throws UnknownHostException {
    }

    @Override
    public void saveKV(KVRequest request, StreamObserver<PersistReply> responseObserver) {

        ManagedChannel channel = null;

        try {

            failWhenEnvPortMissing();

            channel = ManagedChannelBuilder.forAddress(HOSTNAME, Integer.parseInt(GRPC_PORT)).usePlaintext().build();
            DaprBlockingStub client = DaprGrpc.newBlockingStub(channel);


            final ByteString pack = Any.pack(request).toByteString();
            final Any build = Any.newBuilder().setValue(pack).build();


            final PublishEventEnvelope publish = PublishEventEnvelope.newBuilder().setTopic(SAVE_TOPIC).
                    setData(build).build();

            client.publishEvent(publish);

            PersistReply persistReply = PersistReply.newBuilder().setMessage("Sending event..").build();
            responseObserver.onNext(persistReply);
            responseObserver.onCompleted();

        } catch (StatusRuntimeException ex) {
            System.out.println("Server nicht erreichbar..");
        } finally {
            if (channel != null)
                channel.shutdown();
        }

    }

    private void failWhenEnvPortMissing() {
        if (GRPC_PORT == null || GRPC_PORT.isEmpty()) {
            throw new StatusRuntimeException(Status.UNAVAILABLE);
        }
    }

}
