package com.example.app.service;

import com.example.app.domain.InventoryData;

public interface InventoryService {
	Integer addToInventory(Integer userId, InventoryData inventoryAddData) throws Exception;

}