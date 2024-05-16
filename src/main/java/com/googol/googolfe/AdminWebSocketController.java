package com.googol.googolfe;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public AdminWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Method to send updated active barrels data to the client
    public void sendBarrelUpdates(Object data) {
        messagingTemplate.convertAndSend("/topic/barrelUpdates", data);
    }

    // Method to send updated top 10 searches data to the client
    public void sendSearchUpdates(Object data) {
        messagingTemplate.convertAndSend("/topic/searchUpdates", data);
    }
}
