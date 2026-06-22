package com.travel.controller;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travel.entity.User;
import com.travel.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
	private static final Logger log =
            LoggerFactory.getLogger(UserController.class);
	@Autowired UserService userser;

    @GetMapping("/oauthAuthentication")
    public ResponseEntity<?> getCurrentUser(
            @AuthenticationPrincipal OAuth2User principal) {

        if (principal == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }
        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");
        String picture = principal.getAttribute("picture");

        Optional<User> existingUser =
        		userser.findByEmail(email);

        User user;

        if (existingUser.isPresent()) {
        	 log.info("User already exists in DB");
            user = existingUser.get();
        } else {
            user = new User();
            user.setEmail(email);
            user.setFullName(name);
            user.setProfilePicture(picture);
            user.setProvider("GOOGLE");

            userser.save(user);
            log.info("User saved successfully with ID: {}", user.getId());
        }

        return ResponseEntity.ok(user);
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return ResponseEntity.ok(Map.of("token", authHeader));
    }
}