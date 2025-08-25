package com.cjg.home.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
@ToString
@DynamicInsert //checked컬럼 값이 설정이 안되었을 경우 default값으로 설정한다. nullable = false가 있으면 이 어노테이션이 무시된다.
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long alarmId;

    @ManyToOne
    @JoinColumn(name = "userId")
    User user;

    @Column(length = 1000)
    private String message;

    @Column(length = 1000)
    private String link;

    @Column(length = 1)
    @ColumnDefault("'N'")
    private String checked;

    @CreationTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime regDate;
}
