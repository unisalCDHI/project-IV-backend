package com.cdhi.projectivbackend.services;

import com.cdhi.projectivbackend.domain.Team;
import com.cdhi.projectivbackend.domain.User;
import com.cdhi.projectivbackend.dtos.NewTeamDTO;
import com.cdhi.projectivbackend.dtos.TeamDTO;
import com.cdhi.projectivbackend.dtos.UserDTO;
import com.cdhi.projectivbackend.repositories.TeamRepository;
import com.cdhi.projectivbackend.repositories.UserRepository;
import com.cdhi.projectivbackend.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {

    @Autowired
    TeamRepository repo;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Transactional
    public List<TeamDTO> findAll() {
        User user = userService.getWebRequestUser();
        return user.getTeams().stream().map(TeamDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public Team create(NewTeamDTO newTeamDTO) {
        User requestUser = userService.getWebRequestUser();
        Team teamToCreate = new Team();

        teamToCreate.setOwner(requestUser);
        teamToCreate.setName(newTeamDTO.getName());
        teamToCreate.setMembers(new HashSet<>(userRepository.findByIdIn(newTeamDTO.getMembers().stream().map(UserDTO::getId).collect(Collectors.toList()))));
        if (teamToCreate.getMembers().stream().noneMatch(user -> user.getId().equals(requestUser.getId())))
            throw new ObjectNotFoundException("Não foi encontrado o seu usuário na lista de membros do time");
        Team team = repo.save(teamToCreate);
        for (User u : teamToCreate.getMembers()) {
            u.getTeams().add(teamToCreate);
            userRepository.save(u);
        }
        return team;
    }

    @Transactional
    @Deprecated
    public Team edit(TeamDTO teamDTO, Integer id) {

        User requestUser = userService.getWebRequestUser();

        if (teamDTO.getMembers().stream().noneMatch(userDTO -> userDTO.getId().equals(requestUser.getId())))
            throw new ObjectNotFoundException("Não foi encontrado o seu usuário com id: " + id);

        Team team = repo.findById(id).orElseThrow(() -> new ObjectNotFoundException("Não foi encontrado um time com o id: " + id));
        List<User> usersBefore = userRepository.findByIdIn(team.getMembers().stream().map(User::getId).collect(Collectors.toList()));

        // remove users
        for (User u : usersBefore) {
            Team finalTeam = team;
            u.getTeams().removeIf(t -> t.getId().equals(finalTeam.getId()));
            team.getMembers().removeIf(user -> user.getId().equals(u.getId()));
            repo.save(team);
            userRepository.save(u);
        }

        // add new users
        team.setName(teamDTO.getName());
        team.setMembers(new HashSet<>(userRepository.findByIdIn(teamDTO.getMembers().stream().map(UserDTO::getId).collect(Collectors.toList()))));
        team = repo.save(team);

        for (User u : team.getMembers()) {
            u.getTeams().add(team);
            userRepository.save(u);
        }
        return team;
    }



}
