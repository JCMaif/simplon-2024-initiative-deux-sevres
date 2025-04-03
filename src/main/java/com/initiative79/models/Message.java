package com.initiative79.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class Message {
    private String id;
    private User user1;
    private User user2;
    private String content;
}
