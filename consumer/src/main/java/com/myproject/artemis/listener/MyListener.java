package com.myproject.artemis.listener;

import com.myproject.artemis.exception.MQException;
import com.myproject.artemis.service.MessagingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import static com.myproject.artemis.service.MessagingConstants.JMS_HEADER_CUSTOM_MSG_ID;

@Slf4j
public class MyListener implements MessageListener {

    @Autowired
    private MessagingService messagingService;

    @Override
    public void onMessage(Message message) {

        try {
            log.info("Message received {}", message.getBody(Object.class));

            messagingService.add(message.getStringProperty(JMS_HEADER_CUSTOM_MSG_ID), message);

        } catch (JMSException e) {
            throw new MQException(e);
        }
    }
}
