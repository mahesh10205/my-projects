package com.cool.learn.advancekafka.producer;

import com.google.gson.Gson;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerCallbacksDemo {


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Logger log = LoggerFactory.getLogger(ProducerCallbacksDemo.class);
        Properties properties = new Properties();

        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        for (int i = 0; i <= 10; i++) {
            String topic = "3rd_topic";
            String message = "Hey Mahi!!!" + Integer.toString(i);
            String key = "Key0001" + Integer.toString(i);
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, message);
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e == null) {
                        log.info("topic------ " + recordMetadata.topic() + "\n"
                                + "Partition------ " + recordMetadata.partition() + "\n"
                                + "offset------ " + recordMetadata.offset() + "\n"
                                + "key------ " + key);
                    } else {
                        log.info(e.getMessage());
                    }

                }
            }).get();
            producer.flush();
        }
        producer.close();
    }

}
