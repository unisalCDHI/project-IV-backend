package com.cdhi.projectivbackend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDTO implements Serializable {

    private Integer id;

    @Length(min = 1, max = 100, message = "Nome deve ter entre 1 e 100 caracteres")
    @NotNull(message = "'Nome' não pode ser nulo")
    @NotEmpty(message = "'Nome' é obrigatório")
    private String name;

    @Length(min = 1, max = 100, message = "Email deve ter entre 1 e 100 caracteres")
    @NotNull(message = "'Email' não pode ser nulo")
    @NotEmpty(message = "'Email' é obrigatório")
    @Email(message = "Email inválido <email@exemplo.com>")
    private String email;

    @Length(min = 1, max = 200, message = "Senha deve ter entre 1 e 200 caracteres")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "'Senha' não pode ser nulo")
    @NotEmpty(message = "'Senha' é obrigatório")
    private String password;
}
