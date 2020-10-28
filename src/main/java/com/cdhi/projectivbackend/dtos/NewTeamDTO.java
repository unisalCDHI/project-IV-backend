package com.cdhi.projectivbackend.dtos;


import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class NewTeamDTO {

    @NotNull(message = "'Nome' não pode ser nulo")
    @NotEmpty(message = "'Nome' é obrigatório")
    private String name;

    @NotEmpty(message = "O time deve conter pelo menos 1 membro (você)")
    private List<UserDTO> members;
}
