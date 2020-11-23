package com.cdhi.projectivbackend.dtos;

import com.cdhi.projectivbackend.domain.User;
import com.cdhi.projectivbackend.services.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class UserDTO implements Serializable {

    @Autowired
    private UserService userService;

    private Integer id;

    @NotNull(message = "'Nome de usuário' não pode ser nulo")
    @NotEmpty(message = "'Nome de usuário' é obrigatório")
    private String username;

    @NotNull(message = "'Nome' não pode ser nulo")
    @NotEmpty(message = "'Nome' é obrigatório")
    private String name;

    private String avatar;

    private boolean followed;

    private boolean follower;

    public UserDTO() {
    }

  public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.avatar = user.getAvatar();
  }

    public UserDTO(User user, boolean followedByYou, boolean followingYou) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.avatar = user.getAvatar();
        this.followed = followedByYou;
        this.follower = followingYou;
    }
}
