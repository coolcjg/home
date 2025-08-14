package com.cjg.home.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

}
