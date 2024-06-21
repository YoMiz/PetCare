package com.example.app.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.app.domain.InventoryData;
@Mapper
public interface InventoryMapper {
	//全てのインベントリを取得
	List<InventoryData> showInventory() throws Exception;
	//ペットIDから全てのインベントリデータを取得
	List<InventoryData> showInventoryForPet(int petId) throws Exception;
	InventoryData getInventoryByInventoryId(Integer inventoryId) throws Exception;
	Integer addInventory(InventoryData newInventoryData) throws Exception;
	void addInventoryPet(InventoryData inventoryAddData) throws Exception;
	//
	List<Integer> showPetByInventoryId(Integer inventoryId) throws Exception;
	void updateInventory(Integer integer) throws Exception;
	boolean findinventoryPetByUserId(Integer userId) throws Exception;
	boolean findinventoryPetByPetId(Integer petId) throws Exception;
	List<InventoryData> inventoryPetCheckList(Integer inventoryId) throws Exception;
	void updateInventoryPetByInventoryIdAndPetId(InventoryData inventoryAddData) throws Exception;
	//ペットIDが登録されている全てのインベントリデータを取得する。
	List<InventoryData> getInventoryByPetId(Integer petId) throws Exception;
}