package com.example.app.service;

import com.example.app.domain.InventoryData;

public interface InventoryService {
	void addToInventory(Integer userId, InventoryData inventoryAddData) throws Exception;
}
