package com.presbo.presboservice.utility;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
public class MessagingUtility {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public <T> T convertSendAndReceiveAsTypeAndRemoveTypeIdHeader(String exchange, String routingKey, Object data, Class<T> expectedClassType) throws Exception {


        ParameterizedTypeReference<Object> parameterizedTypeReference = new ParameterizedTypeReference<Object>() {};
        Object result = rabbitTemplate.convertSendAndReceiveAsType(exchange, routingKey, data, m -> {
            m.getMessageProperties().getHeaders().remove("__TypeId__");
            return m;
        }, parameterizedTypeReference);

        String objString = JsonUtility.convertObjectToString(result);

        return  JsonUtility.convertStringToObject(objString, expectedClassType);

    }


}
