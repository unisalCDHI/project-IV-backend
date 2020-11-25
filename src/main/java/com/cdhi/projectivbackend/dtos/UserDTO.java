package com.cdhi.projectivbackend.dtos;

import com.cdhi.projectivbackend.domain.User;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    private boolean followed;

    private boolean follower;

    private LocalDateTime createdDate;

    private List<UserDTO> followers = new ArrayList<>();

    private List<UserDTO> followings = new ArrayList<>();

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.avatar = user.getAvatar();
        this.createdDate = user.getCreatedDate();
    }

    public UserDTO(User user, boolean followedByYou, boolean followingYou) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.name = user.getName();
        this.avatar = user.getAvatar();
        this.createdDate = user.getCreatedDate();
        this.followed = followedByYou;
        this.follower = followingYou;
        this.followers = user.getFollowers().stream().map(UserDTO::new).collect(Collectors.toList());
        this.followings = user.getFollowingUsers().stream().map(UserDTO::new).collect(Collectors.toList());
    }
}
