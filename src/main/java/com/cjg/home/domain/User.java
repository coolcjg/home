package com.cjg.home.domain;

import com.cjg.home.code.SocialType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@DynamicUpdate
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="home_user")
public class User {

	@Id
	@Column(name ="user_id", length = 20)
	private String userId;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false)
	private String password;

	@Column(nullable = true, length = 255)
	private String image;

	@Column(nullable = false, length=10)
	private String auth;

	@CreationTimestamp
	private LocalDateTime regDate;

	private LocalDateTime modDate;

    @Column(nullable = true, length = 200)
    private String email;

    @Column
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column
    private String socialId;

}
