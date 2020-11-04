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

    MessageDTO send(NewMessageDTO newMessageDTO);

    List<MessageDTO> getConversationWith(Integer userId);

    void deleteMessage(Integer userId, Integer messageId);

    MessageDTO update(Integer messageId, MessageDTO messageDTO);
}
