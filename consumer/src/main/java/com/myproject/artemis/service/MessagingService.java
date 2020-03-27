package com.myproject.artemis.service;

import javax.jms.Message;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MessagingService {

    private Map<String, Message> messageMap = new ConcurrentHashMap<>();
    public void add(String id, Message message) {
        messageMap.put(id, message);
    }

    public Message getMessage(String id) {
        return messageMap.get(id);
    }


    public Message removeMessage(String id) {
        return messageMap.remove(id);
    }

    public void deleteAll() {
        messageMap.clear();
    }

}
