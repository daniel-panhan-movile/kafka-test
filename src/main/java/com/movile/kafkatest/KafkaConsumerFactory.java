package com.movile.kafkatest;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class KafkaConsumerFactory {

    private static Consumer<String, String> diveoConsumer;
    private static Consumer<String, String> alogConsumer;
    private static List<Consumer<String, String>> localhostConsumers;

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

    public static void initLocalhost(int amount) {
        localhostConsumers = new ArrayList<>();
        Properties props = getProperties("localhost:9092");
        for (int i = 0; i < amount; i++) {
            Consumer<String, String> localhostConsumer = new KafkaConsumer<>(props, new StringDeserializer(), new StringDeserializer());
            localhostConsumer.subscribe(Collections.singletonList("MOVILE.CORP.TEST"));
            localhostConsumers.add(localhostConsumer);
        }
    }

    private static Properties getProperties(String servers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", servers);
        props.put("group.id", "TEST_GROUP_ID");
        props.put("auto.offset.reset", "earliest");
        props.put("enable.auto.commit", "false");
        props.put("request.timeout.ms", 60_000);
        props.put("session.timeout.ms", 10_000);
        props.put("max.poll.records", 100);
        props.put("heartbeat.interval.ms", 3_000);

        return props;
    }

    public static Consumer<String, String> getDiveoConsumer() {
        return diveoConsumer;
    }

    public static Consumer<String, String> getAlogConsumer() {
        return alogConsumer;
    }

    public static List<Consumer<String, String>> getLocalhostConsumers() {
        return localhostConsumers;
    }

    public static void shutdown() {
        try {
            if (diveoConsumer != null) {
                diveoConsumer.close(Duration.ofSeconds(10));
            }
            if (alogConsumer != null) {
                alogConsumer.close(Duration.ofSeconds(10));
            }
            if (localhostConsumers != null) {
                for (Consumer<String, String> localhostConsumer : localhostConsumers) {
                    localhostConsumer.close(Duration.ofSeconds(10));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
