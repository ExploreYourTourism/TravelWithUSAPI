package com.travel.entity;

public class AuthResponse {

    private String token;
    private String email;
    private String username;
    private String fullName;
    private String profilePicture;

    public AuthResponse(String token, String email, String username,
                        String fullName, String profilePicture) {
        this.token          = token;
        this.email          = email;
        this.username       = username;
        this.fullName       = fullName;
        this.profilePicture = profilePicture;
    }

    public String getToken()                { return token; }
    public String getEmail()                { return email; }
    public String getUsername()             { return username; }
    public String getFullName()             { return fullName; }
    public String getProfilePicture()       { return profilePicture; }
}
