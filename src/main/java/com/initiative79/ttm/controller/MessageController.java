package com.initiative79.ttm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.BSONObject;
import org.bson.Document;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.initiative79.models.Message;
import com.initiative79.ttm.services.MongoService;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;

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
        
        mongoService.listenForNewMessages("1", "2")
            .forEach(doc -> {
                switch (doc.getOperationType()) {
                    case INSERT:
                        template.convertAndSend("/newMessage", doc.getFullDocument().toJson());
                        break;
                    case DELETE:
                        template.convertAndSend("/deleteMessage", doc.getDocumentKey().get("_id").asObjectId().getValue());
                        break;
                    case UPDATE:
                        template.convertAndSend("/updateMessage", doc.getUpdateDescription().getUpdatedFields().toJson());
                        break;
                    case REPLACE:
                        template.convertAndSend("/updateMessage", doc.getFullDocument().toJson());
                        break;
                    default:
                        log.warn("Not yet implemented OperationType: {}", doc.getOperationType());
                }
            });
    }

    @MessageMapping("/send")
    public void sendMessage(Message message) throws Exception {
        mongoService.insert(message.getUser1(), message.getUser2(), message.getContent());
    }
}
