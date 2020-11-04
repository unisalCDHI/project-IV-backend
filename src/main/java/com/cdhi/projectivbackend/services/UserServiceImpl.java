package com.cdhi.projectivbackend.services;

import com.cdhi.projectivbackend.domain.Post;
import com.cdhi.projectivbackend.domain.User;
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
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository repo;

    @Autowired
    BCryptPasswordEncoder CRYPTER;

    @Autowired
    private EmailService emailService;

    private static UserSS authenticated() {
        try {
            return (UserSS) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public User getWebRequestUser() {
        UserSS user = UserServiceImpl.authenticated();
        if (user == null) {
            throw new AuthorizationException("Erro, usuário nulo no cabeçalho da requisição");
        }
        return repo.findById(user.getId()).orElseThrow(() -> new ObjectNotFoundException("Não foi encontrado um usuário com o id: " + user.getId()));
    }

    @Override
    @Transactional
    public User findOne(Integer id) {
        UserSS user = UserServiceImpl.authenticated();
        if (user == null || !user.hasRole(Profile.ADMIN) && !id.equals(user.getId())) {
            throw new AuthorizationException("Você precisa estar logado no usuário que deseja recuperar as informações ou em uma conta ADMIN");
        }
        return repo.findById(id).orElseThrow(() -> new ObjectNotFoundException("Não foi encontrado um usuário com o id: " + id));
    }

    @Override
    @Transactional
    public List<UserDTO> findAll(String name) {
        return repo.findDistinctByNameContainingIgnoreCase(name).stream().filter(user -> user.getEnabled() && !user.getId().equals(getWebRequestUser().getId())).map(UserDTO::new).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public User create(NewUserDTO newUserDTO) {
        newUserDTO.setId(null);
        if (repo.findByEmail(newUserDTO.getEmail()) != null)
            throw new ObjectAlreadyExistsException("Este email já está sendo usado");
        else if (repo.findByUsername(newUserDTO.getUsername()) != null)
            throw new ObjectAlreadyExistsException("Este nome de usuário já está sendo usado");
        else {
            newUserDTO.setPassword(CRYPTER.encode(newUserDTO.getPassword()));
            User userToCreate = toObject(newUserDTO);
            userToCreate.setUsername(newUserDTO.getUsername());

            User user = repo.save(userToCreate);
            emailService.sendUserConfirmationHtmlEmail(user);
            return user;
        }
    }

    private User toObject(NewUserDTO newUserDTO) {
        return new User(newUserDTO.getName(), newUserDTO.getEmail(), newUserDTO.getPassword());
    }

    private UserDTO toDTO(User user) {
        return new UserDTO(user);
    }

    @Override
    @Transactional
    public User save(UserDTO userDTO, Integer userId) {
        User userToUpdate = findOne(userId);
        userToUpdate
                .setName(userDTO.getName());
//        userToUpdate.setUsername(userDTO.getUsername());
        userToUpdate.setAvatar(userDTO.getAvatar());
        repo.save(userToUpdate);
        return findOne(userId);
    }

    @Override
    @Transactional
    public User save(String newPassword, Integer userId) {
        User userToUpdate = findOne(userId);
        userToUpdate.setPassword(CRYPTER.encode(newPassword));
        repo.save(userToUpdate);
        return findOne(userId);
    }

    @Override
    @Transactional
    public void delete(Integer userId) {
        repo.deleteById(userId);
    }

    @Override
    @Transactional
    public Page<UserDTO> findAllByPage(String name, Integer page, Integer size, String orderBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.valueOf(direction), orderBy);
        // TODO
        return repo.findAllPageable(name.toLowerCase(), pageRequest).map(UserDTO::new);
    }

    @Override
    @Transactional
    public User enable(Integer id, String _key) {
        User user = repo.findById(id).orElseThrow(() -> new ObjectNotFoundException("Não foi encontrado um usuário com o id: " + id));
        String userKey = "DO_NOT_FOUND_ANY_KEY";

        for (String key : user.get_key()) {
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

    @Override
    @Transactional
    public User addPost(Post post, User user) {
        user.getPosts().add(post);
        return repo.save(user);
    }

    @Override
    @Transactional
    public User addPost(Post post, Integer userId) {
        User user = findOne(userId);
        user.getPosts().add(post);
        return repo.save(user);
    }

    @Override
    @Transactional
    public User followUser(Integer userId) {
        User user = getWebRequestUser();
        User followingUser = repo.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Não foi encontrado um usuário com o id: " + userId));;

        if(!followingUser.getEnabled())
            throw new ObjectNotFoundException("Não foi encontrado um usuário com o id: " + userId);

        if (user.getFollowingUsers().stream().anyMatch(f -> f.getId().equals(followingUser.getId())) &&
                followingUser.getFollowers().stream().anyMatch(f -> f.getId().equals(user.getId()))) {
            user.getFollowingUsers().removeIf(f -> f.getId().equals(followingUser.getId()));
            followingUser.getFollowers().removeIf(p -> p.getId().equals(user.getId()));
        } else {
            user.getFollowingUsers().add(followingUser);
            followingUser.getFollowers().add(user);
        }

        repo.save(followingUser);
        return repo.save(user);
    }

    @Override
    public List<UserDTO> findAllFollowingUsers() {
        return getWebRequestUser().getFollowingUsers().stream().map(UserDTO::new).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> findAllFollowers() {
        return getWebRequestUser().getFollowers().stream().map(UserDTO::new).collect(Collectors.toList());
    }
}
