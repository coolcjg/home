package com.cjg.home.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@DynamicUpdate
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="home_post")
@ToString
public class Post {
	
	@Id
	@Column(name ="post_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long postId;

	@ManyToOne
	@JoinColumn(name = "userId")
	private User user;

	@Column(nullable = false)
	private String title;

	@Column
	private String content;

	@Column
	private Character open;

	@Column(nullable = false)
	@ColumnDefault("0")
	private Integer viewCnt;

	@CreationTimestamp
	private LocalDateTime regDate;

	private LocalDateTime modDate;

	@OneToMany(mappedBy="post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@OrderBy("regDate asc")
	@BatchSize(size=10)
	private List<Comment> commentList;

}
