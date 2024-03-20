package com.example.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.app.domain.InventoryData;
import com.example.app.domain.PetData;
import com.example.app.mapper.InventoryMapper;
import com.example.app.mapper.PetDataMapper;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainPageController {
	private final PetDataMapper petDataMapper;
	private final InventoryMapper inventoryMapper;
	@GetMapping("/")
	public String showPets(Model model) throws Exception {
		List<PetData> petList = petDataMapper.showPets();
		Map<Integer, List<InventoryData>> petInventoryMap = new HashMap<>();
		for(PetData pet : petList) {
			int petId = pet.getPetId();
			List<InventoryData> petInventory = inventoryMapper.showInventoryForPet(petId);
			petInventoryMap.put(petId, petInventory);
		}
		
		model.addAttribute("petList", petList);
		model.addAttribute("petInventoryMap", petInventoryMap);
		
		return "Front/Main";
	}
}

