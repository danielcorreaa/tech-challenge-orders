package com.techchallenge;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@ComponentScan(value = {"com.techchallenge"})
@EnableMongoRepositories
public class KafkaTestConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${kafka.topic.consumer.groupId}")
    private String groupId;



}
