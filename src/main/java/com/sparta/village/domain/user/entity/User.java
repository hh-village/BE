package com.sparta.village.domain.user.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "update users set is_deleted = true where id = ?")
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long kakaoId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String profile;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    private boolean isDeleted = Boolean.FALSE;

    public User(Long kakaoId, String nickname, String profile, UserRoleEnum role) {
        this.kakaoId = kakaoId;
        this.nickname = nickname;
        this.profile = profile;
        this.role = role;
    }

    public void updateProfile(String profile) {
        this.profile = profile;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

}