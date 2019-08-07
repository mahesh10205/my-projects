package com.cool.learn.advancekafka.producer;

import javafx.beans.property.Property;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProducerDemo {
    public static void main(String[] args) {
        Properties properties = new Properties();

        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);
        for (int i = 0; i <= 10; i++) {
            String topic = "first_topic";
            String message = "Hey Mahi!!!" + Integer.toString(i);
            String key = "Key0001" + Integer.toString(i);
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
            producer.send(record);
            producer.flush();
        }
        producer.close();
    }

}
