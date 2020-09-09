package com.cdhi.projectivbackend.services;

import com.cdhi.projectivbackend.domain.Post;
import com.cdhi.projectivbackend.domain.User;
import com.cdhi.projectivbackend.dtos.NewPostDTO;
import com.cdhi.projectivbackend.dtos.PostDTO;
import com.cdhi.projectivbackend.repositories.PostRepository;
import com.cdhi.projectivbackend.repositories.UserRepository;
import com.cdhi.projectivbackend.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    PostRepository repo;

    @Autowired
    UserService userService;

    public Post createPost(NewPostDTO newPostDTO) {
        User user = userService.findOne(newPostDTO.getUserId());

        Post post = new Post();
        post.setOwner(user);
        post.setBody(newPostDTO.getBody());
        post.setTitle(newPostDTO.getTitle());

        Post savedPost = repo.save(post);
        userService.addPost(savedPost, user);
        return savedPost;
    }

    public PostDTO findOne(Integer id) {
        Post post = repo.findById(id).orElseThrow(() ->
                new ObjectNotFoundException("Não foi encontrado o post com id: " + id));
        return new PostDTO(post);
    }
}
