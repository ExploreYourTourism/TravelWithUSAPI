package com.travel.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travel.configurations.JwtService;
import com.travel.entity.AuthRequest;
import com.travel.entity.AuthResponse;
import com.travel.entity.RegisterRequest;
import com.travel.entity.User;
import com.travel.service.UserService;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {
	private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private UserService userRepository;

    @Autowired
    private JwtService jwtService;

    // BCrypt encoder — no need to declare a @Bean, instantiate directly here
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ── Register ──────────────────────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        // Check duplicate email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Email already registered"));
        }

        // Check duplicate username
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Username already taken"));
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setProvider("LOCAL");

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());

        return ResponseEntity.ok(new AuthResponse(
                token,
                user.getEmail(),
                user.getUsername(),
                user.getFullName(),
                user.getProfilePicture()
        ));
    }

    // ── Login ─────────────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {

        // Allow login by email OR username
    	String hashedPassword = passwordEncoder.encode(request.getPassword());
//    	user.setPasswordHash(hashedPassword);
//        Optional<User> userOpt = userRepository.findByEmailAndPasswordHash(request.getUsername(),hashedPassword);
    	Optional<User> userOpt = userRepository.findByEmail(request.getUsername());
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByUsername(request.getUsername());
        }

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(new ErrorResponse("Invalid credentials"));
        }

        User user = userOpt.get();
        log.info("Password matched::"+passwordEncoder.matches(request.getPassword(), user.getPasswordHash()));

        // Google-only accounts have no password
        if (user.getPasswordHash() == null) {
            return ResponseEntity.status(401)
                    .body(new ErrorResponse("This account uses Google login. Please sign in with Google."));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401)
                    .body(new ErrorResponse("Invalid credentials"));
        }

        String token = jwtService.generateToken(user.getEmail());

        return ResponseEntity.ok(new AuthResponse(
                token,
                user.getEmail(),
                user.getUsername(),
                user.getFullName(),
                user.getProfilePicture()
        ));
    }

    // ── Inner error wrapper ───────────────────────────────────────────────────
    public record ErrorResponse(String message) {}
}
