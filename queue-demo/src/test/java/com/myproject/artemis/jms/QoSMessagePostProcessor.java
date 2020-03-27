package com.myproject.artemis.jms;


import com.myproject.artemis.exception.MQException;
import lombok.AllArgsConstructor;
import org.springframework.jms.core.MessagePostProcessor;

import javax.jms.JMSException;
import javax.jms.Message;

import java.util.HashMap;
import java.util.Map;

import static com.myproject.artemis.service.MessagingConstants.JMS_HEADER_CUSTOM_MSG_ID;

public class QoSMessagePostProcessor implements MessagePostProcessor {

    private String messageId;
    private int priority;
    private Map<String, Object> map;
    public QoSMessagePostProcessor(String messageId, int priority) {
        this.messageId = messageId;
        this.priority = priority;
        map = new HashMap<>();
    }
    public QoSMessagePostProcessor(String messageId, int priority,Map<String, Object> map) {
        this(messageId, priority);
        this.map = map;
    }

    @Override
    public Message postProcessMessage(Message m) throws JMSException {
        // assign custom header
        m.setStringProperty(JMS_HEADER_CUSTOM_MSG_ID, messageId);

        if (!map.isEmpty()) {
            map.forEach((k,v) -> {
                try {
                    m.setObjectProperty(k, v);
                } catch (JMSException e) {
                    throw new MQException(e);
                }
            });

        }
        // set priority at header level
        m.setIntProperty("priority", priority);
        return m;
    }

    ;

}

