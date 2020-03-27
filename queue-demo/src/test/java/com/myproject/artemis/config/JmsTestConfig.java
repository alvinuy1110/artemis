package com.myproject.artemis.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties
@EnableJms
public class JmsTestConfig {
}
