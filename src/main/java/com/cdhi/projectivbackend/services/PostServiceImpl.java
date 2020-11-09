package com.cdhi.projectivbackend.services;

import com.cdhi.projectivbackend.domain.Post;
import com.cdhi.projectivbackend.domain.User;
import com.cdhi.projectivbackend.dtos.NewPostDTO;
import com.cdhi.projectivbackend.dtos.PostDTO;
import com.cdhi.projectivbackend.repositories.PostRepository;
import com.cdhi.projectivbackend.repositories.UserRepository;
import com.cdhi.projectivbackend.services.exceptions.AuthorizationException;
import com.cdhi.projectivbackend.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    PostRepository repo;

    @Autowired
    UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Post createPost(NewPostDTO newPostDTO) {

        if((newPostDTO.getImage() == null || newPostDTO.getImage().equals("")) && (newPostDTO.getBody() == null || newPostDTO.getBody().equals("")))
            throw new AuthorizationException("Você deve preencher o campo 'body' ou o campo 'image'");

        User user = userService.getWebRequestUser();

        Post post = new Post();
        post.setOwner(user);
        post.setBody(newPostDTO.getBody());
        post.setImage(newPostDTO.getImage());
        post.setCommentary(false);

        Post savedPost = repo.save(post);
        userService.addPost(savedPost, user);
        return savedPost;
    }

    @Override
    public Post createCommentary(NewPostDTO newCommentaryDTO, Integer postId) {
        if((newCommentaryDTO.getImage() == null || newCommentaryDTO.getImage().equals("")) && (newCommentaryDTO.getBody() == null || newCommentaryDTO.getBody().equals("")))
            throw new AuthorizationException("Você deve preencher o campo 'body' ou o campo 'image'");

        User user = userService.getWebRequestUser();
        Post parentPost = repo.findById(postId).orElseThrow(() ->
                new ObjectNotFoundException("Não foi encontrado o post com id: " + postId));

        Post commentary = new Post();
        commentary.setOwner(user);
        commentary.setBody(newCommentaryDTO.getBody());
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
        User user = userService.getWebRequestUser();
        Post post = repo.findById(id).orElseThrow(() ->
                new ObjectNotFoundException("Não foi encontrado o post com id: " + id));
        return new PostDTO(post, post.getUsersLikes().stream().anyMatch(u -> u.getId().equals(user.getId())),
                post.getUsersReposts().stream().anyMatch(u -> u.getId().equals(user.getId())));
    }

    @Override
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
            return new PostDTO(repo.save(post), post.getUsersLikes().stream().anyMatch(u -> u.getId().equals(user.getId())),
                    post.getUsersReposts().stream().anyMatch(u -> u.getId().equals(user.getId())));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<PostDTO> findCommentariesFrom(Integer id) {
        User user = userService.getWebRequestUser();
        Post post = repo.findById(id).orElseThrow(() ->
                new ObjectNotFoundException("Não foi encontrado o post com id: " + id));
        return post.getCommentaries().stream().map(comment -> new PostDTO(comment, comment.getUsersLikes().stream().anyMatch(u -> u.getId().equals(user.getId())),
                comment.getUsersReposts().stream().anyMatch(u -> u.getId().equals(user.getId())))).collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> findAll() {
        User user = userService.getWebRequestUser();
        List<Post> userPosts = repo.findAllByOwner_id(user.getId());
        List<Post> followingUsersPosts = new ArrayList<>();
        List<Post> repostsByFollowings = new ArrayList<>();

        for (User u : user.getFollowingUsers()) {
            followingUsersPosts.addAll(repo.findAllByOwner_id(u.getId()));
            repostsByFollowings.addAll(u.getReposts());
        }

        List<Post> posts = new ArrayList<>();
        posts.addAll(userPosts);
        posts.addAll(followingUsersPosts);

        for (Post p : repostsByFollowings) {
            posts.removeIf(post -> post.getId().equals(p.getId()));
        }

        posts.addAll(repostsByFollowings);

        return posts.stream().map(p ->
                new PostDTO(
                        p,
                        p.getUsersLikes().stream().anyMatch(u -> u.getId().equals(user.getId())),
                        p.getUsersReposts().stream().anyMatch(u -> u.getId().equals(user.getId()))
                ))
                .sorted((p1, p2) ->
                        p1.getCreatedDate().isAfter(p2.getCreatedDate()) ? -1 :
                                p1.getCreatedDate().isAfter(p2.getCreatedDate()) ? 1 : 0)
                .collect(Collectors.toList());
    }

    @Override
    public void repost(Integer postId) {
        User user = userService.getWebRequestUser();
        Post post = repo.findById(postId).orElseThrow(() ->
                new ObjectNotFoundException("Não foi encontrado o post com id: " + postId));

        post.getUsersReposts().add(user);
        user.getReposts().add(post);

        userRepository.save(user);
        repo.save(post);
    }

    @Override
    public List<PostDTO> getReposts() {
        User user = userService.getWebRequestUser();
        Set<Post> posts = user.getReposts();
        return posts.stream()
                .map(post -> new PostDTO(post, post.getUsersLikes().stream().anyMatch(u -> u.getId().equals(user.getId())), true))
                .collect(Collectors.toList());
    }

}
