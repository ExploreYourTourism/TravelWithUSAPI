package com.travel.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.travel.dao.UserRepository;
import com.travel.entity.User;

@Service
public class UserServiceImpl implements UserService {
	@Autowired UserRepository userRepository;
	 public Optional<User> findByEmail(String email){
		 return userRepository.findByEmail(email);
	 }
	 
	public Optional<User> findByEmailAndPasswordHash(String email,String passwordhash){
		return userRepository.findByEmailAndPasswordHash(email,passwordhash);
	}

	public Optional<User> findByUsername(String username){
		return userRepository.findByUsername(username);
	}

	@Override
	public void save(User user) {
		userRepository.save(user);
		
	}

}
