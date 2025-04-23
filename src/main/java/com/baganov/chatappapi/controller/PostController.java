package com.baganov.chatappapi.controller;

import com.baganov.chatappapi.model.Post;
import com.baganov.chatappapi.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return postRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/next/{currentId}")
    public ResponseEntity<Post> getNextPost(@PathVariable Long currentId) {
        List<Post> nextPosts = postRepository.findNextPosts(currentId, PageRequest.of(0, 1));

        if (!nextPosts.isEmpty()) {
            return ResponseEntity.ok(nextPosts.get(0));
        } else {
            // Если нет следующего, вернуть первый (цикличность)
            List<Post> firstPosts = postRepository.findFirstPosts(PageRequest.of(0, 1));
            return !firstPosts.isEmpty() ? ResponseEntity.ok(firstPosts.get(0)) : ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/previous/{currentId}")
    public ResponseEntity<Post> getPreviousPost(@PathVariable Long currentId) {
        List<Post> previousPosts = postRepository.findPreviousPosts(currentId, PageRequest.of(0, 1));

        if (!previousPosts.isEmpty()) {
            return ResponseEntity.ok(previousPosts.get(0));
        } else {
            // Если нет предыдущего, вернуть последний (цикличность)
            List<Post> lastPosts = postRepository.findLastPosts(PageRequest.of(0, 1));
            return !lastPosts.isEmpty() ? ResponseEntity.ok(lastPosts.get(0)) : ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/random")
    public ResponseEntity<Post> getRandomPost() {
        Post random = postRepository.findRandomPost();
        return random != null ? ResponseEntity.ok(random) : ResponseEntity.notFound().build();
    }

    // Упрощенные эндпоинты без указания currentId
    @GetMapping("/next")
    public ResponseEntity<Post> getNextPost() {
        List<Post> firstPosts = postRepository.findFirstPosts(PageRequest.of(0, 1));
        return !firstPosts.isEmpty() ? ResponseEntity.ok(firstPosts.get(0)) : ResponseEntity.notFound().build();
    }

    @GetMapping("/previous")
    public ResponseEntity<Post> getPreviousPost() {
        List<Post> lastPosts = postRepository.findLastPosts(PageRequest.of(0, 1));
        return !lastPosts.isEmpty() ? ResponseEntity.ok(lastPosts.get(0)) : ResponseEntity.notFound().build();
    }

    /**
     * Полнотекстовый поиск по заголовку постов.
     *
     * @param query           термин для поиска
     * @param useSimpleSearch использовать ли простой поиск (по умолчанию false)
     * @param pageable        параметры пагинации
     * @return страница с результатами поиска
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Post>> searchPosts(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "false") boolean useSimpleSearch,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<Post> searchResults;

        if (useSimpleSearch) {
            // Простой поиск по подстроке
            searchResults = postRepository.findByTitleContainingIgnoreCase(query, pageable);
        } else {
            // Полнотекстовый поиск с ранжированием результатов
            searchResults = postRepository.fullTextSearchByTitleWithRanking(query, pageable);
        }

        return ResponseEntity.ok(searchResults);
    }
}