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

import java.util.List;
import java.util.stream.Collectors;

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
        post.setCommentary(false);

        Post savedPost = repo.save(post);
        userService.addPost(savedPost, user);
        return savedPost;
    }

    public Post createCommentary(NewPostDTO newCommentaryDTO, Integer postId) {
        User user = userService.getWebRequestUser();
        Post parentPost = repo.findById(postId).orElseThrow(() ->
                new ObjectNotFoundException("Não foi encontrado o post com id: " + postId));

        Post commentary = new Post();
        commentary.setOwner(user);
        commentary.setBody(newCommentaryDTO.getBody());
        commentary.setTitle(newCommentaryDTO.getTitle());
        commentary.setImage(newCommentaryDTO.getImage());
        commentary.setCommentary(true);

        parentPost.getCommentaries().add(commentary);
        commentary.setParentPost(parentPost);

        repo.save(parentPost);
        Post savedCommentary = repo.save(commentary);

        userService.addPost(savedCommentary, user);
        return savedCommentary;
    }

    public PostDTO findOne(Integer id) {
        Post post = repo.findById(id).orElseThrow(() ->
                new ObjectNotFoundException("Não foi encontrado o post com id: " + id));
        return new PostDTO(post);
    }

    public PostDTO likePost(Integer postId) {
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
            return new PostDTO(repo.save(post));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<PostDTO> findCommentariesFrom(Integer id) {
        Post post = repo.findById(id).orElseThrow(() ->
                new ObjectNotFoundException("Não foi encontrado o post com id: " + id));
        return post.getCommentaries().stream().map(PostDTO::new).collect(Collectors.toList());
    }
}
