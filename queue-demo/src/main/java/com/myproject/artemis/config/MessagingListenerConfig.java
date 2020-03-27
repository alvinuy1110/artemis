package com.myproject.artemis.config;

import com.myproject.artemis.config.properties.MessagingConfigProperties;
import com.myproject.artemis.listener.MyListener;
import com.myproject.artemis.service.MessagingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
public class MessagingListenerConfig {

    @Autowired
    ConnectionFactory connectionFactory;

    @Bean("testQueueListener")
    public MessageListener myListener() {
        return new MyListener();
    }

    @Bean("testQueueListenerContainer")
    public DefaultMessageListenerContainer testQueueListenerContainer(
            @Qualifier(value = "testQueueMessagingConfig") MessagingConfigProperties messagingConfigProperties,
            @Qualifier("testQueueListener") MessageListener messageListener,
            @Qualifier("jmsTransactionManager") PlatformTransactionManager transactionManager) throws JMSException {

        DefaultMessageListenerContainer defaultMessageListenerContainer =
                new DefaultMessageListenerContainer();
        defaultMessageListenerContainer.setConnectionFactory(connectionFactory);
        defaultMessageListenerContainer.setDestinationName(messagingConfigProperties.getDefaultDestinationName());
        defaultMessageListenerContainer.setMessageListener(messageListener);
//        defaultMessageListenerContainer.setErrorHandler(new JmsErrorHandler());
        defaultMessageListenerContainer.setConcurrentConsumers(messagingConfigProperties.getConcurrentConsumers());
        defaultMessageListenerContainer.setMaxConcurrentConsumers(messagingConfigProperties.getMaxConcurrentConsumers());
        defaultMessageListenerContainer.setSessionTransacted(true);
        defaultMessageListenerContainer.setPubSubDomain(false);
        defaultMessageListenerContainer.setTransactionManager(transactionManager);

        return defaultMessageListenerContainer;
    }

}
