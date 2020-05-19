package com.myproject.artemis.broker.config;

import org.apache.activemq.artemis.core.config.impl.SecurityConfiguration;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.spi.core.security.ActiveMQJAASSecurityManager;
import org.apache.activemq.artemis.spi.core.security.jaas.InVMLoginModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BrokerConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public EmbeddedActiveMQ embeddedActiveMQ() {
        // Step 1. Configure security.
        SecurityConfiguration securityConfig = new SecurityConfiguration();
        securityConfig.addUser("guest", "guest");
        securityConfig.addRole("guest", "guest");
        securityConfig.setDefaultUser("guest");
        ActiveMQJAASSecurityManager securityManager = new ActiveMQJAASSecurityManager(InVMLoginModule.class.getName(), securityConfig);

        EmbeddedActiveMQ embeddedActiveMQ = new EmbeddedActiveMQ();
        embeddedActiveMQ.setConfigResourcePath("broker.xml");
        embeddedActiveMQ.setSecurityManager(securityManager);
        return embeddedActiveMQ;
    }
}
