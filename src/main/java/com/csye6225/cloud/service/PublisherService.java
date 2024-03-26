package com.csye6225.cloud.service;

import com.csye6225.cloud.dto.PublishResponseDTO;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class PublisherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublisherService.class);

    @Value("${publisher.topic}")
    private String topicName;

    private final PubSubTemplate pubSubTemplate;

    public void prepareToPublish(String email){
        PublishResponseDTO publishResponseDTO = new PublishResponseDTO();
        publishResponseDTO.setEmail(email);
        publishMessage(publishResponseDTO.toString());
    }
    public void publishMessage(String message) {
        LOGGER.debug("Publishing {} topic {}", message, topicName);
        CompletableFuture<String> future = pubSubTemplate.publish(topicName, message);

        future.thenAccept(result -> LOGGER.debug("Message {} was sent successfully with msg id {}", message, result))
                .exceptionally(e -> {
                    LOGGER.error("An error occurred while sending message {} : {}", message, e.getMessage());
                    return null;
                });
    }
}
