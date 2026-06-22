package com.travel.entity;

// identifier = email OR username — backend tries both
public class AuthRequest {

    private String username;
    private String password;



    public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword()             { return password; }
    public void   setPassword(String v)     { this.password = v; }
}
