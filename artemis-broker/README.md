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
