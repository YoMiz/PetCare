package com.example.app.service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.domain.User;
import com.example.app.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
	private final UserMapper mapper;
	
	@Override
	public boolean isCorrectIdAndPassword(String userLogin, String userPassword) throws Exception {
		User user = mapper.selectUserByUserLogin(userLogin);
		if(user == null) {
			return false;
		}
		if(!BCrypt.checkpw(userPassword,  user.getUserPassword())) {
			return false;
		}
		user = mapper.selectUserByUserLogin(userLogin);
		return true;
	}

}
