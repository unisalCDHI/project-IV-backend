package com.cdhi.projectivbackend.dtos;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class NewPostDTO {

    @NotNull(message = "'body' não pode ser nulo")
    private String body;

//    @NotNull(message = "'userId' não pode ser nulo")
//    private Integer userId;

    private String image;
}
