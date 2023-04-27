package com.sparta.village.domain.visitor.repository;

import com.sparta.village.domain.visitor.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VisitorCountRepository extends JpaRepository<Visitor, Long> {
    @Query("SELECT v.visitorCount FROM Visitor v WHERE v.id = 1")
    Long findVisitorCountById();
}
