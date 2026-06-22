package com.travel.service;

import java.util.Optional;

import com.travel.entity.User;

public interface UserService {
	Optional<User> findByEmailAndPasswordHash(String email,String passwordHash);
	Optional<User> findByUsername(String username);
	void save(User user);
	Optional<User> findByEmail(String email);

}
