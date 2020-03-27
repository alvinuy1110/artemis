package com.myproject.artemis.consumer;

import com.myproject.artemis.config.JmsTestConfig;
import com.myproject.artemis.config.MessagingConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.jms.Message;

import static com.myproject.artemis.service.MessagingConstants.JMS_HEADER_CUSTOM_MSG_ID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {JmsTestConfig.class, MessagingConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE
        , properties = {
}
)
@Slf4j
public class DelayedMessageTest extends AbstractTestNGSpringContextTests {

    @Autowired
    JmsTemplate jmsTemplate;

    @BeforeMethod
    public void setupMethod() {

    }

    @Test
    @SneakyThrows
    public void createMessage_sendToQueue_messageReceived() {

        // when
        String messageId = "TEST_HDR_ID";
        MessagePostProcessor postProcessor = (m) -> {
            // assign custom header
            m.setStringProperty(JMS_HEADER_CUSTOM_MSG_ID, messageId);

            // add the Artemis property to trigger the delay; relative to current time
            m.setLongProperty(  org.apache.activemq.artemis.api.core.Message.HDR_SCHEDULED_DELIVERY_TIME.toString(),
                    System.currentTimeMillis() + 5000);
            return m;
        };

        String messageStr = "this is a test scheduled message";
        jmsTemplate.convertAndSend("testQueue", messageStr, postProcessor);

        // allow consumer time to process
        // set timeout slightly higher than delay
        jmsTemplate.setReceiveTimeout(6000);
        // wait for message to come in
        Message message = jmsTemplate.receive();
        // then
        assertThat
                (message).isNotNull();
        assertThat
                (message.getBody(Object.class)).isEqualTo(messageStr);

    }
}
