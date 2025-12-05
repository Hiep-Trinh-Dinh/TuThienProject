package com.example.server.repository;

import com.example.server.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    boolean existsByEmail(String email);

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

    @Query("""
            SELECT u FROM User u
            WHERE 
                (
                    :q IS NULL OR :q = '' OR 
                    LOWER(u.fullName) LIKE LOWER(CONCAT('%', :q, '%')) OR
                    LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%')) OR
                    LOWER(u.phone) LIKE LOWER(CONCAT('%', :q, '%'))
                )
                AND (:authProvider IS NULL OR :authProvider = '' OR LOWER(u.authProvider) LIKE LOWER(CONCAT('%', :authProvider, '%')))
                AND (:statusEnum IS NULL OR u.status = :statusEnum)
            """)
    Page<User> findUsersWithFilters(
            @Param("q") String q,
            @Param("authProvider") String authProvider,
            @Param("statusEnum") User.Status statusEnum,
            @Param("sortBy") String sortBy,
            Pageable pageable
    );
}

