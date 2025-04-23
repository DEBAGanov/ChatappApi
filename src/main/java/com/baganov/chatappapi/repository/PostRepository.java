package com.baganov.chatappapi.repository;

import com.baganov.chatappapi.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Получить следующий пост
    @Query("SELECT p FROM Post p WHERE p.id > :currentId ORDER BY p.id ASC")
    List<Post> findNextPosts(@Param("currentId") Long currentId, Pageable pageable);

    // Получить предыдущий пост
    @Query("SELECT p FROM Post p WHERE p.id < :currentId ORDER BY p.id DESC")
    List<Post> findPreviousPosts(@Param("currentId") Long currentId, Pageable pageable);

    // Получить случайный пост
    @Query(value = "SELECT * FROM posts ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Post findRandomPost();

    // Получить первый пост (по ID)
    @Query("SELECT p FROM Post p ORDER BY p.id ASC")
    List<Post> findFirstPosts(Pageable pageable);

    // Получить последний пост (по ID)
    @Query("SELECT p FROM Post p ORDER BY p.id DESC")
    List<Post> findLastPosts(Pageable pageable);

    // Простой поиск по частичному совпадению в title (регистронезависимый)
    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Post> findByTitleContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Полнотекстовый поиск по title с использованием PostgreSQL
    @Query(value = "SELECT * FROM posts WHERE to_tsvector('russian', title) @@ plainto_tsquery('russian', :searchTerm)", countQuery = "SELECT COUNT(*) FROM posts WHERE to_tsvector('russian', title) @@ plainto_tsquery('russian', :searchTerm)", nativeQuery = true)
    Page<Post> fullTextSearchByTitle(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Комбинированный полнотекстовый поиск с ранжированием результатов
    @Query(value = "SELECT *, ts_rank(to_tsvector('russian', title), plainto_tsquery('russian', :searchTerm)) AS rank "
            +
            "FROM posts " +
            "WHERE to_tsvector('russian', title) @@ plainto_tsquery('russian', :searchTerm) " +
            "ORDER BY rank DESC", countQuery = "SELECT COUNT(*) FROM posts WHERE to_tsvector('russian', title) @@ plainto_tsquery('russian', :searchTerm)", nativeQuery = true)
    Page<Post> fullTextSearchByTitleWithRanking(@Param("searchTerm") String searchTerm, Pageable pageable);
}