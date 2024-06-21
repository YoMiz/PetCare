package com.example.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.app.domain.InventoryData;
import com.example.app.domain.PetData;
import com.example.app.mapper.InventoryMapper;
import com.example.app.mapper.PetDataMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
	private final InventoryMapper inventoryMapper;
	private final PetDataMapper petDataMapper;

	@Override
	public void addInventory(Integer userId, InventoryData inventoryAddData, List<Integer> inventoryPetActiveList)
			throws Exception {
		inventoryAddData.setUserId(userId);
		inventoryMapper.addInventory(inventoryAddData);
		List<PetData> allPetIdList = petDataMapper.showUserPetByUserId(userId);
		for (PetData petData : allPetIdList) {
			inventoryAddData.setPetId(petData.getPetId());
			inventoryAddData.setInventoryPetStatus(0);
			inventoryMapper.addInventoryPet(inventoryAddData);
		}
		Integer inventoryPetStatus;
		System.out.println("inventoryPetActiveList:" + inventoryPetActiveList);
		for (Integer inventoryPetActive : inventoryPetActiveList) {
			if (inventoryPetActive != null) {
				inventoryPetStatus = 1;
			} else {
				inventoryPetStatus = 0;
			}
			inventoryAddData.setInventoryPetStatus(inventoryPetStatus);
			inventoryAddData.setPetId(inventoryPetActive);
			inventoryMapper.updateInventoryPetByInventoryIdAndPetId(inventoryAddData);
		}

	}
}