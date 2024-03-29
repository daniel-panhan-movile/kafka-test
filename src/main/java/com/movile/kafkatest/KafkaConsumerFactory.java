package com.movile.kafkatest;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class KafkaConsumerFactory {

    private static Consumer<String, String> diveoConsumer;
    private static Consumer<String, String> alogConsumer;

    public static void initDiveo() {
        Properties props = getProperties("jerico1.datac.movile.com:9092,jerico2.datac.movile.com:9092,jerico3.datac.movile.com:9092");
        diveoConsumer = new KafkaConsumer<>(props, new StringDeserializer(), new StringDeserializer());
        diveoConsumer.subscribe(Collections.singletonList("MOVILE.CORP.TEST"));
    }

    public static void initAlog() {
        Properties props = getProperties("tangara1.datac.movile.com:9092,tangara2.datac.movile.com:9092,tangara3.datac.movile.com:9092");
        alogConsumer = new KafkaConsumer<>(props, new StringDeserializer(), new StringDeserializer());
        alogConsumer.subscribe(Collections.singletonList("MOVILE.CORP.TEST"));
    }

    private static Properties getProperties(String servers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", servers);
        props.put("group.id", "TEST_GROUP_ID");
        props.put("auto.offset.reset", "earliest");
        props.put("enable.auto.commit", "true");
        props.put("request.timeout.ms", 190_000);
        props.put("session.timeout.ms", 180_000);
        props.put("max.poll.records", 1_000);
        props.put("heartbeat.interval.ms", 10_000);

        return props;
    }

    public static Consumer<String, String> getDiveoConsumer() {
        return diveoConsumer;
    }

    public static Consumer<String, String> getAlogConsumer() {
        return alogConsumer;
    }

    public static void shutdown() {
        try {
            diveoConsumer.close(10, TimeUnit.SECONDS);
            alogConsumer.close(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
