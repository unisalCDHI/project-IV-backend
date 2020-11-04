package com.cdhi.projectivbackend.services;

import com.cdhi.projectivbackend.domain.Team;
import com.cdhi.projectivbackend.dtos.NewTeamDTO;
import com.cdhi.projectivbackend.dtos.TeamDTO;

import java.util.List;

/**
 * CRUD service that operates Teams feature and save teams in database
 *
 * @author Davi MA
 */
public interface TeamService {

    List<TeamDTO> findAll();

    TeamDTO findOne(Integer id);

    TeamDTO edit(TeamDTO teamDTO, Integer id);

    TeamDTO create(NewTeamDTO newTeamDTO);

    void delete(Integer id);

    TeamDTO addUserToTeam(Integer userId, Integer teamId);

    TeamDTO removeUserFromTeam(Integer userId, Integer teamId);

    void quitTeam(Integer id);

}
