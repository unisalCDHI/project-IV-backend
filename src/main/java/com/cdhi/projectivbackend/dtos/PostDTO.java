package com.cdhi.projectivbackend.dtos;

import com.cdhi.projectivbackend.domain.Post;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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
    private LocalDateTime createdDate;
    private boolean liked;
    private boolean repostedByMe;
    private Set<UserDTO> usersRepostList;


    public PostDTO(Post post, boolean liked, boolean reposted) {
        this.id = post.getId();
        this.body = post.getBody();
        this.owner = new UserDTO(post.getOwner());
        this.commentary = post.isCommentary();
        this.image = post.getImage();
        this.likes = post.getUsersLikes().stream().map(UserDTO::new).collect(Collectors.toList());
        this.totalLikes = (long) post.getUsersLikes().size();
        this.totalCommentaries = (long) post.getCommentaries().size();
        this.createdDate = post.getCreatedDate();
        this.usersRepostList = post.getUsersReposts().stream().map(UserDTO::new).collect(Collectors.toSet());
        this.liked = liked;
        this.repostedByMe = reposted;
    }
}
