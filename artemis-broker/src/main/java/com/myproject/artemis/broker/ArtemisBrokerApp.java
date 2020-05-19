package com.myproject.artemis.broker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;


@SpringBootApplication(exclude = {ActiveMQAutoConfiguration.class})
public class ArtemisBrokerApp {

    public static void main(String[] args) {
        SpringApplication.run(ArtemisBrokerApp.class, args);
    }

}
