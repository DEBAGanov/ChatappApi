package com.baganov.chatappapi.repository;

import com.baganov.chatappapi.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}