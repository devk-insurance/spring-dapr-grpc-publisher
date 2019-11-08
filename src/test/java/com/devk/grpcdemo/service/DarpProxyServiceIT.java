package com.devk.grpcdemo.service;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class DarpProxyServiceIT {

    @Ignore
    @Test
    public void callDaprProxyService() {

        ManagedChannel channel = ManagedChannelBuilder.
                forAddress("localhost", 12345).
                usePlaintext().
                build();

        DaprProxyServiceGrpc.DaprProxyServiceBlockingStub client = DaprProxyServiceGrpc.newBlockingStub(channel);
        KVRequest request = KVRequest.newBuilder().setKey("key").setValue("value").build();

        try {
            client.saveKV(request);
        } catch (StatusRuntimeException ex) {
            System.out.println("Server not available..");
        } finally {
            channel.shutdown();
        }

    }

    @Test
    @Ignore
    public void callDaprProxyService2() {

        ManagedChannel channel = ManagedChannelBuilder.
                forAddress("localhost", 12345).
                usePlaintext().
                build();

        DaprProxyServiceGrpc.DaprProxyServiceBlockingStub client = DaprProxyServiceGrpc.newBlockingStub(channel);
        KVRequest request = KVRequest.newBuilder().setKey("key").setValue("value").build();
        try {
            client.saveKV(request);
        } catch (StatusRuntimeException ex) {
            System.out.println("Server not available..");
        } finally {
            channel.shutdown();
        }

    }

    @Test
    public void anyParser() throws InvalidProtocolBufferException {

        final KVRequest kv = KVRequest.newBuilder().setKey("key").setValue("value").build();
        final Any pack = Any.pack(kv);

        final boolean isClazz = pack.is(KVRequest.class);
        Assert.assertTrue(isClazz);

        final ByteString bytes = pack.toByteString();
        final Any envelope = Any.newBuilder().setValue(bytes).build();

        final ByteString value = envelope.getValue();

        final Any any = Any.parser().parseFrom(value);

        final boolean isStillClazz = any.is(KVRequest.class);
        Assert.assertTrue(isStillClazz);

    }
}
