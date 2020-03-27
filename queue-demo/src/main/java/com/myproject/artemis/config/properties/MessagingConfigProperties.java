package com.myproject.artemis.config.properties;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Data
@Validated
public class MessagingConfigProperties {

    @NotEmpty
    private String defaultDestinationName;
    // in milliseconds
    private long receiveTimeout = 10000L;

    private int concurrentConsumers = 1;
    private int maxConcurrentConsumers = 5;
}
