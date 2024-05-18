package com.googol.googolfe.web.websockets;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

/**
 * The AdminWebSocketController class handles WebSocket communication for sending updates
 * related to active barrels and top 10 searches to the client.
 */
@RestController
public class AdminWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Constructor for AdminWebSocketController class.
     * @param messagingTemplate the messaging template for sending WebSocket messages
     */
    public AdminWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Sends updated active barrels data to the client.
     * @param data the updated data to send
     */
    public void sendBarrelUpdates(Object data) {
        messagingTemplate.convertAndSend("/topic/barrelUpdates", data);
    }

    /**
     * Sends updated top 10 searches data to the client.
     * @param data the updated data to send
     */
    public void sendSearchUpdates(Object data) {
        messagingTemplate.convertAndSend("/topic/searchUpdates", data);
    }
}
