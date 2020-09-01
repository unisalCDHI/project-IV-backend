package com.cdhi.projectivbackend.services;

import com.cdhi.projectivbackend.domain.User;
import com.cdhi.projectivbackend.domain.enums.Avatar;
import com.cdhi.projectivbackend.domain.enums.Profile;
import com.cdhi.projectivbackend.dtos.NewUserDTO;
import com.cdhi.projectivbackend.dtos.UserDTO;
import com.cdhi.projectivbackend.repositories.UserRepository;
import com.cdhi.projectivbackend.security.UserSS;
import com.cdhi.projectivbackend.services.exceptions.AuthorizationException;
import com.cdhi.projectivbackend.services.exceptions.ObjectAlreadyExistsException;
import com.cdhi.projectivbackend.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    UserRepository repo;

    @Autowired
    BCryptPasswordEncoder CRYPTER;

    @Autowired
    private EmailService emailService;

    public static UserSS authenticated() {
        try {
            return (UserSS) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public User findOne(Integer id) {
        UserSS user = UserService.authenticated();
        if (user == null || !user.hasRole(Profile.ADMIN) && !id.equals(user.getId())) {
            throw new AuthorizationException("Você precisa estar logado no usuário que deseja recuperar as informações ou em uma conta ADMIN");
        }
        return repo.findById(id).orElseThrow(() -> new ObjectNotFoundException("Não foi encontrado um usuário com o id: " + id));
    }

    @Transactional
    public List<UserDTO> findAll(String name) {
        return repo.findDistinctByNameContainingIgnoreCase(name).stream().filter(User::getEnabled).map(UserDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public User create(NewUserDTO newUserDTO) {
        newUserDTO.setId(null);
        if (repo.findByEmail(newUserDTO.getEmail()) != null)
            throw new ObjectAlreadyExistsException("Este email já está sendo usado");
        else {
            newUserDTO.setPassword(CRYPTER.encode(newUserDTO.getPassword()));
            User userToCreate = toObject(newUserDTO);
            userToCreate.setAvatar(Avatar.A0);
            User user = repo.save(userToCreate);
            emailService.sendUserConfirmationHtmlEmail(user);
            return user;
        }
    }

    public User toObject(NewUserDTO newUserDTO) {
        return new User(newUserDTO.getName(), newUserDTO.getEmail(), newUserDTO.getPassword());
    }

    public UserDTO toDTO(User user) {
        return new UserDTO(user);
    }

    @Transactional
    public User save(UserDTO userDTO, Integer userId) {
        User userToUpdate = findOne(userId);
        userToUpdate
                .setName(userDTO.getName());
        userToUpdate.setAvatar(userDTO.getAvatar());
        repo.save(userToUpdate);
        return findOne(userId);
    }

    @Transactional
    public User save(String newPassword, Integer userId) {
        User userToUpdate = findOne(userId);
        userToUpdate.setPassword(CRYPTER.encode(newPassword));
        repo.save(userToUpdate);
        return findOne(userId);
    }

    @Transactional
    public void delete(Integer userId) {
        repo.deleteById(userId);
    }

    @Transactional
    public Page<UserDTO> findAllByPage(String name, Integer page, Integer size, String orderBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);
        // TODO
        return repo.findAllPageable(name.toLowerCase(), pageRequest).map(UserDTO::new);
    }

    @Transactional
    public User enable(Integer id, String _key) {
        User user = repo.findById(id).orElseThrow(() -> new ObjectNotFoundException("Não foi encontrado um usuário com o id: " + id));
        String userKey = "DO_NOT_FOUND_ANY_KEY";

        for (String key : user.get_key()){
            userKey = key;
        }
        if (userKey.equals(_key)) {
            user.setEnabled(true);
            user.deleteKey();
            repo.save(user);
            return repo.findById(id).orElseThrow(() -> new ObjectNotFoundException("Não foi encontrado um usuário com o id: " + id));
        }
        throw new ObjectNotFoundException("Chave não encontrada, verifique seu email");
    }
}
