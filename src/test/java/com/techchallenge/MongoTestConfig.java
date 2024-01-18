package com.techchallenge;


import com.techchallenge.application.usecases.MessageUseCase;
import com.techchallenge.application.usecases.interactor.MessageUseCaseInteractor;
import com.techchallenge.config.KafkaConfig;
import com.techchallenge.core.kafka.produce.TopicProducer;
import com.techchallenge.domain.entity.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static org.mockito.Mockito.mock;


@ComponentScan(value = {"com.techchallenge"})
@EnableMongoRepositories
public class MongoTestConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${kafka.topic.consumer.groupId}")
    private String groupId;

    @Primary
    @Bean
    public KafkaConfig kafkaConfig(){
        return mock(KafkaConfig.class);
    }

    @Primary
    @Bean
    public MessageUseCase messageUseCase(TopicProducer<Order> topicProducer){
        return mock(MessageUseCaseInteractor.class);
    }

    @Primary
    @Bean
    public TopicProducer<Order> topicProducer(){
        return mock(TopicProducer.class);
    }
}
