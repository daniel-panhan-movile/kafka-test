package com.movile.kafkatest;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class KafkaTestMain {

    public static void main(String[] args) throws Exception {

//        KafkaProducerFactory.initDiveo();
//        KafkaProducerFactory.initAlog();
//        KafkaConsumerFactory.initDiveo();
//        KafkaConsumerFactory.initAlog();

        int producersCount = 10;
        int consumersCount = 10;

        KafkaProducerFactory.initLocalhost(producersCount);
        KafkaConsumerFactory.initLocalhost(consumersCount);

        System.out.println("--- PRODUCER LOCALHOST ---");
        List<Thread> threads = new ArrayList<>();
        for (int pIdx = 0; pIdx < producersCount; pIdx++) {
            Thread t = new ThreadProducer(pIdx, producersCount);
            t.start();
            threads.add(t);
        }
        for (Thread t : threads) {
            t.join();
        }
//
//        System.out.println("--- PRODUCER DIVEO ---");
//
//        sendMessage(KafkaProducerFactory.getDiveoProducer(),"key1", "test1");
//        sendMessage(KafkaProducerFactory.getDiveoProducer(),"key2", "test2");
//        sendMessage(KafkaProducerFactory.getDiveoProducer(),"key3", "test3");
//
//        System.out.println("--- PRODUCER ALOG ---");
//
//        sendMessage(KafkaProducerFactory.getAlogProducer(),"key1", "test1");
//        sendMessage(KafkaProducerFactory.getAlogProducer(),"key2", "test2");
//        sendMessage(KafkaProducerFactory.getAlogProducer(),"key3", "test3");
//
//        System.out.println("--- CONSUMER ---");
//        for (int i = 0; i < 10; i++) {
//            receiveMessage(KafkaConsumerFactory.getDiveoConsumer());
//            receiveMessage(KafkaConsumerFactory.getAlogConsumer());
//        }

        Thread.sleep(100);

        System.out.println("--- CONSUMER ---");
        List<Thread> threads2 = new ArrayList<>();
        for (int cIdx = 0; cIdx < consumersCount; cIdx++) {
            Thread t = new ThreadConsumer(cIdx);
            t.start();
            threads2.add(t);
        }
//        for (Thread t2 : threads2) {
//            t2.join();
//        }

        System.out.println("Waiting to finish...");
        Thread.sleep(10000);

        KafkaProducerFactory.shutdown();
//        KafkaConsumerFactory.shutdown();
    }

    private static void sendMessage(Producer<String, String> producer, String key, String value, int partition) {
        try {
            System.out.println("key: " + key + ", value: " + value + ", partition: " + partition);

            ProducerRecord<String, String> record = new ProducerRecord<>("MOVILE.CORP.TEST", partition, key, value);
            //record.headers().add("header1", "value_header1".getBytes());
            Future<RecordMetadata> future = producer.send(record);
//
//            RecordMetadata recordMetadata = future.get(10, TimeUnit.SECONDS);
//            System.out.println("partition: " + recordMetadata.partition() + ", offset: " + recordMetadata.offset() + ", timestamp: " + Instant.ofEpochMilli(recordMetadata.timestamp()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void receiveMessage(int poolCount, int cIdx, Consumer<String, String> consumer) {
        try {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));

            System.out.println("poolCount: " + poolCount + ", cIdx: " + cIdx + ", total: " + records.count());
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("poolCount: " + poolCount + ", cIdx: " + cIdx + ", total: " + records.count() +  ", partition: " + record.partition() + ", key: " + record.key() + ", value: " + record.value());
            }

            consumer.commitAsync();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static class ThreadProducer extends Thread {
        private int pIdx;
        private int producersCount;

        public ThreadProducer(int pIdx, int producersCount) {
            this.pIdx = pIdx;
            this.producersCount = producersCount;
        }

        @Override
        public void run() {
            for (int i = 0; i < 1000; i++) {
                int k = ((i*producersCount) + pIdx) % 1000;
                sendMessage(KafkaProducerFactory.getLocalhostProducers().get(pIdx), "key" + k, "test" + k, k);
            }
        }
    }

    public static class ThreadConsumer extends Thread {
        private int cIdx;

        public ThreadConsumer(int cIdx) {
            this.cIdx = cIdx;
        }

        @Override
        public void run() {
            for (int i = 0; i < 50; i++) {
                receiveMessage(i, cIdx, KafkaConsumerFactory.getLocalhostConsumers().get(cIdx));
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
