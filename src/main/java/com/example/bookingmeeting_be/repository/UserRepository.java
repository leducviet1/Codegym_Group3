package com.example.bookingmeeting_be.repository;

import com.example.bookingmeeting_be.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<Users> findByEmailContainingIgnoreCase(String email, String fullName, Pageable pageable);
}
