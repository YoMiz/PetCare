package com.example.app.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.app.domain.InventoryData;
@Mapper
public interface InventoryMapper {
	List<InventoryData> showInventory() throws Exception;
	List<InventoryData> showInventoryForPet(int petId) throws Exception;
	Integer addInventory(InventoryData newInventoryData) throws Exception;
	void addPetInventory(InventoryData newInventoryData) throws Exception;
	List<Integer> showPetByInventoryId(Integer inventoryId) throws Exception;
	void updateInventory(InventoryData inventoryUpdateData) throws Exception;
	boolean findPetInventoryByUserId(Integer userId) throws Exception;
	boolean findPetInventoryByPetId(Integer petId) throws Exception;
	List<Integer> inventoryPetCheckList(Integer inventoryId) throws Exception;
}