package com.github.rodrigodata;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;

import java.util.Arrays;
import java.util.Properties;

public class WordCount {
    // psvm (shortcut)
    public static void main(String[] args) {
        Properties config = new Properties();

        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count-app");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        KStreamBuilder builder = new KStreamBuilder();
        // 1 - Stream from kafka
        KStream<String, String> wordCountInput = builder.stream("word-count-input");

        KTable<String, Long> wordCounts = wordCountInput
                // 2 - map values to lowercase
                .mapValues(value -> value.toUpperCase())
                // 3 - flatMapValues split by space
                .flatMapValues(value -> Arrays.asList(value.split("\\W+")))
                // 4 - replace key by value o word
                .selectKey((key, value) -> value)
                // 5 - Group By Key
                .groupByKey()
                // 6 - Count and store in variable named Counts
                .count("Counts");

        wordCounts.to( Serdes.String(), Serdes.Long(),"word-count-output");

        KafkaStreams streams = new KafkaStreams(builder, config);

        // start
        streams.start();

        // printed the topology
        System.out.println(streams.toString());

        // close application gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }
}
