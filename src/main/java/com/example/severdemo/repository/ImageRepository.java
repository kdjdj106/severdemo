package com.example.severdemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.severdemo.Image;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

}
