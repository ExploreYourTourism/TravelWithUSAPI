package com.travel.configurations;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.travel.controller.UserController;
import com.travel.dao.UserRepository;
import com.travel.entity.User;

//import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private static final Logger log =
            LoggerFactory.getLogger(OAuth2SuccessHandler.class);

    @Autowired
    private JwtService jwtService;
    @Autowired UserRepository userRepository;
    private static final String FRONTEND_URL = "http://localhost:5173";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        log.info("Attributes: {}", oAuth2User.getAttributes());

        String email     = oAuth2User.getAttribute("email");
        String name      = oAuth2User.getAttribute("name");
        String picture   = oAuth2User.getAttribute("picture");

        // Generate JWT for the authenticated user
        String jwt = jwtService.generateToken(email);
        
        Optional<User> existingUser =
                userRepository.findByEmail(email);

        User user;

        if (existingUser.isPresent()) {
        	 log.info("Google login: existing user {}", email);
            user = existingUser.get();
        } else {
            user = new User();
            user.setEmail(email);
            user.setFullName(name);
            user.setProfilePicture(picture);
            user.setProvider("GOOGLE");
         // username defaults to part before @ in email
            user.setUsername(email.split("@")[0]);
            userRepository.save(user);
            log.info("User saved successfully with ID: {}", user.getId());
        }

        // Return token as JSON
//        response.setContentType("application/json");
//        try {
//			response.getWriter().write(
//			    String.format(
//			        "{\"token\":\"%s\",\"email\":\"%s\",\"name\":\"%s\",\"picture\":\"%s\"}",
//			        jwt, email, name, picture
//			    )
//			);
//		} catch (java.io.IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        String jwt = jwtService.generateToken(email);

        // ── Redirect to React frontend callback page with token ──
        // React will read ?token= from the URL and store it in AuthContext
        String redirectUrl = FRONTEND_URL
                + "/oauth2/callback"
                + "?token=" + jwt
                + "&email=" + encode(email)
                + "&name=" + encode(name != null ? name : "")
                + "&picture=" + encode(picture != null ? picture : "")
                + "&username=" + encode(user.getUsername() != null ? user.getUsername() : "");

        response.sendRedirect(redirectUrl);
    }
    private String encode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }
}