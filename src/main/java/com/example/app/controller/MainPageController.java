package com.example.app.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.app.domain.ContactData;
import com.example.app.domain.InventoryData;
import com.example.app.domain.PetData;
import com.example.app.domain.User;
import com.example.app.mapper.ContactMapper;
import com.example.app.mapper.InventoryMapper;
import com.example.app.mapper.PetDataMapper;
import com.example.app.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainPageController {
	private final PetDataMapper petDataMapper;
	private final InventoryMapper inventoryMapper;
	private final ContactMapper contactMapper;
	private final UserService userService;

	@PostMapping("/main")
	public String showPets(HttpSession session, @Valid User user, Error errors, BindingResult bindingResult, Model model) throws Exception {

		if(bindingResult.hasErrors()) {
			System.out.println("hasError");
			return "redirect:/login";
		}
		
		if(!userService.isCorrectIdAndPassword(user.getUserLogin(),user.getUserPassword())) {
			bindingResult.rejectValue("userLogin","error.incorrect_id_password");
			System.out.println("incorrect");
			return "redirect:/login";
		}
		session.setAttribute("userLogin", user.getUserLogin());
		
		
		List<PetData> petList = petDataMapper.showPets();
		Map<Integer, List<InventoryData>> petInventoryMap = new HashMap<>();
		Map<Integer, List<ContactData>> petContactMap = new HashMap<>();
		List<Integer> petIdList = new ArrayList<>();
		
		petIdList.add(0);
		List<InventoryData> allInventory = inventoryMapper.showInventory();
		petInventoryMap.put(0, allInventory);
		List<ContactData> allContact = contactMapper.showContact();
		petContactMap.put(0,allContact);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Date date = formatter.parse("2000/1/1");

		PetData allPetsData = new PetData(0, "All", 0, 0, date, "All", date, date, "none", 0, 0, 0);
		petList.add(0, allPetsData);
		
		for (PetData pet : petList) {
			int petId = pet.getPetId();
			petIdList.add(petId); // リストにpetIdを追加します
			
			List<InventoryData> petInventory = inventoryMapper.showInventoryForPet(petId);
			petInventoryMap.put(petId, petInventory);
			List<ContactData> petContact = contactMapper.showContactForPet(petId);
			petContactMap.put(petId, petContact);
		}

		model.addAttribute("petList", petList);
		model.addAttribute("petIdList", petIdList); 
		model.addAttribute("petInventoryMap", petInventoryMap);
		model.addAttribute("petContactMap", petContactMap);
		System.out.println(petList);
		System.out.println(petInventoryMap);
		System.out.println(petContactMap);
		return "Front/Main";
	}
	
	@GetMapping("/login")
	public String login(Model model, User user) throws Exception {
		model.addAttribute("user", user);
		
		return "Front/Login";
	}
}