package com.cdhi.projectivbackend.dtos;

import com.cdhi.projectivbackend.domain.User;
import com.cdhi.projectivbackend.domain.enums.Avatar;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class UserDTO implements Serializable {

    private Integer id;

    @NotNull(message = "'Nome' não pode ser nulo")
    @NotEmpty(message = "'Nome' é obrigatório")
    private String name;

    @NotNull(message = "'Email' não pode ser nulo")
    @NotEmpty(message = "'Email' é obrigatório")
    @Email(message = "Email inválido <email@exemplo.com>")
    private String email;

    private Avatar avatar;

    public UserDTO() {
    }

  public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.avatar = user.getAvatar();
  }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }
}
