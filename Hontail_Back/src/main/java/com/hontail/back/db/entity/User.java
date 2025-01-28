package com.hontail.back.db.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Column(name = "user_nickname", nullable = false, length = 50)
    private String userNickname;

    @Size(max = 255)
    @NotNull
    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Size(max = 255)
    @Column(name = "user_image_url")
    private String userImageUrl;

    @OneToMany(mappedBy = "user")
    private List<Cocktail> cocktails = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Like> likes = new ArrayList<>();

}