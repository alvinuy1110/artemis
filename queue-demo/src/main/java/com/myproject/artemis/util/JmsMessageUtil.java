package com.myproject.artemis.util;

import lombok.extern.slf4j.Slf4j;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Enumeration;

@Slf4j
public class JmsMessageUtil {


    public static void debugProperties(Message message) throws JMSException {

        Enumeration enumeration = message.getPropertyNames();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                String name = (String) enumeration.nextElement();

                log.debug("Property: {}, value:{}", name, message.getStringProperty(name));

            }
        }
    }
}
