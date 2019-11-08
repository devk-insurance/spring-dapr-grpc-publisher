package com.devk.grpcdemo.service;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;

@Slf4j
class AnyUtils {
    private AnyUtils() {
    }

    static Any unpackRedisEnvelope(Any any) {
        try {
            final String utf8 = any.getValue().toStringUtf8();
            String unescaped = StringEscapeUtils.unescapeJava(utf8);
            unescaped = unescaped.substring(1, unescaped.length() - 1);
            final ByteString bytes = ByteString.copyFromUtf8(unescaped);
            final Any converted = Any.parser().parseFrom(bytes);
            return converted;
        } catch (InvalidProtocolBufferException e) {
            log.error("Could not serialize data, returning input");
            return any;
        }
    }

}
