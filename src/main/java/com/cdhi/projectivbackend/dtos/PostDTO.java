package com.cdhi.projectivbackend.dtos;

import com.cdhi.projectivbackend.domain.Post;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class PostDTO {

    private Integer id;
    private String body;
    private UserDTO owner;
    private boolean commentary;
    private String image;
    private List<UserDTO> likes;
    private Long totalLikes;
    private Long totalCommentaries;
    private LocalDateTime createdData;
    private boolean liked;

    public PostDTO(Post post, boolean liked) {
        this.id = post.getId();
        this.body = post.getBody();
        this.owner = new UserDTO(post.getOwner());
        this.commentary = post.isCommentary();
        this.image = post.getImage();
        this.likes = post.getUsersLikes().stream().map(UserDTO::new).collect(Collectors.toList());
        this.totalLikes = (long) post.getUsersLikes().size();
        this.totalCommentaries = (long) post.getCommentaries().size();
        this.createdData = post.getCreatedDate();
        this.liked = liked;
    }
}
