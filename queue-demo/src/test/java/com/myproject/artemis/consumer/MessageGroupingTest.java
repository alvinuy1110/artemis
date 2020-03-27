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

import java.util.HashMap;
import java.util.Map;

import static com.myproject.artemis.service.MessagingConstants.JMS_HEADER_CUSTOM_MSG_ID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {JmsTestConfig.class, MessagingConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE
        , properties = {
}
)
@Slf4j
public class MessageGroupingTest extends AbstractTestNGSpringContextTests {

    @Autowired
    JmsTemplate jmsTemplate;

    @BeforeMethod
    public void setupMethod() {
    }

    @Test
    @SneakyThrows
    public void createMessagesInGroups_sendToQueue_receiveGroupMessage() {

        // when
        String messageId = "TEST_HDR_ID";
        String messageId2 = "TEST_HDR_ID2";
        String messageStr = "this is a test scheduled message";
        String messageStr2 = "this is a test scheduled message";

        String group1 = "GroupABC";
        Map<String, Object> map = new HashMap<>();
        map.put(org.apache.activemq.artemis.api.core.Message.HDR_GROUP_ID.toString(), group1);
        jmsTemplate.convertAndSend("testQueue", messageStr, new QoSMessagePostProcessor(messageId, 1, map));
        jmsTemplate.convertAndSend("testQueue", messageStr2, new QoSMessagePostProcessor(messageId2, 9, map));

        // The message grouping will be the responsibility of the broker to assign to a single consumer

        jmsTemplate.setReceiveTimeout(3000L);

        // then
        Message message1 = jmsTemplate.receiveSelected(JMS_HEADER_CUSTOM_MSG_ID + "='" + messageId2 + "'");
        System.out.println("m1>>" +message1);
        assertThat
                (message1).isNotNull();
        assertThat
                (message1.getBody(Object.class)).isEqualTo(messageStr2);

        Message message2 = jmsTemplate.receiveSelected(JMS_HEADER_CUSTOM_MSG_ID + " = '" + messageId + "'");
        System.out.println("m2>>" +message2);
        assertThat
                (message2).isNotNull();
        assertThat
                (message2.getBody(Object.class)).isEqualTo(messageStr);
    }

}
