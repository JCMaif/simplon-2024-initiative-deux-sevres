package com.initiative79.ttm.controller;

import org.bson.Document;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.initiative79.models.Message;
import com.initiative79.ttm.services.MongoService;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MessageController {

    private MongoService mongoService;
    private SimpMessagingTemplate template;

    public MessageController(MongoService mongoService, SimpMessagingTemplate template) {
        this.mongoService = mongoService;
        this.template = template;
    }

    @MessageMapping("/requestMessages")
    public void openMessagePage() {
        var messages = mongoService.getMessagesForConversation("1", "2");
        template.convertAndSend("/getMessages", messages);

        mongoService.listenForNewMessages("1", "2").forEach((event) -> {
            template.convertAndSend("/getMessages", event.getFullDocument().toJson());
        });
    }

    @MessageMapping("/send")
    public void sendMessage(Message message) throws Exception {
        mongoService.insert(message.getUser1(), message.getUser2(), message.getContent());
    }
}
