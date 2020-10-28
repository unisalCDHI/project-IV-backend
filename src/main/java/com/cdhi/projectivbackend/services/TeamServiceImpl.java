package com.cdhi.projectivbackend.services;

import com.cdhi.projectivbackend.domain.Team;
import com.cdhi.projectivbackend.domain.User;
import com.cdhi.projectivbackend.dtos.NewTeamDTO;
import com.cdhi.projectivbackend.dtos.TeamDTO;
import com.cdhi.projectivbackend.dtos.UserDTO;
import com.cdhi.projectivbackend.repositories.TeamRepository;
import com.cdhi.projectivbackend.repositories.UserRepository;
import com.cdhi.projectivbackend.services.exceptions.AuthorizationException;
import com.cdhi.projectivbackend.services.exceptions.ObjectAlreadyExistsException;
import com.cdhi.projectivbackend.services.exceptions.ObjectNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TeamServiceImpl implements TeamService {

    @Autowired
    TeamRepository repo;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public List<TeamDTO> findAll() {
        User user = userService.getWebRequestUser();
        return user.getTeams().stream().map(TeamDTO::new).collect(Collectors.toList());
    }

    @Override
    public TeamDTO findOne(Integer id) {
        User user = userService.getWebRequestUser();
        Team team = repo.findById(id).orElseThrow(() -> new ObjectNotFoundException("Não foi encontrado um time com o id: " + id));
        if(team.getMembers().stream().noneMatch(u -> u.getId().equals(user.getId())))
            throw new ObjectNotFoundException("Não foi encontrado o seu usuário na lista de membros do time");
        return new TeamDTO();
    }

    @Override
    @Transactional
    public TeamDTO create(NewTeamDTO newTeamDTO) {
        User requestUser = userService.getWebRequestUser();
        Team teamToCreate = new Team();

        teamToCreate.setOwner(requestUser);
        teamToCreate.setName(newTeamDTO.getName());
        teamToCreate.setMembers(new HashSet<>(userRepository.findByIdIn(newTeamDTO.getMembers().stream().map(UserDTO::getId).collect(Collectors.toList()))));
        teamToCreate.getMembers().add(requestUser);

        if (teamToCreate.getMembers().stream().noneMatch(user -> user.getId().equals(requestUser.getId())))
            throw new ObjectNotFoundException("Não foi encontrado o seu usuário na lista de membros do time");
        Team team = repo.save(teamToCreate);
        for (User u : teamToCreate.getMembers()) {
            u.getTeams().add(teamToCreate);
            userRepository.save(u);
        }
        return new TeamDTO(team);
    }

    @Override
    @Transactional
    public TeamDTO edit(TeamDTO teamDTO, Integer id) {

        User requestUser = userService.getWebRequestUser();
        Team team = repo.findById(id).orElseThrow(() -> new ObjectNotFoundException("Não foi encontrado um time com o id: " + id));

        if (!team.getOwner().getId().equals(requestUser.getId())) {
            throw new ObjectNotFoundException("Não foi possível editar as informações do time (você não é o líder do time");
        }
        team.setName(teamDTO.getName());
        return new TeamDTO(repo.save(team));
    }

    @Override
    public void delete(Integer id) {
        User requestUser = userService.getWebRequestUser();
        Team team = repo.findById(id).orElseThrow(() -> new ObjectNotFoundException("Não foi encontrado um time com o id: " + id));
        List<User> usersInTeam = userRepository.findByIdIn(team.getMembers().stream().map(User::getId).collect(Collectors.toList()));

        if (team.getOwner().getId().equals(requestUser.getId())) {
            usersInTeam.forEach(user -> {
                user.getTeams().removeIf(t -> t.getId().equals(team.getId()));
            });
            team.setMembers(Collections.emptySet());
            userRepository.saveAll(usersInTeam);
            repo.delete(team);
            log.info("Team {} deleted", team.getId().toString());
        } else
            throw new AuthorizationException("Você deve ser o líder do time para executar essa operação");
    }

    @Override
    public TeamDTO addUserToTeam(Integer userId, Integer teamId) {
        User requestUser = userService.getWebRequestUser();

        if(userId.equals(requestUser.getId()))
            throw new AuthorizationException("Você não pode realizar essa operação com o usuário logado");

        Team team = repo.findById(teamId).orElseThrow(() -> new ObjectNotFoundException("Não foi encontrado um time com o id: " + teamId));
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Não foi encontrado um usuário com o id: " + userId));

        if (team.getOwner().getId().equals(requestUser.getId())) {
            if (team.getMembers().stream().anyMatch(u -> u.getId().equals(user.getId())))
                throw new ObjectAlreadyExistsException("O usuário já está no time.");
            else {
                team.getMembers().add(user);
                user.getTeams().add(team);
                TeamDTO teamDTO = new TeamDTO(repo.save(team));
                userRepository.save(user);
                log.info("User {} added in team {}", user.getId().toString(), team.getId().toString());
                return teamDTO;
            }
        } else
            throw new AuthorizationException("Você deve ser o líder do time para executar essa operação");
    }

    @Override
    public TeamDTO removeUserFromTeam(Integer userId, Integer teamId) {
        User requestUser = userService.getWebRequestUser();

        if(userId.equals(requestUser.getId()))
            throw new AuthorizationException("Você não pode realizar essa operação com o usuário logado");

        Team team = repo.findById(teamId).orElseThrow(() -> new ObjectNotFoundException("Não foi encontrado um time com o id: " + teamId));
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Não foi encontrado um usuário com o id: " + userId));

        if (team.getOwner().getId().equals(requestUser.getId())) {
            if (team.getMembers().stream().noneMatch(u -> u.getId().equals(user.getId())))
                throw new ObjectAlreadyExistsException("O usuário não está no time.");
            else {
                team.getMembers().removeIf(u -> u.getId().equals(user.getId()));
                user.getTeams().removeIf(t -> t.getId().equals(team.getId()));
                TeamDTO teamDTO = new TeamDTO(repo.save(team));
                userRepository.save(user);
                log.info("User {} removed from team {}", user.getId().toString(), team.getId().toString());
                return teamDTO;
            }
        } else
            throw new AuthorizationException("Você deve ser o líder do time para executar essa operação");
    }

    @Override
    public void quitTeam(Integer id) {
        User requestUser = userService.getWebRequestUser();
        Team team = repo.findById(id).orElseThrow(() -> new ObjectNotFoundException("Não foi encontrado um time com o id: " + id));

        if (!team.getOwner().getId().equals(requestUser.getId())) {
            if (team.getMembers().stream().anyMatch(u -> u.getId().equals(requestUser.getId()))) {
                team.getMembers().removeIf(u -> u.getId().equals(requestUser.getId()));
                requestUser.getTeams().removeIf(t -> t.getId().equals(team.getId()));
                repo.save(team);
                userRepository.save(requestUser);
            } else
                throw new AuthorizationException("Você não pode sair de um time que não pertence");
        } else
            throw new AuthorizationException("Você não pode sair de um time que é líder");

    }
}
