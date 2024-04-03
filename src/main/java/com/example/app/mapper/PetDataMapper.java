package com.example.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.app.domain.PetData;

@Mapper
public interface PetDataMapper {
	List<PetData> showPets() throws Exception;
	List<PetData> showUserPetByUserId(Integer userId) throws Exception;
}
