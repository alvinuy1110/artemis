package com.myproject.artemis.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class MessageHolder {

    private Map<String, Object> headers=new HashMap<>();
    private String message;
}
