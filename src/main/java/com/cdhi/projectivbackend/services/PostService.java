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

    @Autowired
    private UserRepository userRepository;

    public Post createPost(NewPostDTO newPostDTO) {
        User user = userService.getWebRequestUser();

        Post post = new Post();
        post.setOwner(user);
        post.setBody(newPostDTO.getBody());
        post.setTitle(newPostDTO.getTitle());
        post.setImage(newPostDTO.getImage());

        Post savedPost = repo.save(post);
        userService.addPost(savedPost, user);
        return savedPost;
    }

    public PostDTO findOne(Integer id) {
        Post post = repo.findById(id).orElseThrow(() ->
                new ObjectNotFoundException("Não foi encontrado o post com id: " + id));
        return new PostDTO(post);
    }

    public Post likePost(Integer postId) {
        try {
            User user = userService.getWebRequestUser();
            Post post = repo.findById(postId).orElseThrow(() ->
                    new ObjectNotFoundException("Não foi encontrado o post com id: " + postId));

            if (post.getUsersLikes().stream().anyMatch(u -> u.getId().equals(user.getId())) &&
                    user.getPosts().stream().anyMatch(p -> p.getId().equals(postId))) {
                post.getUsersLikes().removeIf(u -> u.getId().equals(user.getId()));
                user.getPostsLiked().removeIf(p -> p.getId().equals(post.getId()));
            } else {
                post.getUsersLikes().add(user);
                user.getPostsLiked().add(post);
            }
            userRepository.save(user);
            return repo.save(post);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
