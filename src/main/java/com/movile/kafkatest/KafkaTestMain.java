package com.movile.kafkatest;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class KafkaTestMain {

    public static void main(String[] args) throws Exception {

        KafkaProducerFactory.initDiveo();
        KafkaProducerFactory.initAlog();
        KafkaConsumerFactory.initDiveo();
        KafkaConsumerFactory.initAlog();

        System.out.println("--- PRODUCER DIVEO ---");

        sendMessage(KafkaProducerFactory.getDiveoProducer(),"key1", "test1");
        sendMessage(KafkaProducerFactory.getDiveoProducer(),"key2", "test2");
        sendMessage(KafkaProducerFactory.getDiveoProducer(),"key3", "test3");

        System.out.println("--- PRODUCER ALOG ---");

        sendMessage(KafkaProducerFactory.getAlogProducer(),"key1", "test1");
        sendMessage(KafkaProducerFactory.getAlogProducer(),"key2", "test2");
        sendMessage(KafkaProducerFactory.getAlogProducer(),"key3", "test3");

        System.out.println("--- CONSUMER ---");
        for (int i = 0; i < 10; i++) {
            receiveMessage(KafkaConsumerFactory.getDiveoConsumer());
            receiveMessage(KafkaConsumerFactory.getAlogConsumer());
        }

        Thread.sleep(10000);

        KafkaProducerFactory.shutdown();
        KafkaConsumerFactory.shutdown();
    }

    private static void sendMessage(Producer<String, String> producer, String key, String value) {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>("MOVILE.CORP.TEST", key, value);
            Future<RecordMetadata> future = producer.send(record);

            RecordMetadata recordMetadata = future.get(10, TimeUnit.SECONDS);
            System.out.println(ToStringBuilder.reflectionToString(recordMetadata, ToStringStyle.SIMPLE_STYLE));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void receiveMessage(Consumer<String, String> consumer) {
        try {
            ConsumerRecords<String, String> records = consumer.poll(500);

            for (ConsumerRecord<String, String> record : records) {
                System.out.println(ToStringBuilder.reflectionToString(record, ToStringStyle.SIMPLE_STYLE));
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
