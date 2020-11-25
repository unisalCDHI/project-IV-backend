package com.cdhi.projectivbackend.services;

import com.cdhi.projectivbackend.domain.Post;
import com.cdhi.projectivbackend.dtos.NewPostDTO;
import com.cdhi.projectivbackend.dtos.PostDTO;

import java.util.List;

/**
 * CRUD service that operates Posting feature and save posts in database
 *
 * @author Davi MA
 */
public interface PostService {

    /**
     * Search a post by id
     *
     * @param id is the id saved in DB
     * @return the post object
     * @see PostDTO
     */
    PostDTO findOne(Integer id);

    /**
     * Like a post by id
     *
     * @param postId is the id saved in DB
     * @return the post object with like param inverted
     * @see PostDTO
     */
    PostDTO likePost(Integer postId);

    /**
     * Find all commentaries from a specific post selected by id
     *
     * @param id is the id from post
     * @return the commentaries from the post requested as a list
     * @see PostDTO
     */
    List<PostDTO> findCommentariesFrom(Integer id);

    /**
     * Find all posts from the user who requested the API
     *
     * @return the posts from the user as a list
     * @see PostDTO
     */
    List<PostDTO> findAll();

    /**
     * Create a commentary in a post (generating a new post in DB)
     *
     * @param newCommentaryDTO is the object it will be turned into a new post in DB
     * @param postId is the post id that commentary will be in
     * @return the commentary created
     * @see Post
     */
    Post createCommentary(NewPostDTO newCommentaryDTO, Integer postId);

    /**
     * Create a new post
     *
     * @param newPostDTO is the post object it will be turned into a new post in DB
     * @return the post created
     * @see NewPostDTO
     */
    Post createPost(NewPostDTO newPostDTO);

    /**
     * Repost an existing post to user who requested API
     *
     * @param postId is the post that will be reposted
     */
    void repost(Integer postId);

    /**
     * Get all reposts from current user
     *
     * @return reposts from user who requested API
     * @see PostDTO
     */
    List<PostDTO> getReposts();

    void delete(Integer postId);
}
