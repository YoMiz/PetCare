package com.example.app.service;

public interface UserService {
	boolean isCorrectIdAndPassword(String userLogin, String userPassword) throws Exception;
}
