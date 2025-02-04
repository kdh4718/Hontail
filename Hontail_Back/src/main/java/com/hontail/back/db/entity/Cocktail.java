package com.hontail.back.db.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cocktail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cocktail_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Size(max = 50)
    @NotNull
    @Column(name = "maker_nickname", nullable = false, length = 50)
    private String makerNickname;

    @Size(max = 100)
    @NotNull
    @Column(name = "cocktail_name", nullable = false, length = 100)
    private String cocktailName;

    @Lob
    @Column(name = "cocktail_description")
    private String cocktailDescription;

    @NotNull
    @Column(name = "alcohol_content", nullable = false)
    private Integer alcoholContent;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "likes", nullable = false)
    private Integer likesCnt;

    @Size(max = 255)
    @Column(name = "image_url")
    private String imageUrl;

    @ColumnDefault("0")
    @Column(name = "is_custom")
    private Byte isCustom;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Size(max = 50)
    @Column(name = "base_spirit", length = 50)
    private String baseSpirit;

    @OneToMany(mappedBy = "cocktail")
    private List<CocktailIngredient> cocktailIngredients = new ArrayList<>();

    @OneToMany(mappedBy = "cocktail")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "cocktail")
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "cocktail")
    private List<Recipe> recipes = new ArrayList<>();

    public void addLike() {
        this.likesCnt++;
    }

    public void removeLike() {
        if (this.likesCnt > 0) {
            this.likesCnt--;
        }
    }

}
