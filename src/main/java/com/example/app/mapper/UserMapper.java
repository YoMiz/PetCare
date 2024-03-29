package com.example.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.app.domain.User;

@Mapper
public interface UserMapper {
	List<User> showUser() throws Exception;
	User selectUserByUserId(String userLogin) throws Exception;
	
}
