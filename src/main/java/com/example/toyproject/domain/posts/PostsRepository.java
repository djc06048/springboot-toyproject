package com.example.toyproject.domain.posts;

import com.example.toyproject.domain.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface PostsRepository extends JpaRepository<Posts,Long> {
    List<Posts> findByTitle(String title);
    Optional<Posts> findByPostId(Long postId);
    @Query("SELECT p FROM Posts p ORDER BY p.postId DESC ")
    List<Posts> findAllDesc();
}
