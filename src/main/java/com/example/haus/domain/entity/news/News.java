package com.example.haus.domain.entity.news;

import com.example.haus.domain.entity.BaseEntity;
import com.example.haus.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "news")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class News extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String title;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    String content;

    @ElementCollection
    @CollectionTable(name = "news_images", joinColumns = @JoinColumn(name = "news_id"))
    @Column(name = "image_url")
    List<String> images;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    User author;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    List<CommentNews> comments;

    /* ========= Helper methods ========= */

    public void addComment(CommentNews comment) {
        comments.add(comment);
        comment.setNews(this);
    }

    public void removeComment(CommentNews comment) {
        comments.remove(comment);
        comment.setNews(null);
    }
}
