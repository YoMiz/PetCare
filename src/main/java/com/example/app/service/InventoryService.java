package com.example.app.service;

import java.util.List;

import com.example.app.domain.InventoryData;

public interface InventoryService {
	void addInventory(Integer userId, InventoryData inventoryAddData, List<Integer> petActiveIdList) throws Exception;

}