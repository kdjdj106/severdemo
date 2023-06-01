package com.example.severdemo;

import lombok.*;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@SuperBuilder
@AllArgsConstructor
public abstract class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String uuid;
    @NonNull
    private String fileName;
    @NonNull
    private String fileUrl;
}
