package com.cdhi.projectivbackend.dtos;

import com.cdhi.projectivbackend.domain.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamDTO {

    private Integer id;

    @NotNull(message = "'Nome' não pode ser nulo")
    @NotEmpty(message = "'Nome' é obrigatório")
    private String name;

    private UserDTO owner;

    private List<UserDTO> members;

    private String about;

    private List<PostDTO> posts;

    public TeamDTO(Team team, Integer currentUserId) {
        this.id = team.getId();
        this.name = team.getName();
        this.owner = new UserDTO(team.getOwner());
        this.members = team.getMembers().stream().map(UserDTO::new).collect(Collectors.toList());
        this.about = team.getAbout();
        this.posts = team.getPosts().stream().map(post -> new PostDTO(post, post.getUsersLikes().stream().anyMatch(u -> u.getId().equals(currentUserId)),
                post.getUsersReposts().stream().anyMatch(u -> u.getId().equals(currentUserId)))).collect(Collectors.toList());
    }

}
