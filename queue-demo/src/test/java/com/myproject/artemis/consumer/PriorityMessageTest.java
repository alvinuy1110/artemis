package com.myproject.artemis.consumer;

import com.myproject.artemis.config.JmsTestConfig;
import com.myproject.artemis.config.MessagingConfig;
import com.myproject.artemis.jms.QoSMessagePostProcessor;
import com.myproject.artemis.service.MessagingService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.BrowserCallback;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import java.util.Enumeration;

import static com.myproject.artemis.service.MessagingConstants.JMS_HEADER_CUSTOM_MSG_ID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {JmsTestConfig.class, MessagingConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE
        , properties = {
}
)
@Slf4j
public class PriorityMessageTest extends AbstractTestNGSpringContextTests {

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
    public void createMessageModifyHeader_sendToQueue_browseMessage() {

        // when
        String messageId = "TEST_HDR_ID";
        MessagePostProcessor postProcessor = (m) -> {
            // assign custom header
            m.setStringProperty(JMS_HEADER_CUSTOM_MSG_ID, messageId);

            // set priority at header level
            m.setIntProperty("priority", 9);
            return m;
        };

        String messageStr = "this is a test scheduled message";
        jmsTemplate.convertAndSend("testQueue", messageStr, postProcessor);

        // browse

        BrowserCallback browserCallback = new BrowserCallback() {
            @Override
            public Object doInJms(Session session, QueueBrowser queueBrowser) throws JMSException {
                Enumeration enumeration = queueBrowser.getEnumeration();

                while (enumeration.hasMoreElements()) {
                    Message msg = (Message) enumeration.nextElement();
                    messagingService.add(messageId, msg);

                }
                return null;
            }
        };
        jmsTemplate.browse("testQueue", browserCallback);
        Message message = messagingService.getMessage(messageId);
        assertThat
                (message).isNotNull();
        assertThat
                (message.getBody(Object.class)).isEqualTo(messageStr);

    }

    @Test
    @SneakyThrows
    public void createMessageViaJmsTemplate_sendToQueue_browseMessage() {

        // when
        String messageId = "TEST_HDR_ID";
        MessagePostProcessor postProcessor = (m) -> {
            // assign custom header
            m.setStringProperty(JMS_HEADER_CUSTOM_MSG_ID, messageId);

            return m;
        };

        String messageStr = "this is a test scheduled message";

        // Not this is not good for multi thread if you toggle priority per message
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setPriority(1);
        jmsTemplate.convertAndSend("testQueue", messageStr, postProcessor);

        // browse

        BrowserCallback browserCallback = new BrowserCallback() {
            @Override
            public Object doInJms(Session session, QueueBrowser queueBrowser) throws JMSException {
                Enumeration enumeration = queueBrowser.getEnumeration();

                while (enumeration.hasMoreElements()) {
                    Message msg = (Message) enumeration.nextElement();
                    messagingService.add(messageId, msg);

                }
                return null;
            }
        };
        jmsTemplate.browse("testQueue", browserCallback);
        Message message = messagingService.getMessage(messageId);
        assertThat
                (message).isNotNull();
        assertThat
                (message.getBody(Object.class)).isEqualTo(messageStr);

    }


    @Test
    @SneakyThrows
    public void createMessagesModifyHeader_sendToQueue_receiveMessage() {

        // when
        String messageId = "TEST_HDR_ID";
        String messageId2 = "TEST_HDR_ID2";
        String messageStr = "this is a test scheduled message";
        String messageStr2 = "this is a test scheduled message";

        jmsTemplate.convertAndSend("testQueue", messageStr, new QoSMessagePostProcessor(messageId, 1));
        jmsTemplate.convertAndSend("testQueue", messageStr2, new QoSMessagePostProcessor(messageId2, 9));
        // browse

        BrowserCallback browserCallback = new BrowserCallback() {
            @Override
            public Object doInJms(Session session, QueueBrowser queueBrowser) throws JMSException {
                Enumeration enumeration = queueBrowser.getEnumeration();

                while (enumeration.hasMoreElements()) {
                    Message msg = (Message) enumeration.nextElement();
                    String customMsgId = msg.getStringProperty(JMS_HEADER_CUSTOM_MSG_ID);
                    if (customMsgId != null) {
                        messagingService.add(customMsgId, msg);
                    }

                    log.info("Message: {}", msg);
                }
                return null;
            }
        };
        jmsTemplate.browse("testQueue", browserCallback);
        Message message = messagingService.getMessage(messageId);
        assertThat
                (message).isNotNull();
        assertThat
                (message.getBody(Object.class)).isEqualTo(messageStr);

        // receive message
        // message higher priority received
        Message message1 = jmsTemplate.receive();
        assertThat
                (message1).isNotNull();
        assertThat
                (message1.getBody(Object.class)).isEqualTo(messageStr2);

        Message message2 = jmsTemplate.receive();
        assertThat
                (message2).isNotNull();
        assertThat
                (message2.getBody(Object.class)).isEqualTo(messageStr);
    }

}
