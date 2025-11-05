package com.example.server.repository;

import com.example.server.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByFullName(String fullName);
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<User> findByEmail(String email);
    boolean existsByFullName(String fullName);

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    @NonNull
    @Override
    List<User> findAll();

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    @NonNull
    @Override
    Optional<User> findById(@NonNull Long id);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.passwordHash = ?2 WHERE u.email = ?1")
    void updatePassword(String email, String password);
}
