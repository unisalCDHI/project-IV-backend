package com.cdhi.projectivbackend.dtos;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class NewMessageDTO {

    @Length(min = 1, max = 255, message = "A mensagem de ter no máximo 255 caracteres")
    @NotNull(message = "'body' não pode ser nulo")
    @NotEmpty(message = "'body' é obrigatório")
    private String body;

    private String image;

    @NotNull(message = "'recipientId' não pode ser nulo")
    private Integer recipientId;
}
