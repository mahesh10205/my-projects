package com.cool.learn.advancekafka.twitterproducer;


import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


public class TwitterProdcuer {
    Logger log = LoggerFactory.getLogger(TwitterProdcuer.class.getName());
    List<String> terms = Lists.newArrayList("USA","sports","bitcoin");

    String consumerKey = "Qj8dcXg8gs7MrNgiFNvE5Lt8H";
    String consumerSecret = "MsYqxqOepnGHFS5yUwkuAwUbjE77rjeyZdRL6o4zuS5LjLuw6e";
    String token = "1158427406815903745-LBc72lnv9ouPycd9rfwIddFV4drWY7";
    String secret = "bxEWHp8SznoZ0ybhxtBWGlmER4IzX4P3iNzzHH4TBBXhz";

    public static void main(String[] args) {
        new TwitterProdcuer().run();

    }

    public void run() {

        /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1);
        //create twitter client
        Client client = createTwitterClient(msgQueue);

        client.connect();
        //create kafka prodcuer

        KafkaProducer<String, String> producer = createKafkaProdcuer();

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            log.info("Shutting down client and kafka producer");
            log.info("Closing twitter client!!!!!!!");
            client.stop();
            log.info("Closing kafka prodcuer!!!!");
            producer.close();
            log.info("Done!!!!!!!!!");
        } ));
        //send messages to kafka

        while (!client.isDone()) {
            String msg = null;
            try {
                msg = msgQueue.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                client.stop();
            }
            if (msg != null) {
                log.info(msg);
                producer.send(new ProducerRecord<String, String>("twitter_topic", "", msg), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                        if(e!=null){
                            log.error("Something bad happened!!!!!!!!!!!!!!");
                        }
                    }
                });
            }
            log.info("Appplication end !!!!!!!!!");
        }
    }

    private KafkaProducer<String, String> createKafkaProdcuer() {

        Properties properties = new Properties();

        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //create a safe producer

        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
        properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

        // create a high throughput producer(at the expense of a bit of latency and cpu usage)

        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG,"snappy");
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32*1024));

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        return producer;
    }

    public Client createTwitterClient(BlockingQueue<String> msgQueue) {

/** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */

        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();
// Optional: set up some followings and track terms
        hosebirdEndpoint.trackTerms(terms);

// These secrets should be read from a config file
        Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);
        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01")                              // optional: mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        // optional: use this if you want to process client events

        Client hosebirdClient = builder.build();
        return hosebirdClient;

    }
}
