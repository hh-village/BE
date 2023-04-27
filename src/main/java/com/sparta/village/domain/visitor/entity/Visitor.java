package com.sparta.village.domain.visitor.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer visitorCount;

    public Visitor(Integer visitorCount) {
        this.visitorCount = visitorCount;
    }

    public void updateVisitorCount(Integer visitorCount) {
        this.visitorCount = visitorCount;
    }
}
