package com.techchallenge.config;


import com.techchallenge.application.usecases.MessageUseCase;
import com.techchallenge.application.usecases.OrderUseCase;
import com.techchallenge.application.usecases.interactor.MessageUseCaseInteractor;
import com.techchallenge.core.kafka.KafkaConsumerConfig;
import com.techchallenge.core.kafka.KafkaProducerConfig;
import com.techchallenge.core.kafka.produce.TopicProducer;
import com.techchallenge.domain.entity.Order;
import com.techchallenge.infrastructure.message.consumer.ProductionConsumer;
import com.techchallenge.infrastructure.message.consumer.dto.StatusDto;
import com.techchallenge.infrastructure.message.producer.OrderProduce;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${kafka.topic.produce.orders}")
    private String topic;

    @Value(value = "${kafka.topic.consumer.groupId}")
    private String groupId;

    @Bean
    public KafkaConsumerConfig kafkaConsumerConfig(){
        return new KafkaConsumerConfig(bootstrapAddress, groupId);
    }

    @Bean
    public ConsumerFactory<String, StatusDto> consumerFactoryStatusDto(){
        return kafkaConsumerConfig().consumerFactory(jsonDeserializer(new JsonDeserializer<>(StatusDto.class)));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StatusDto> kafkaListenerContainerFactoryStatusDto(){
        return kafkaConsumerConfig().kafkaListenerContainerFactory(consumerFactoryStatusDto());
    }

    @Bean
    public KafkaProducerConfig kafkaProducer(){
        return new KafkaProducerConfig(bootstrapAddress);
    }

    @Bean
    public ProducerFactory<String, Order> producerFactoryConfig(){
        return kafkaProducer().producerFactory();
    }

    @Bean
    public KafkaTemplate<String, Order> kafkaTemplateConfig() {
        return kafkaProducer().kafkaTemplate();
    }

    @Bean
    public ProductionConsumer orderConsumer(OrderUseCase orderUseCase){
        return new ProductionConsumer(orderUseCase);
    }

    @Bean
    public OrderProduce paymentProduce(MessageUseCase messageUseCase, OrderUseCase orderUseCase){
        return new OrderProduce(messageUseCase,orderUseCase);
    }

    @Bean
    public TopicProducer<Order> topicProducer(){
        return new TopicProducer<>(kafkaTemplateConfig(), topic);
    }

    @Bean
    public MessageUseCase messageUseCase(){
        return  new MessageUseCaseInteractor(topicProducer());
    }

    public <T> JsonDeserializer<T> jsonDeserializer(JsonDeserializer<T> deserializer){
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);
        return deserializer;
    }
}
