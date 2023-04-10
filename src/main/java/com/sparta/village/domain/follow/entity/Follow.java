package com.sparta.village.domain.follow.entity;

import com.sparta.village.domain.user.entity.User;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int followCount = 0;

    @Column(nullable = false)
    private String followUserNickname;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private void plusFollowCount() {
        this.followCount ++;
    }

    private void minusFollowCount() {
        this.followCount --;
    }
}
