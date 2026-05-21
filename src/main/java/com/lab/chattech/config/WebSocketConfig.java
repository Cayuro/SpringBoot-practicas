package com.lab.chattech.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/*
    This class configures WebSockets for the entire application.

    WHAT IS WEBSOCKET?
    WebSocket is a communication protocol that allows the server and the browser
    to send messages to each other at any time, without the browser having to
    make a new HTTP request. This is very useful for real-time features like chat.

    In normal HTTP: Browser asks --> Server responds --> connection closes.
    With WebSocket: Browser connects --> Both sides can send messages anytime.

    WHAT IS STOMP?
    STOMP stands for Simple Text Oriented Messaging Protocol.
    WebSocket is just a pipe between the browser and the server.
    It doesn't tell us HOW to structure our messages or WHERE to send them.
    STOMP adds a messaging format on top of WebSocket, similar to how HTTP
    adds structure (headers, methods, paths) on top of raw TCP connections.

    With STOMP, we can:
    - Subscribe to channels (like joining a chat room)
    - Send messages to specific destinations
    - Use message headers for routing information

    WHAT IS SOCKJS?
    SockJS is a JavaScript library that acts as a backup plan.
    Not all browsers or networks support WebSocket.
    SockJS tries WebSocket first, and if it doesn't work, it falls back to
    other techniques like long-polling so the app still works.
    Think of it as: "try WebSocket, but if that fails, use something else".

    The @Configuration annotation tells Spring that this class contains
    configuration settings that should be applied to the application.

    The @EnableWebSocketMessageBroker annotation activates the WebSocket support
    with a message broker. The "message broker" is the component that routes
    messages from senders to the correct subscribers (like a post office).
*/
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /*
        This method configures the MESSAGE BROKER.

        The message broker is the routing system that decides where messages go.
        When someone sends a message, the broker figures out who should receive it.

        We configure two things here:
        1. Broker destinations: channels that clients can SUBSCRIBE to
        2. Application destinations: paths that go to our @MessageMapping methods

        UNDERSTANDING DESTINATIONS:
        Think of destinations like radio channels.
        - Some channels are managed by the broker (subscribers listen here)
        - Some channels go to our application code (controllers handle these)
    */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        /*
            enableSimpleBroker("/tema") activates an in-memory message broker
            for destinations that start with "/tema".

            "Simple" means it runs inside our app (no external software needed).
            Real production apps sometimes use RabbitMQ or ActiveMQ instead,
            but for learning purposes the simple broker is perfect.

            When a message is sent to any destination starting with "/tema",
            the broker broadcasts it to all clients subscribed to that destination.

            Example: if a message goes to "/tema/mensajes",
            all clients who subscribed to "/tema/mensajes" will receive it.
            This is how our chat broadcast works.
        */
        config.enableSimpleBroker("/tema");

        /*
            setApplicationDestinationPrefixes("/app") means:
            Any destination starting with "/app" should be routed to
            our Spring controller methods annotated with @MessageMapping.

            So when the browser sends a message to "/app/enviar",
            Spring looks for a @MessageMapping("/enviar") method and calls it.
            The "/app" prefix is stripped before matching with @MessageMapping.
        */
        config.setApplicationDestinationPrefixes("/app");
    }

    /*
        This method registers the WebSocket endpoint.

        An ENDPOINT is the URL where the browser first connects to establish
        the WebSocket connection. Think of it as the "front door" of the WebSocket.
        The browser connects here once, and after that, messages flow freely.

        FLOW:
        1. Browser opens connection to: ws://localhost:8080/chat-websocket
        2. Upgrade from HTTP to WebSocket protocol happens
        3. STOMP is then used on top for message routing
        4. Browser subscribes to channels and sends messages using STOMP
    */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        /*
            We add an endpoint at the path "/chat-websocket".
            This is where the browser connects to open the WebSocket.

            .withSockJS() adds SockJS support as a fallback.
            If the browser doesn't support WebSocket, SockJS will automatically
            use a compatible transport method (like long-polling) instead.
            This makes our app work in more environments and older browsers.
        */
        registry.addEndpoint("/chat-websocket").withSockJS();
    }

}