package com.cdhi.projectivbackend.services;

import com.cdhi.projectivbackend.domain.Message;
import com.cdhi.projectivbackend.dtos.MessageDTO;
import com.cdhi.projectivbackend.dtos.NewMessageDTO;

import java.util.List;

/**
 * CRUD service that operates Messages feature and save messages in database
 *
 * @author Davi MA
 */
public interface MessageService {

    Message send(NewMessageDTO newMessageDTO);

    List<MessageDTO> getConversationWith(Integer userId);
}
