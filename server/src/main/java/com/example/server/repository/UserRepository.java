package com.example.server.repository;

import com.example.server.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByFullName(String fullName);
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<User> findByEmail(String email);
    boolean existsByFullName(String fullName);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.passwordHash = ?2 WHERE u.email = ?1")
    void updatePassword(String email, String password);
}
