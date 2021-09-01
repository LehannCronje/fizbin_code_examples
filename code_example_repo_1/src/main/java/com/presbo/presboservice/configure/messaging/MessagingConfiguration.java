package com.presbo.presboservice.configure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

@Configuration
@EnableRabbit
public class MessagingConfiguration implements RabbitListenerConfigurer {

    static final String DIRECT_EXCHANGE = "direct-exchange";
    public static final String QUEUE_UPLOAD_DOCUMENT = "upload-document-queue";
    public static final String QUEUE_GET_DOCUMENT = "get-document-queue";
    public static final String QUEUE_GENERATE_REPORT = "generate-report-queue";
    public static final String QUEUE_GET_REPORT = "get-report-queue";
    public static final String QUEUE_PERSIST_RESOURCE = "persist-resource-queue";


    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE);
    }

    @Bean
    public Queue uploadDocumentQueue(){
        return new Queue(QUEUE_UPLOAD_DOCUMENT, false);
    }

    @Bean
    public Queue getDocumentQueue(){
        return new Queue(QUEUE_GET_DOCUMENT, false);
    }

    @Bean
    public Queue generateReportQueue(){
        return new Queue(QUEUE_GENERATE_REPORT, false);
    }

    @Bean
    public Queue getReportQueue(){
        return new Queue(QUEUE_GET_REPORT, false);
    }

    @Bean
    public Queue persistResourceQueue(){return new Queue(QUEUE_PERSIST_RESOURCE); };

    @Bean
    public Binding uploadDocumentBinding(Queue uploadDocumentQueue, DirectExchange exchange){
        return BindingBuilder.bind(uploadDocumentQueue).to(exchange).with("upload-document");
    }

    @Bean
    public Binding getDocumentBinding(Queue getDocumentQueue, DirectExchange exchange){
        return BindingBuilder.bind(getDocumentQueue).to(exchange).with("get-document");
    }

    @Bean
    public Binding generateReportBinding(Queue generateReportQueue, DirectExchange exchange){
        return BindingBuilder.bind(generateReportQueue).to(exchange).with("generate-report");
    }


    @Bean
    public Binding getReportBinding(Queue getReportQueue, DirectExchange exchange){
        return BindingBuilder.bind(getReportQueue).to(exchange).with("get-report");
    }

    @Bean
    public Binding persistResourceBinding(Queue persistResourceQueue, DirectExchange exchange){
        return BindingBuilder.bind(persistResourceQueue).to(exchange).with("persist-resource");
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Autowired
    public ConnectionFactory connectionFactory;


    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        rabbitTemplate.setReplyTimeout(60000);
        return rabbitTemplate;
    }

    @Override
    public void configureRabbitListeners(
            RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(myHandlerMethodFactory());
    }

    @Bean
    public DefaultMessageHandlerMethodFactory myHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(new MappingJackson2MessageConverter());
        return factory;
    }

}