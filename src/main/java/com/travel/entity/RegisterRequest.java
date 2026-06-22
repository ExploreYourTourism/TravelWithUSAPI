package com.travel.entity;

public class RegisterRequest {

    private String fullName;
    private String username;
    private String email;
    private String phone;
    private String password;

    public String getFullName()             { return fullName; }
    public void   setFullName(String v)     { this.fullName = v; }

    public String getUsername()             { return username; }
    public void   setUsername(String v)     { this.username = v; }

    public String getEmail()                { return email; }
    public void   setEmail(String v)        { this.email = v; }

    public String getPhone()                { return phone; }
    public void   setPhone(String v)        { this.phone = v; }

    public String getPassword()             { return password; }
    public void   setPassword(String v)     { this.password = v; }
}
