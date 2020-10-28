package com.cdhi.projectivbackend.dtos;

import com.cdhi.projectivbackend.domain.Team;
import com.cdhi.projectivbackend.domain.User;
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

    public TeamDTO(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.owner = new UserDTO(team.getOwner());
        this.members = team.getMembers().stream().map(UserDTO::new).collect(Collectors.toList());
    }

}
