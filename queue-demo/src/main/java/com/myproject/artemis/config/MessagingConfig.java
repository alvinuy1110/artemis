package com.myproject.artemis.config;

import com.myproject.artemis.config.properties.MessagingConfigProperties;
import com.myproject.artemis.listener.MyListener;
import com.myproject.artemis.service.MessagingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.PlatformTransactionManager;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Session;

@Configuration
@Slf4j
@EnableConfigurationProperties
public class MessagingConfig {


    @Autowired
    ConnectionFactory connectionFactory;

    @Bean("testQueueMessagingConfig")
    @ConfigurationProperties(prefix = "messaging.config.test-queue")
    public MessagingConfigProperties testQueueMessagingConfigProperties() {
        return new MessagingConfigProperties();
    }


    @Bean
    public MessagingService messagingService() {
        return new MessagingService();
    }

    @Bean("testQueueListener")
    public MessageListener myListener() {
        return new MyListener();
    }

    @Bean
    public JmsTemplate jmsTemplate(@Qualifier(value = "testQueueMessagingConfig") MessagingConfigProperties messagingConfigProperties) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(false);
        jmsTemplate.setSessionTransacted(true);
        jmsTemplate.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        jmsTemplate.setDefaultDestinationName(messagingConfigProperties.getDefaultDestinationName());
        return jmsTemplate;
    }

    @Bean(value = "jmsTransactionManager")
    public PlatformTransactionManager jmsTransactionManager() {
        return new JmsTransactionManager(connectionFactory);
    }

}
