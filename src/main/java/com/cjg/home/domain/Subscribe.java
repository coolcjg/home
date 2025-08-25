package com.cjg.home.domain;

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
@Table(name="home_subscribe")
@IdClass(SubscribePrimaryKey.class)
@ToString
public class Subscribe {

    @Id
	@ManyToOne
	@JoinColumn(name = "userId")
	private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "targetUserId")
    private User targetUser;

	@CreationTimestamp
	private LocalDateTime regDate;

}
