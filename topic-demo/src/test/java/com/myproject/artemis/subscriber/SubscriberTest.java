package com.myproject.artemis.subscriber;

import com.myproject.artemis.ArtemisTopicApp;
import com.myproject.artemis.service.MessagingService;
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

@SpringBootTest(classes = {ArtemisTopicApp.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE
        , properties = {
}
)
@Slf4j
public class SubscriberTest extends AbstractTestNGSpringContextTests {

    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    MessagingService messagingService;

    @BeforeMethod
    public void setupMethod() {
        messagingService.deleteAll();
    }

    @Test
    @SneakyThrows
    public void createMessage_sendToTopic_messageReceived() {

        // when
        String messageId = "TEST_HDR_ID";
        MessagePostProcessor postProcessor = (m) -> {
            // assign custom header
            m.setStringProperty(JMS_HEADER_CUSTOM_MSG_ID, messageId);
            return m;
        };
        String messageStr = "this is a test message";
        jmsTemplate.convertAndSend("testTopic", messageStr, postProcessor);

        // allow consumer time to process
        Thread.sleep(500L);
        // then
        Message message = messagingService.getMessage(messageId);
        assertThat
                (message).isNotNull();
        assertThat
                (message.getBody(Object.class)).isEqualTo(messageStr);

    }
}
