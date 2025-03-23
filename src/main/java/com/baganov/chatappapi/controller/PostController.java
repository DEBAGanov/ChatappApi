package com.baganov.chatappapi.controller;

import com.baganov.chatappapi.model.Post;
import com.baganov.chatappapi.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping
    public ResponseEntity<Page<Post>> getAllPosts(@PageableDefault(size = 100) Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return ResponseEntity.ok(posts);
    }
}