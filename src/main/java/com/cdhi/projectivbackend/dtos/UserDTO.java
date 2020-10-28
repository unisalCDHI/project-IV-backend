package com.cdhi.projectivbackend.dtos;

import com.cdhi.projectivbackend.domain.User;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UserDTO implements Serializable {

    private Integer id;

    @NotNull(message = "'Nome de usuário' não pode ser nulo")
    @NotEmpty(message = "'Nome de usuário' é obrigatório")
    private String username;

    @NotNull(message = "'Nome' não pode ser nulo")
    @NotEmpty(message = "'Nome' é obrigatório")
    private String name;

    private String avatar;

    public UserDTO() {
    }

  public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.avatar = user.getAvatar();
  }
}
