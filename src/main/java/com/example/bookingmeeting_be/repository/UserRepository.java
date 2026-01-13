package com.example.bookingmeeting_be.repository;

import com.example.bookingmeeting_be.model.Role;
import com.example.bookingmeeting_be.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);

    Page<Users> findByEmailContainingIgnoreCaseOrFullnameContainingIgnoreCase(String email, String fullName, Pageable pageable);
    @Query("""
        SELECT DISTINCT u
        FROM Users u
        JOIN u.role r
        WHERE r.name = :roleName
    """)
    List<Users> findUsersByRoleName(@Param("roleName") String roleName);
}
