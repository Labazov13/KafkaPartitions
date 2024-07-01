package com.example.KafkaPartitions.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.train.maven.model.Event;
import org.train.maven.model.EventDto;

@Service
@AllArgsConstructor
public class EventService {
    private KafkaTemplate<String, String> kafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper;
    public Event createEvent(EventDto eventDto) {
        LOGGER.info("Create an event");
        Event event = new Event(eventDto.name(), eventDto.message());
        try {
            LOGGER.info("Trying to send a message to a topic");
            sendMessage(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return event;
    }
    public void sendMessage(Event event) throws JsonProcessingException {
        LOGGER.info("Sending a message to a topic");
        kafkaTemplate.send("event_users_topic", objectMapper.writeValueAsString(event));
    }

    @KafkaListener(topics = "event_users_topic")
    public void listenEventUsersTopic(String message) throws JsonProcessingException {
        Event event = objectMapper.readValue(message, Event.class);
        LOGGER.info(event.toString());
    }
}
