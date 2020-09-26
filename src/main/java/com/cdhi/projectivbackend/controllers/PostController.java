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
import java.util.List;

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

    @ApiOperation(value = "Get Commentaries by Post id")
    @GetMapping(value = "/{id}/commentaries")
    public ResponseEntity<List<PostDTO>> getCommentariesByPostId(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.findCommentariesFrom(id));
    }

    @ApiOperation(value = "Create Post")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid NewPostDTO newPostDTO) {
        Post p = service.createPost(newPostDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(p.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @ApiOperation(value = "Create Commentary")
    @PostMapping(value = "{id}")
    public ResponseEntity<?> create(@RequestBody @Valid NewPostDTO newCommentaryDTO, @PathVariable Integer id) {
        Post p = service.createCommentary(newCommentaryDTO, id);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(p.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @ApiOperation(value = "Like Post by id")
    @PutMapping(value = "/{id}")
    public ResponseEntity<PostDTO> likePost(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.likePost(id));
    }

    @ApiOperation(value = "Get Posts")
    @GetMapping
    public ResponseEntity<List<PostDTO>> getPosts() {
        return ResponseEntity.status(HttpStatus.OK).body(service.findAll());
    }

    @ApiOperation(value = "Repost")
    @PutMapping(value = "repost/{id}")
    public ResponseEntity<?> repost(@PathVariable Integer id) {
        service.repost(id);
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    @ApiOperation(value = "Get reposts")
    @GetMapping(value = "repost")
    public ResponseEntity<List<?>> getReposts() {
        return ResponseEntity.status(HttpStatus.OK).body( service.getReposts());
    }
}
