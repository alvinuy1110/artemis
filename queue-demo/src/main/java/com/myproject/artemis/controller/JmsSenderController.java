package com.myproject.artemis.controller;

import com.myproject.artemis.domain.MessageHolder;
import com.myproject.artemis.exception.MQException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.jms.JMSException;
import java.util.Map;

/*
This is to allow publishing via HTTP
 */
@Controller
@Slf4j
public class JmsSenderController {

    @Autowired
    JmsTemplate jmsTemplate;

    @RequestMapping(value = "/sendToQueue", method = RequestMethod.POST)
    public ResponseEntity<?> sendToQueue(@RequestBody MessageHolder messageHolder) throws Exception {

        sendToJms(messageHolder);

        return ResponseEntity.ok().build();
    }

    private void sendToJms(MessageHolder messageHolder) {

        MessagePostProcessor postProcessor = (m) -> {
            Map<String, Object> map = messageHolder.getHeaders();
            // assign custom header
            if (!map.isEmpty()) {
                map.forEach((k, v) -> {
                    try {
                        m.setObjectProperty(k, v);
                    } catch (JMSException e) {
                        throw new MQException(e);
                    }
                    log.info("key:{}, value:{}", k, v);
                });

            }
            return m;
        };

        String messageStr = messageHolder.getMessage();

        jmsTemplate.convertAndSend((Object) messageStr, postProcessor);

    }
}

