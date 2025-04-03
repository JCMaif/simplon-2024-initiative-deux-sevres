package com.initiative79.ttm.services;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bson.BsonValue;
import org.bson.Document;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.initiative79.models.Message;
import com.initiative79.models.User;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import com.mongodb.client.result.InsertOneResult;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MongoService {
    private MongoClient client;
    private MongoDatabase db;
    private MongoCollection<Document> messageCollection;

    @PostConstruct
    void init() throws IOException {
        log.info("start");
        client = MongoClients.create("mongodb://localhost:27017/?replicaSet=rs0");
        db = client.getDatabase("ttm");
        messageCollection = db.getCollection("messages");
    }

    public BsonValue insert(User sender, User dest, String content) {
        return messageCollection.insertOne(
                new Document("sender", sender.getId())
                        .append("dest", dest.getId())
                        .append("content", content))
                .getInsertedId();
    }

    public Iterable<Object> getMessagesForConversation(String user1, String user2) {
        return messageCollection.aggregate(Arrays.asList(
                new Document("$match",
                        new Document("sender", 
                            new Document("$in", Arrays.asList(user1, user2)))
                        .append("dest", 
                            new Document("$in", Arrays.asList(user1, user2)))
                ))).map(Document::toJson);
    }

    public ChangeStreamIterable<Document> listenForNewMessages(String user1, String user2) {
        return messageCollection.watch(Arrays.asList(
            new Document("$match",
                new Document("sender", 
                    new Document("$in", Arrays.asList(user1, user2)))
                .append("dest", 
                    new Document("$in", Arrays.asList(user1, user2)))
        )));
    }

}