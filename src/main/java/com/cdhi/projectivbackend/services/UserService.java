package com.cdhi.projectivbackend.services;

import com.cdhi.projectivbackend.domain.Post;
import com.cdhi.projectivbackend.domain.User;
import com.cdhi.projectivbackend.dtos.NewUserDTO;
import com.cdhi.projectivbackend.dtos.PostDTO;
import com.cdhi.projectivbackend.dtos.UserDTO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * CRUD service that operates User feature and save users in database
 *
 * @author Davi MA
 */
public interface UserService {

    User getWebRequestUser();

    User findOne(Integer id);

    List<UserDTO> findAll(String name);

    List<UserDTO> findAllFollowingUsers();

    List<UserDTO> findAllFollowers();

    void delete(Integer userId);

    User create(NewUserDTO newUserDTO);

    User save(UserDTO userDTO, Integer userId);

    User save(String newPassword, Integer userId);

    User followUser(Integer userId);

    Page<UserDTO> findAllByPage(String name, Integer page, Integer size, String orderBy, String direction);

    User enable(Integer id, String _key);

    User addPost(Post post, User user);

    User addPost(Post post, Integer userId);

    List<PostDTO> findPosts(Integer id);
}
