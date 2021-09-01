package com.presbo.presboservice.messaging.sender;

import com.presbo.presboservice.dto.req.PersistResourceReqDto;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersistResourceMsgSender {
    
    @Autowired
    RabbitTemplate rabbitTemplate;

    public void persistResourceMsgSender(String exchange, String routingKey, PersistResourceReqDto data) throws Exception {


        rabbitTemplate.convertAndSend(exchange, routingKey, data);


    }

}