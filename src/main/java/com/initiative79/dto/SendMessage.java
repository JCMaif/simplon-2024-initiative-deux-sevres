package com.initiative79.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendMessage {
    private String dest;
    private String content;
}
