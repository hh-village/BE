package com.sparta.village.domain.image.entity;

import com.sparta.village.domain.product.entity.Product;

import javax.persistence.*;

@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String fileName;
    @Column(nullable = false)
    private String s3Key;
    @Column(nullable = false)
    private String imageUrl;
    @Column(nullable = false)
    private String contentType;
    @Column(nullable = false)
    private Long size;

}
