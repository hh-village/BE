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
    private Long visitorCount;

    public Visitor(Long visitorCount) {
        this.visitorCount = visitorCount;
    }

    public void updateVisitorCount(Long visitorCount) {
        this.visitorCount = visitorCount;
    }
}
