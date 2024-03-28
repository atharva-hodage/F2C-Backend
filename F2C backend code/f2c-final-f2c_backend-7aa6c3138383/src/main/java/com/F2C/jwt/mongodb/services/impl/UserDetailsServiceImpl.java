package com.F2C.jwt.mongodb.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.F2C.jwt.mongodb.models.User;
import com.F2C.jwt.mongodb.repository.UserRepository;
import com.F2C.jwt.mongodb.services.UserDetailsImpl;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String phoneNo) throws UsernameNotFoundException {
		User user = userRepository.findByPhoneNo(phoneNo)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with phoneNO: " + phoneNo));

		return UserDetailsImpl.build(user);
	}
	

}
