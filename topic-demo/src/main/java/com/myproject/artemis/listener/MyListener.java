package com.myproject.artemis.listener;

import com.myproject.artemis.exception.MQException;
import com.myproject.artemis.service.MessagingService;
import com.myproject.artemis.util.JmsMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import static com.myproject.artemis.service.MessagingConstants.JMS_HEADER_CUSTOM_MSG_ID;

@Slf4j
public class MyListener  {

    @Autowired
    private MessagingService messagingService;

    @JmsListener(destination = "testTopic", containerFactory = "jmsListenerContainerFactory", subscription = "a1")
    public void receive0(String message) {
        log.info("'subscriber0' received message='{}'", message);
    }
    @JmsListener(destination = "${messaging.config.test-topic.defaultDestinationName}")
    public void receive1(String message) {
        log.info("'subscriber1' received message='{}'", message);
    }

    @JmsListener(destination = "${messaging.config.test-topic.defaultDestinationName}")
    public void receive2(String message) {
        log.info("'subscriber2' received message='{}'", message);
    }
    @JmsListener(destination = "testTopic", containerFactory = "jmsListenerContainerFactory", subscription = "a2")
    public void onMessage(Message message) {

        try {
            log.info("Message received");

            JmsMessageUtil.debugProperties(message);;

            String customMsgId = message.getStringProperty(JMS_HEADER_CUSTOM_MSG_ID);
            if (customMsgId!=null) {
                messagingService.add(customMsgId, message);

            }
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                log.info("Message body {}", textMessage.getText());
            }

        } catch (JMSException e) {
            throw new MQException(e);
        }
    }
}
