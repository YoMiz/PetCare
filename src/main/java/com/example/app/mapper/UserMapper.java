package com.example.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.app.domain.ContactData;
import com.example.app.domain.InventoryData;
import com.example.app.domain.User;

@Mapper
public interface UserMapper {
	List<User> showUser() throws Exception;
	User selectUserByUserLogin(String userLogin) throws Exception;
	List<InventoryData> selectInventoryByUserId(Integer userId) throws Exception;
	List<ContactData> selectContactByUserId(Integer userId) throws Exception;
	
	
}
