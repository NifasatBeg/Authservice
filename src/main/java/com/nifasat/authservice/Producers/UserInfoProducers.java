package com.nifasat.authservice.Producers;

import com.nifasat.authservice.model.UserInfoDto;
import com.nifasat.authservice.model.UserInfoEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class UserInfoProducers {
    private final KafkaTemplate<String, UserInfoDto>kafkaTemplate;

    @Value("${spring.kafka.topic.name}")
    private String TOPIC_NAME;
    @Autowired
    UserInfoProducers(KafkaTemplate<String, UserInfoDto> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEventToKafka(UserInfoEvent userInfoEvent){
        Message<UserInfoEvent> message = MessageBuilder
                .withPayload(userInfoEvent)
                .setHeader(KafkaHeaders.TOPIC, TOPIC_NAME)
                .build();
        kafkaTemplate.send(message);
    }
}
