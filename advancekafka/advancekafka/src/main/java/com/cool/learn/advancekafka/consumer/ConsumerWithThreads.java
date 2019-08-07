package com.cool.learn.advancekafka.consumer;

import ch.qos.logback.core.db.dialect.MySQLDialect;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.rmi.runtime.Log;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ConsumerWithThreads {
    Logger log = LoggerFactory.getLogger(ConsumerWithThreads.class);

    public static void main(String[] args) {
        new ConsumerWithThreads().run();
    }

    public ConsumerWithThreads() {
    }

    public void run() {
        CountDownLatch latch = new CountDownLatch(1);

        Runnable myRunnable = new MyRunnable(latch);

        Thread myThread = new Thread(myRunnable);
        myThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Caught shutdown hook!!!!!!!!!!!");
            ((MyRunnable) myRunnable).shutdown();
        }));

        try {
            log.info("My thread callled latch !!!!!!!!!!!!!!!!");
            latch.await();
        } catch (InterruptedException e) {
            log.info("Appplication got intterupted!!!!!!!!!!!!!!");
            e.printStackTrace();
        } finally {
            log.info("Apllication is closing !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    }

    public class MyRunnable implements Runnable {
        private CountDownLatch latch;
        private KafkaConsumer<String, String> consumer;

        Logger log = LoggerFactory.getLogger(MyRunnable.class);

       public  MyRunnable(CountDownLatch latch) {
            this.latch = latch;
            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "2nd_group");
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            consumer = new KafkaConsumer<String, String>(properties);
            consumer.subscribe(Collections.singleton("3rd_topic"));
        }

        @Override
        public void run() {
            try {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                    for (ConsumerRecord<String, String> record : records) {
                        System.out.println(record.value());
                    }
                }
            } catch (WakeupException e) {
                log.info("Recieved shutdown signal !!!!!!!!!!!!!!!!");
                e.printStackTrace();
            } finally {
                consumer.close();
                latch.countDown();
            }


        }

        public void shutdown() {
            consumer.wakeup();
        }
    }
}