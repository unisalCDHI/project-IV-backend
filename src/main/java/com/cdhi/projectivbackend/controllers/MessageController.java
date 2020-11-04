package com.cdhi.projectivbackend.controllers;

import com.cdhi.projectivbackend.domain.Message;
import com.cdhi.projectivbackend.dtos.MessageDTO;
import com.cdhi.projectivbackend.dtos.NewMessageDTO;
import com.cdhi.projectivbackend.dtos.TeamDTO;
import com.cdhi.projectivbackend.services.MessageService;
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

@Api(value = "Message Controller")
@RestController
@RequestMapping(value = "messages")
public class MessageController {
    @Autowired
    private MessageService service;

    @ApiOperation(value = "Send a message")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid NewMessageDTO newMessageDTO) {
        MessageDTO p = service.send(newMessageDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(p.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @ApiOperation(value = "Get conversation between you and user")
    @GetMapping("{userId}")
    public ResponseEntity<List<MessageDTO>> findAll(@PathVariable("userId") Integer userId) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getConversationWith(userId));
    }

    @ApiOperation(value = "Delete message between you and user")
    @DeleteMapping("{userId}/{messageId}")
    public ResponseEntity<?> delete(@PathVariable("userId") Integer userId, @PathVariable("messageId") Integer messageId) {
        service.deleteMessage(userId, messageId);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @ApiOperation(value = "Update message between you and user")
    @PutMapping("{userId}")
    @Deprecated
    public ResponseEntity<MessageDTO> update(@PathVariable("userId") Integer userId, @RequestBody @Valid MessageDTO messageDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(null);
//        return ResponseEntity.status(HttpStatus.OK).body(service.update(userId, messageDTO));
    }
}
