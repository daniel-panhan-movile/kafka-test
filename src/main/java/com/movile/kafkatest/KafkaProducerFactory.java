package com.movile.kafkatest;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class KafkaProducerFactory {

    private static Producer<String, String> diveoProducer;
    private static Producer<String, String> alogProducer;
    private static List<Producer<String, String>> localhostProducers;

    public static void initDiveo() {
        diveoProducer = new KafkaProducer<>(getProperties("jerico1.datac.movile.com:9092,jerico2.datac.movile.com:9092,jerico3.datac.movile.com:9092"));
    }

    public static void initAlog() {
        alogProducer = new KafkaProducer<>(getProperties("tangara1.datac.movile.com:9092,tangara2.datac.movile.com:9092,tangara3.datac.movile.com:9092"));
    }

    public static void initLocalhost(int amount) {
        localhostProducers = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Producer<String, String> localhostProducer = new KafkaProducer<>(getProperties("localhost:9092"));
            localhostProducers.add(localhostProducer);
        }
    }

    private static Properties getProperties(String servers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", servers);
        props.put("enable.auto.commit", true);
        props.put("acks", "1");
        props.put("retries", 5);
        props.put("batch.size", 16384);
        props.put("linger.ms", 10); // integer
        props.put("buffer.memory", 33554432L);
        props.put("request.timeout.ms", 60000);
        props.put("compression.type", "gzip");

        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        return props;
    }

    public static Producer<String, String> getDiveoProducer() {
        return diveoProducer;
    }
    public static Producer<String, String> getAlogProducer() {
        return alogProducer;
    }
    public static List<Producer<String, String>> getLocalhostProducers() {
        return localhostProducers;
    }

    public static void shutdown() {
        try {
            if (diveoProducer != null) {
                diveoProducer.close(10, TimeUnit.SECONDS);
            }
            if (alogProducer != null) {
                alogProducer.close(10, TimeUnit.SECONDS);
            }
            if (localhostProducers != null) {
                for (Producer<String, String> localhostProducer : localhostProducers) {
                    localhostProducer.close(10, TimeUnit.SECONDS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
