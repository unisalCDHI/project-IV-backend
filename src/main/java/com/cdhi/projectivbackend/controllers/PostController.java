package com.cdhi.projectivbackend.controllers;

import com.cdhi.projectivbackend.domain.Post;
import com.cdhi.projectivbackend.dtos.NewPostDTO;
import com.cdhi.projectivbackend.dtos.PostDTO;
import com.cdhi.projectivbackend.services.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@Api(value = "Post Controller")
@RestController
@RequestMapping(value = "posts")
public class PostController {

    @Autowired
    private PostService service;

    @ApiOperation(value = "Get Post by id")
    @GetMapping(value = "/{id}")
    public ResponseEntity<PostDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.findOne(id));
    }

    @ApiOperation(value = "Create Post")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid NewPostDTO newPostDTO) {
        Post p = service.createPost(newPostDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(p.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }


}
