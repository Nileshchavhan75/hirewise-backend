package com.hirewise.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hirewise.dto.ApiResponse;
import com.hirewise.dto.BlogPostRequest;
import com.hirewise.dto.BlogResponseDTO;
import com.hirewise.service.BlogService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/blogs")
//@CrossOrigin(origins = "*")
public class BlogController {

    @Autowired
    private BlogService blogService;

    /**
     * Create new blog post (Admin only)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BlogResponseDTO>> createPost(
            @RequestParam Integer authorId,
            @Valid @RequestBody BlogPostRequest request) {
        BlogResponseDTO post = blogService.createPost(authorId, request);
        return new ResponseEntity<>(
            new ApiResponse<>(true, "Blog post created successfully", post),
            HttpStatus.CREATED
        );
    }

    /**
     * Update blog post (Admin only)
     */
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<BlogResponseDTO>> updatePost(
            @PathVariable Integer postId,
            @RequestParam Integer authorId,
            @Valid @RequestBody BlogPostRequest request) {
        BlogResponseDTO updatedPost = blogService.updatePost(postId, authorId, request);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Blog post updated successfully", updatedPost)
        );
    }

    /**
     * Get all published posts
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<BlogResponseDTO>>> getAllPublishedPosts() {
        List<BlogResponseDTO> posts = blogService.getAllPublishedPosts();
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Blog posts fetched successfully", posts)
        );
    }

    /**
     * Get post by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BlogResponseDTO>> getPostById(@PathVariable Integer id) {
        BlogResponseDTO post = blogService.getPostById(id);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Blog post fetched successfully", post)
        );
    }

    /**
     * Get post by slug
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<BlogResponseDTO>> getPostBySlug(@PathVariable String slug) {
        BlogResponseDTO post = blogService.getPostBySlug(slug);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Blog post fetched successfully", post)
        );
    }

    /**
     * Get recent posts (top 5)
     */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<BlogResponseDTO>>> getRecentPosts() {
        List<BlogResponseDTO> posts = blogService.getRecentPosts();
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Recent posts fetched successfully", posts)
        );
    }

    /**
     * Delete post (Admin only)
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Integer postId,
            @RequestParam Integer adminId) {
        blogService.deletePost(postId, adminId);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Blog post deleted successfully", null)
        );
    }
}