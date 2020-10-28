package com.cdhi.projectivbackend.domain;

import com.cdhi.projectivbackend.domain.enums.Profile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity(name = "USER_ENTITY")
@Data
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String username;

    @Column(name = "_name")
    private String name;
    @Column(unique = true)
    private String email;
    private Boolean enabled;

    @Column(columnDefinition = "TEXT")
    private String avatar;

    private Date created;

    @JsonIgnore
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "PROFILES")
    private Set<Integer> profiles = new HashSet<>();

    @JsonIgnore
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "KEYS")
    private List<String> _key = new ArrayList<>();

    @JsonIgnore
    private String password;

    @JsonIgnore
    @ManyToMany(mappedBy = "usersLikes", cascade = CascadeType.DETACH)
    private Set<Post> postsLiked;

    @JsonIgnore
    @ManyToMany(mappedBy = "usersReposts", cascade = CascadeType.DETACH)
    private Set<Post> reposts = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "followers", cascade = CascadeType.DETACH)
    private Set<User> followingUsers = new HashSet<>();

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(name = "USER_FOLLOWS", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "follow_user_id"))
    private Set<User> followers = new HashSet<>();

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(name = "USER_TEAMS", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "team_id"))
    private Set<Team> teams = new HashSet<>();

    public User() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        setEnabled(false);
        get_key().add(
                random.ints(leftLimit, rightLimit + 1)
                        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                        .limit(targetStringLength)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString()
        );
        addProfile(Profile.USER);
        this.created = new Date(System.currentTimeMillis() - 10800000);
    }

    public User(String name, String email, String password) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        this.name = name;
        this.email = email;
        this.password = password;
        addProfile(Profile.USER);
        setEnabled(false);
        get_key().add(
                random.ints(leftLimit, rightLimit + 1)
                        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                        .limit(targetStringLength)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString()
        );
        this.created = new Date(System.currentTimeMillis() - 10800000);
    }

    public Set<Profile> getProfiles() {
        return profiles.stream().map(Profile::toEnum).collect(Collectors.toSet());
    }

    public void addProfile(Profile profile) {
        profiles.add(profile.getCod());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) &&
                email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    public void deleteKey() {
        this._key = null;
    }
}
