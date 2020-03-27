package com.myproject.artemis.consumer;

import com.myproject.artemis.config.JmsTestConfig;
import com.myproject.artemis.config.MessagingConfig;
import com.myproject.artemis.jms.QoSMessagePostProcessor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
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
public class SelectorMessageTest extends AbstractTestNGSpringContextTests {

    @Autowired
    JmsTemplate jmsTemplate;

    @BeforeMethod
    public void setupMethod() {
    }

    @Test
    @SneakyThrows
    public void createMessagesModifyHeader_sendToQueue_receiveSelectedMessage() {

        // when
        String messageId = "TEST_HDR_ID";
        String messageId2 = "TEST_HDR_ID2";
        String messageStr = "this is a test scheduled message";
        String messageStr2 = "this is a test scheduled message";

        jmsTemplate.convertAndSend("testQueue", messageStr, new QoSMessagePostProcessor(messageId, 1));
        jmsTemplate.convertAndSend("testQueue", messageStr2, new QoSMessagePostProcessor(messageId2, 9));

        jmsTemplate.setReceiveTimeout(3000L);

        // then
        Message message1 = jmsTemplate.receiveSelected(JMS_HEADER_CUSTOM_MSG_ID + "='" + messageId2 + "'");
        assertThat
                (message1).isNotNull();
        assertThat
                (message1.getBody(Object.class)).isEqualTo(messageStr2);

        Message message2 = jmsTemplate.receiveSelected(JMS_HEADER_CUSTOM_MSG_ID + " = '" + messageId + "'");
        assertThat
                (message2).isNotNull();
        assertThat
                (message2.getBody(Object.class)).isEqualTo(messageStr);
    }

}
