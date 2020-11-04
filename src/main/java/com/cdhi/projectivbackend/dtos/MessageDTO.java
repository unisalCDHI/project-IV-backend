package com.cdhi.projectivbackend.dtos;

import com.cdhi.projectivbackend.domain.Message;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class MessageDTO {

    private Integer id;

    @Length(min = 0, max = 255, message = "A mensagem de ter no máximo 255 caracteres")
    @NotNull(message = "'body' não pode ser nulo")
    private String body;

    private String image;

    private LocalDateTime createdDate;

    private UserDTO sender;
    private UserDTO recipient;

    private boolean deleted;
    private boolean updated;

    public MessageDTO(Message msg) {
        this.id = msg.getId();
        this.body = msg.getBody();
        this.image = msg.getImage();
        this.updated = msg.getUpdated();
        this.deleted = msg.getDeleted();
        this.createdDate = msg.getCreatedDate();
        this.sender = new UserDTO(msg.getSender());
        this.recipient = new UserDTO(msg.getRecipient());
    }
}
