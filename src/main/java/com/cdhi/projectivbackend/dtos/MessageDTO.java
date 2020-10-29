package com.cdhi.projectivbackend.dtos;

import com.cdhi.projectivbackend.domain.Message;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {

    private String body;

    private String image;

    private LocalDateTime createdDate;

    private UserDTO sender;
    private UserDTO recipient;

    public MessageDTO(Message msg) {
        this.body = msg.getBody();
        this.image = msg.getImage();
        this.createdDate = msg.getCreatedDate();
        this.sender = new UserDTO(msg.getSender());
        this.recipient = new UserDTO(msg.getRecipient());
    }
}
