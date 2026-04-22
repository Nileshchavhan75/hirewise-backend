package com.hirewise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hirewise.entity.BlogCategory;
import com.hirewise.entity.BlogPost;
import com.hirewise.entity.User;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Integer> {

    // Find all published posts
    List<BlogPost> findByIsPublishedTrueOrderByPublishedDateDesc();

    // Find by category
    List<BlogPost> findByCategoryAndIsPublishedTrue(BlogCategory category);

    // Find by author
    List<BlogPost> findByAuthor(User author);

    // Find by slug
    Optional<BlogPost> findBySlug(String slug);

    // Find recent posts
    List<BlogPost> findTop5ByIsPublishedTrueOrderByPublishedDateDesc();

    // Search blog posts
    List<BlogPost> findByTitleContainingIgnoreCaseAndIsPublishedTrue(String title);
}