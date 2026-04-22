package com.hirewise.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hirewise.dto.BlogPostRequest;
import com.hirewise.dto.BlogResponseDTO;
import com.hirewise.entity.BlogPost;
import com.hirewise.entity.User;
import com.hirewise.exception.ResourceNotFoundException;
import com.hirewise.exception.UnauthorizedException;
import com.hirewise.repository.BlogPostRepository;
import com.hirewise.repository.UserRepository;

@Service
public class BlogService {

    @Autowired
    private BlogPostRepository blogRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create new blog post (Admin only)
     */
    @Transactional
    public BlogResponseDTO createPost(Integer authorId, BlogPostRequest request) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

        BlogPost post = new BlogPost();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setExcerpt(request.getExcerpt());
        post.setAuthor(author);
        post.setCategory(request.getCategory());
        post.setTags(request.getTags());
        post.setIsPublished(request.getIsPublished());
        post.setFeaturedImageUrl(request.getFeaturedImageUrl());
        post.setSlug(request.getSlug());
        post.setPublishedDate(LocalDateTime.now());
        post.setViewCount(0);

        BlogPost savedPost = blogRepository.save(post);
        return convertToDTO(savedPost);
    }

    /**
     * Update blog post (Admin only)
     */
    @Transactional
    public BlogResponseDTO updatePost(Integer postId, Integer authorId, BlogPostRequest request) {
        BlogPost post = blogRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with id: " + postId));

        // Check if user is author or admin
        if (!post.getAuthor().getId().equals(authorId) && !isAdmin(authorId)) {
            throw new UnauthorizedException("You are not authorized to update this post");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setExcerpt(request.getExcerpt());
        post.setCategory(request.getCategory());
        post.setTags(request.getTags());
        post.setIsPublished(request.getIsPublished());
        post.setFeaturedImageUrl(request.getFeaturedImageUrl());
        post.setSlug(request.getSlug());

        BlogPost updatedPost = blogRepository.save(post);
        return convertToDTO(updatedPost);
    }

    /**
     * Get all published posts
     */
    public List<BlogResponseDTO> getAllPublishedPosts() {
        return blogRepository.findByIsPublishedTrueOrderByPublishedDateDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get post by ID
     */
    public BlogResponseDTO getPostById(Integer id) {
        BlogPost post = blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with id: " + id));

        // Increment view count
        post.setViewCount(post.getViewCount() + 1);
        blogRepository.save(post);

        return convertToDTO(post);
    }

    /**
     * Get post by slug (for SEO-friendly URLs)
     */
    public BlogResponseDTO getPostBySlug(String slug) {
        BlogPost post = blogRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with slug: " + slug));

        // Increment view count
        post.setViewCount(post.getViewCount() + 1);
        blogRepository.save(post);

        return convertToDTO(post);
    }

    /**
     * Get posts by category
     */
    public List<BlogResponseDTO> getPostsByCategory(String category) {
        // Convert string to enum (simplified)
        return blogRepository.findByCategoryAndIsPublishedTrue(null).stream() // Will fix enum conversion later
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get recent posts (top 5)
     */
    public List<BlogResponseDTO> getRecentPosts() {
        return blogRepository.findTop5ByIsPublishedTrueOrderByPublishedDateDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Delete post (Admin only)
     */
    @Transactional
    public void deletePost(Integer postId, Integer adminId) {
        BlogPost post = blogRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found"));

        if (!isAdmin(adminId)) {
            throw new UnauthorizedException("Only admins can delete posts");
        }

        blogRepository.delete(post);
    }

    /**
     * Convert BlogPost entity to DTO
     */
    private BlogResponseDTO convertToDTO(BlogPost post) {
        BlogResponseDTO dto = new BlogResponseDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setExcerpt(post.getExcerpt());

        if (post.getAuthor() != null) {
            dto.setAuthorName(post.getAuthor().getEmail()); // Will be replaced with full name later
            dto.setAuthorId(post.getAuthor().getId());
        }

        dto.setCategory(post.getCategory());
        dto.setTags(post.getTags());
        dto.setPublishedDate(post.getPublishedDate());
        dto.setIsPublished(post.getIsPublished());
        dto.setFeaturedImageUrl(post.getFeaturedImageUrl());
        dto.setViewCount(post.getViewCount());
        dto.setSlug(post.getSlug());

        return dto;
    }

    /**
     * Check if user is admin
     */
    private boolean isAdmin(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null && user.getRole().toString().equals("admin");
    }
}