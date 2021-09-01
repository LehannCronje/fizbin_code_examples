package com.presbo.presboservice.messaging.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.presbo.presboservice.dto.req.PersistResourceReqDto;
import com.presbo.presboservice.service.ResourceService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static com.presbo.presboservice.configure.messaging.MessagingConfiguration.QUEUE_PERSIST_RESOURCE;

@Component
public class PersistResourceMsgHandler {
    
    @Autowired
    ResourceService resourceService;

    @RabbitListener(queues=QUEUE_PERSIST_RESOURCE)
    public void persistResourceMsgHandler(@Payload PersistResourceReqDto data) throws JsonProcessingException {

        resourceService.persistResource(data);

    }

}