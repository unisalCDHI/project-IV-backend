package com.cdhi.projectivbackend.services;

import com.cdhi.projectivbackend.domain.Message;
import com.cdhi.projectivbackend.domain.User;
import com.cdhi.projectivbackend.dtos.MessageDTO;
import com.cdhi.projectivbackend.dtos.NewMessageDTO;
import com.cdhi.projectivbackend.repositories.MessageRepository;
import com.cdhi.projectivbackend.repositories.UserRepository;
import com.cdhi.projectivbackend.services.exceptions.AuthorizationException;
import com.cdhi.projectivbackend.services.exceptions.ObjectNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository repo;

    @Override
    @Transactional
    public MessageDTO send(NewMessageDTO newMessageDTO) {
        User sender = userService.getWebRequestUser();
        User recipient = userRepository.findById(newMessageDTO.getRecipientId()).orElseThrow(() -> new ObjectNotFoundException("Não foi encontrado um usuário com o id: " + newMessageDTO.getRecipientId()));

        Message message = new Message();

        if((newMessageDTO.getImage() == null || newMessageDTO.getImage().equals("")) && (newMessageDTO.getBody() == null || newMessageDTO.getBody().equals("")))
            throw new AuthorizationException("Você deve preencher o campo 'body' ou o campo 'image'");

        message.setBody(newMessageDTO.getBody());
        message.setImage(newMessageDTO.getImage());
        message.setRecipient(recipient);
        message.setSender(sender);

        sender.getMessagesDelivered().add(message);
        recipient.getMessagesReceived().add(message);

        Message m = repo.saveAndFlush(message);
        userRepository.saveAndFlush(sender);
        userRepository.saveAndFlush(recipient);
        return new MessageDTO(m);
    }

    @Override
    @Transactional
    public List<MessageDTO> getConversationWith(Integer userId) {
        User sender = userService.getWebRequestUser();
        User recipient = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Não foi encontrado um usuário com o id: " + userId));

        List<MessageDTO> messageDTOList = repo.findConversationBetween(sender.getId(), recipient.getId()).stream().map(MessageDTO::new).collect(Collectors.toList());

        return messageDTOList;
    }

    @Override
    @Transactional
    public void deleteMessage(Integer userId, Integer messageId) {
        User sender = userService.getWebRequestUser();
        User recipient = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Não foi encontrado um usuário com o id: " + userId));

        Message msg = repo.findById(messageId).orElseThrow(() -> new ObjectNotFoundException("Mensagem não encontrada"));

        msg.setBody(null);
        msg.setImage("");
        msg.setDeleted(true);

        if (msg.getSender().getId().equals(sender.getId()) && msg.getRecipient().getId().equals(recipient.getId()))
            repo.saveAndFlush(msg);
        else
            throw new ObjectNotFoundException("Mensagem não encontrada");
    }

    @Override
    @Transactional
    public MessageDTO update(Integer messageId, MessageDTO messageDTO) {
        User sender = userService.getWebRequestUser();
        User recipient = userRepository.findById(messageDTO.getRecipient().getId()).orElseThrow(() -> new ObjectNotFoundException("Não foi encontrado um usuário com o id: " + messageDTO.getRecipient().getId()));

        Message message = repo.findById(messageId).orElseThrow(() -> new ObjectNotFoundException("Mensagem não encontrada"));

        if(message.getSender().getId().equals(sender.getId()) && message.getRecipient().getId().equals(recipient.getId())) {
            message.setBody(messageDTO.getBody());
            message.setImage(messageDTO.getImage());
            message.setUpdated(true);
        } else
            throw new ObjectNotFoundException("Mensagem não encontrada");

        return new MessageDTO(repo.saveAndFlush(message));
    }


}
