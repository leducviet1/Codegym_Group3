package com.example.bookingmeeting_be.services;

import com.example.bookingmeeting_be.model.Role;
import com.example.bookingmeeting_be.model.Users;
import com.example.bookingmeeting_be.model.dto.UserResponse;
import com.example.bookingmeeting_be.repository.RoleRepository;
import com.example.bookingmeeting_be.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    public Users register(Users user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Role roleUser = roleRepository
                .findByName("ROLE_ATTENDEE")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("ROLE_ATTENDEE");
                    return roleRepository.save(r);
                });

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Set.of(roleUser));

        return userRepository.save(user);
    }

    public String verify(Users user) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        if(authentication.isAuthenticated()) {
            return jwtService.generateToken(user.getEmail());
        }
        throw new RuntimeException("Login failed");
    }
    @Transactional
    public Page<UserResponse> listUsers(String q, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("userId").descending());
        Page<Users> usersPage = (q == null || q.isBlank())
                ? userRepository.findAll(pageable)
                : userRepository.findByEmailContainingIgnoreCaseOrFullnameContainingIgnoreCase(q, q, pageable);
        return usersPage.map(this::toDto);
    }
    private UserResponse toDto(Users user) {
        UserResponse dto = new UserResponse();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setFullname(user.getFullname());
        var roleNames = (user.getRole() == null) ? java.util.List.<String>of()
                : user.getRole().stream().map(Role::getName).toList();

        dto.setRoles(roleNames);
        dto.setRolesText(String.join(",", roleNames));

//        boolean isAdmin = user.getRole() != null && user.getRole().stream()
//                .anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));
//
//        dto.setRole(isAdmin ? "ADMIN" : "USER");
        return dto;
    }
    //Update Role user
    @Transactional
    public void updateRolesUser(int userId,Set<String> roleNames) {
        Users user = userRepository.findById(userId).orElseThrow();
        Set<Role> newRoles = roleNames.stream()
                .map(name -> roleRepository.findByName(name).orElseThrow())
                .collect(Collectors.toSet());

        user.setRole(newRoles);
        userRepository.save(user);

    }
    public List<Users> findBookers() {
        return userRepository.findUsersByRoleName("ROLE_BOOKER");
    }
    public List<Users> findAll(){
        return userRepository.findAll();
    }
    public Users findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }
}
