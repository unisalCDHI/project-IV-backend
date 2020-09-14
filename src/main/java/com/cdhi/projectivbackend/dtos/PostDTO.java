package com.cdhi.projectivbackend.dtos;

import com.cdhi.projectivbackend.domain.Post;
import lombok.Data;

@Data
public class PostDTO {

    private Integer id;
    private String title;
    private String body;
    private UserDTO owner;
    private String image;
    private Long likes;

    public PostDTO(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.owner = new UserDTO(post.getOwner());
        this.image = post.getImage();
        this.likes = (long) post.getUsersLikes().size();
    }
}
