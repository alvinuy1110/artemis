TOPIC DEMO
==========

Consumer
========

# Steps

1. Define the consumer by implementing @JmsListener
1. Define the properties used by consumer
1. Define DefaultMessageListenerContainer which manages the listener and connection to MQ (see MessagingAnnotationConfig)
1. Define a JMSTemplate for publishing (see MessagingConfig)

UI
==

Artemis UI (management console) is powered by Hawt.io.  Since we are using Spring Boot, we will use it here as well.

# Dependency
```
<dependency>
  <groupId>io.hawt</groupId>
  <artifactId>hawtio-springboot</artifactId>
  <version>2.9.1</version>
</dependency>
```
Note: change version accordingly

# Actuator
```
## to expose specific endpoints for hawtio, jolokia
management.endpoints.web.exposure.include=hawtio,jolokia

## to customize the url for the console relative to actuator path  Default is actuator/hawtio
management.endpoints.web.path-mapping.hawtio=hawtio/console
```

## UI URL

Example: Default
```
http://localhost:8080/actuator/hawtio
```

Example: Customize HawtIo Url

if management.endpoints.web.path-mapping.hawtio=hawtio/console
```
http://localhost:8080/actuator/hawtio/console
```

## Security

To disable
```
hawtio.authenticationEnabled = false
```

# Navigating

1. Login to HawtIo console
1. Click on JMX
1. Search 'org.apache.activemq.artemis'
