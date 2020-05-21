package com.myproject.artemis.listener;

import com.myproject.artemis.exception.MQException;
import com.myproject.artemis.service.MessagingService;
import com.myproject.artemis.util.JmsMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import static com.myproject.artemis.service.MessagingConstants.JMS_HEADER_CUSTOM_MSG_ID;
import static com.myproject.artemis.service.MessagingConstants.JMS_HEADER_SHOULD_FAIL;

@Slf4j
public class MyListener implements MessageListener {

    @Autowired
    private MessagingService messagingService;

    @Override
    public void onMessage(Message message) {

        try {
            log.info("Message received");

            JmsMessageUtil.debugProperties(message);;

            boolean shouldFail = message.getBooleanProperty(JMS_HEADER_SHOULD_FAIL);
            log.info("shouldFail: {}", shouldFail);
            if (shouldFail) {

                throw new RuntimeException("Failure forced");
            }

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
