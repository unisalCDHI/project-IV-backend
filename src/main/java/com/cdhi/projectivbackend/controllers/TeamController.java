package com.cdhi.projectivbackend.controllers;

import com.cdhi.projectivbackend.domain.Team;
import com.cdhi.projectivbackend.dtos.NewTeamDTO;
import com.cdhi.projectivbackend.dtos.TeamDTO;
import com.cdhi.projectivbackend.services.TeamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Api(value = "Team Controller")
@RestController
@RequestMapping(value = "teams")
public class TeamController {

    @Autowired
    private TeamService service;

    @ApiOperation(value = "Get Teams")
    @GetMapping
    public ResponseEntity<List<TeamDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(service.findAll());
    }

    @ApiOperation(value = "Create Team")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid NewTeamDTO newTeamDTO) {
        Team u = service.create(newTeamDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(u.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }
}
