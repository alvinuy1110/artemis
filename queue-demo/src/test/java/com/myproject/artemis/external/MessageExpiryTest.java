package com.myproject.artemis.external;

import com.myproject.artemis.config.JmsTestConfig;
import com.myproject.artemis.config.MessagingConfig;
import com.myproject.artemis.service.MessagingService;
import com.myproject.artemis.util.JmsMessageUtil;
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
        , properties = {"spring.profiles.active=external"
}
)
@Slf4j
public class MessageExpiryTest extends AbstractTestNGSpringContextTests {

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
    public void createMessage_sendToQueue_browseMessage() {

        long messageExpiry = 2000;
        String queueName ="test.Queue";
        String expiryQueueName ="ExpiryQueueTest";

        // when
        String messageId = "TEST_HDR_ID";
        MessagePostProcessor postProcessor = (m) -> {
            // assign custom header
            m.setStringProperty(JMS_HEADER_CUSTOM_MSG_ID, messageId);

            // in milliseconds; relative to current time
           // m.setJMSExpiration(System.currentTimeMillis() + messageExpiry);

            return m;
        };

        String messageStr = "this is a test expired message";
messageStr += " " + System.currentTimeMillis();
        // must be turned on
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setTimeToLive(messageExpiry); // this is global to the template

        // per message wont work because of the way jmsTemplate doSend is coded!!

        log.info("Message: {}", messageStr);
        jmsTemplate.convertAndSend(queueName, messageStr, postProcessor);

        // browse

        BrowserCallback browserCallback = new BrowserCallback() {
            @Override
            public Object doInJms(Session session, QueueBrowser queueBrowser) throws JMSException {
                Enumeration enumeration = queueBrowser.getEnumeration();

                while (enumeration.hasMoreElements()) {
                    Message msg = (Message) enumeration.nextElement();

                    JmsMessageUtil.debugProperties(msg);
                    messagingService.add(messageId, msg);

                }
                return null;
            }
        };

        Thread.sleep(messageExpiry+3000);
        // check expired queue
        jmsTemplate.browse(expiryQueueName, browserCallback);
        Message message = messagingService.getMessage(messageId);
        assertThat
                (message).isNotNull();
        assertThat
                (message.getBody(Object.class)).isEqualTo(messageStr);

    }


}
