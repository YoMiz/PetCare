package com.example.app.service;

import org.springframework.stereotype.Service;

import com.example.app.domain.InventoryData;
import com.example.app.mapper.InventoryMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
	private final InventoryMapper inventoryMapper;

	@Override
	public void addToInventory(Integer userId, InventoryData inventoryAddData) throws Exception {
	    inventoryAddData.setUserId(userId);
	    inventoryMapper.addInventory(inventoryAddData);
	    inventoryMapper.addPetInventory(inventoryAddData);
	}
}