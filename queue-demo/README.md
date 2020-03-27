QUEUE DEMO
==========

# Steps

1. Define the consumer by implementing MessageListener
1. Define the properties used by consumer
1. Define DefaultMessageListenerContainer which manages the listener and connection to MQ
1. Define a JMSTemplate for publishing


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

## Publishing

1. Select the queue
1. In Operations tab, select sendMessage

|Parameter|Description|Example|
|---------|-----------|-------|
|headers |JSON format; JMS headers|{"h1":"v1"}|
|type| JMS message type; (3) for TextMessage|3|
|body| JMS payload||
|durable| true if durable||

### Send test message via HTTP

Send a POST to 'http://localhost:10080/sendToQueue'
Example: Unix
```
curl -kv -X POST -H "Application/json" --data '{"headers":{"artemis_custom_hdr":"testId","priority":1},"message":"this is a test message"}' \ http://localhost:10080/sendToQueue
```

Example: Windows
```
curl -kv -X POST -H "Content-Type: application/json" --data "{\"headers\":{\"artemis_custom_hdr\":\"testId\",\"priority\":1},\"message\":\"this is a test message\"}" \ http://localhost:10080/sendToQueue
```

# Features

* Simple publish-consume (see ConsumerTest)
* Delayed message (see DelayedMessageTest)
* Browse message (see BrowseMessageTest)
* Priority message (see PriorityMessageTest)
* Selector message (see SelectorMessageTest)
* Message grouping (see MessageGroupingTest)


# Troubleshooting

## Cause: AMQ229017: Queue testQueue does not exist
As per https://access.redhat.com/solutions/4236881, queues/ topic does not support dynamic creation.  

Also need to set the flag, to prevent from being destroyed.
```
<auto-delete-queues>false</auto-delete-queues> 

or toggle 'Purge when no consumers'
```

Note: 
In Spring, this error is actually ignored and dynamic creation actually happens,  The destination is detroyed afterwards.


