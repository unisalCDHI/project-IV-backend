package com.cdhi.projectivbackend.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "POST")
@Data
public class Post implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    //TODO
//    private List<User> repostUserList;
}
