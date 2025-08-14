package com.cjg.home.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@DynamicUpdate
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="home_comment")
@ToString
public class Comment {

    @Id
    @Column(name ="comment_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long commentId;

    /*
    대댓글 기능 : 시간 부족으로 인해 미구현
     */
    @ManyToOne
    @JoinColumn(name="parent_id")
    private Comment parent;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @ColumnDefault("'N'")
    private Character deleted;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    @Builder.Default
    private List<Comment> children = new ArrayList<>();

    //부모댓글 수정
    public void updateParent(Comment parent){
        this.parent = parent;
    }

    @CreationTimestamp
    private LocalDateTime regDate;

    private LocalDateTime modDate;

}
