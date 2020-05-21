BROKER DEMO
==========

This uses Spring Boot to stand up an Artemis Broker.

The intent is to test specific broker settings like DLQ, Redelivery, etc.

# Configuration

See broker.xml for Artemis settings.

# Security

Security is programmatically set in BrokerConfig.

For more permissions, read https://activemq.apache.org/components/artemis/documentation/latest/security.html

# Persistence

This broker will store stuff under "./data" directory.  Delete the directory to reset if needed.


# HawtIO

HawtIO authentication is disabled for demo.

## UI URL

Example: Default
```
http://localhost:11080/actuator/hawtio/console
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

# Features

## Message Expiry
```
<!-- expired messages in exampleQueue will be sent to the expiry address expiryQueue -->
<address-setting match="exampleQueue">
   <expiry-address>expiryQueue</expiry-address>
   <expiry-delay>10</expiry-delay>
</address-setting>

expiry-delay will be the default, applied only if no value is set.  If value in message is set, will honor the value
-1 (default) dont override

```

Note:
* The match "test#" wont work even if queue name is "testQueue" but "test.#" works for "test.Queue"

### Solutions/ Scenarios

#### Spring JMSTemplate

```
	long messageExpiry = 2000;
       // must be turned on
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setTimeToLive(messageExpiry); // this is global to the template;  in milliseconds
```

#### Per Message
*  not working it seems
*  Spring JMSTemplate doSend doesnt honor per message.  Need to extend this is if we want to !!!!
*  Going direct vi MessageProducer does work!!


#### No expiry but using broker.xml values
    * define the expiration time in milliseconds

#### With expiry and broker.xml values
* message expiry is used instead of broker values

#### auto-create-expiry-resources (dynamic)
* available: 2.12.x +
* example:

```
            <address-setting match="test.#">
                <!-- expiry config -->
                <!-- dynamic expiry address; available in artemis v2.12.x+ -->

<!--                <auto-delete-queues>false</auto-delete-queues>-->
<!--                <auto-delete-addresses>false</auto-delete-addresses>-->

                <dead-letter-address>a1</dead-letter-address>
                <auto-create-dead-letter-resources>true</auto-create-dead-letter-resources>
                <dead-letter-queue-prefix></dead-letter-queue-prefix>
                <dead-letter-queue-suffix>.DLQ</dead-letter-queue-suffix>

		<!-- must specify one -->
                <expiry-address>a2</expiry-address>
                <!-- default false-->
                <auto-create-expiry-resources>true</auto-create-expiry-resources>
                <!-- default EXP. -->
                <expiry-queue-prefix></expiry-queue-prefix>
                <!-- default '' -->
                <expiry-queue-suffix>.EXP</expiry-queue-suffix>
                <!-- end of expiry config -->

```
Note: given a queue "test.Queue", the expiry address will be "a2" but the queue name will be a multicast "test.Queue.EXP"

This will allow a common setting for several addresses

#### No expiry address
* the message will be dropped

## Message Delivery, Retry, DLQ

https://github.com/apache/activemq-artemis/blob/master/docs/user-manual/en/undelivered-messages.md

### Solutions/ Scenarios

#### has settings, no DLA (dead letter address)
* message dropped

#### has DLA
* message stored

Note: autocreated queues (by default) are purged if no consumer and 0 messages

```
         <address-setting match="deliveryExample-Dynamic.#">
                <!-- default is 1.0 -->
                <redelivery-delay-multiplier>1.5</redelivery-delay-multiplier>
                <!-- default is 0 (no delay) -->
                <redelivery-delay>2000</redelivery-delay>
                <!-- default is 0.0); to randomize the redelivery -->
                <redelivery-collision-avoidance-factor>0.15</redelivery-collision-avoidance-factor>
                <!-- default is redelivery-delay * 10 -->
                <max-redelivery-delay>10000</max-redelivery-delay>

                <!-- default is 10 -->
                <max-delivery-attempts>3</max-delivery-attempts>

                <dead-letter-address>DeadLetterAddr</dead-letter-address>
                <auto-create-dead-letter-resources>true</auto-create-dead-letter-resources>
                <dead-letter-queue-prefix></dead-letter-queue-prefix>
                <dead-letter-queue-suffix>.DLQ</dead-letter-queue-suffix>

                <!--                <dead-letter-address>DLQ</dead-letter-address>-->
                <!--                <expiry-address>ExpiryQueueTest</expiry-address>-->
                <!-- in milliseconds -->
                <!-- with -1 only the global-max-size is in use for limiting -->
                <max-size-bytes>-1</max-size-bytes>
                <message-counter-history-day-limit>10</message-counter-history-day-limit>
                <address-full-policy>PAGE</address-full-policy>
                <auto-create-queues>true</auto-create-queues>
                <auto-create-addresses>true</auto-create-addresses>
                <auto-create-jms-queues>true</auto-create-jms-queues>
                <auto-create-jms-topics>true</auto-create-jms-topics>
            </address-setting>

```