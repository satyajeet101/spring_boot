# Content
[Event Publisher](#Event-Publisher) | [Event Subscriber](#Event-Subscriber)

## Event-Publisher
- Step 1: Create a Custom Event Class
```java
package com.example.event.event;

import org.springframework.context.ApplicationEvent;

public class CustomEvent extends ApplicationEvent {
    private String message;

    public CustomEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
```
- Step 2: Create an Event Publisher
```java
package com.example.event.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {
    @Autowired
    private ApplicationEventPublisher publisher;

    public void publishEvent(String message) {
        CustomEvent event = new CustomEvent(this, message);
        publisher.publishEvent(event);
    }
}
```
- Step 3: Publish the Event
```java
package com.example.event;

import com.example.event.event.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Service
public class Service {
    @Autowired
    private EventPublisher eventPublisher;
    public void createOrder() {
        System.out.println("Order created");
        eventPublisher.publishEvent("Order created successfully!");
    }
}

```
- Step 4: Create an Event Listener (Subscriber)
```java
package com.example.event.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EmailListener {
    @EventListener
    public void handleCustomEvent(CustomEvent event) {
        System.out.println("EmailListener received event with message: " + event.getMessage());
        System.out.println("Initiating email notification...");
        sendEmail(event.getMessage());
    }
    private void sendEmail(String message) {
        // Simulate sending an email
        System.out.println("Sending email with message: " + message);
    }
}
```