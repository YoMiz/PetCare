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
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.app.domain.ContactData;
import com.example.app.domain.InventoryData;
import com.example.app.domain.PetData;
import com.example.app.domain.User;
import com.example.app.mapper.ContactMapper;
import com.example.app.mapper.InventoryMapper;
import com.example.app.mapper.PetDataMapper;
import com.example.app.mapper.UserMapper;
import com.example.app.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainPageController {
	private final UserMapper userMapper;
	private final PetDataMapper petDataMapper;
	private final InventoryMapper inventoryMapper;
	private final ContactMapper contactMapper;
	private final UserService userService;

	@PostMapping("/main")
	public String showPets(HttpSession session, @Valid User user, Errors errors, BindingResult bindingResult, Model model) throws Exception {
		//ログイン記述
		if(bindingResult.hasErrors()) {
			System.out.println("hasErrors");
			return "redirect:/login";
		}
		
		if(!userService.isCorrectIdAndPassword(user.getUserLogin(),user.getUserPassword())) {
			bindingResult.rejectValue("userLogin","error.incorrect_id_password");
			System.out.println("incorrect");
			return "redirect:/login";
		}
		user = userMapper.selectUserByUserLogin(user.getUserLogin());
		session.setAttribute("user", user);
		
		//各種データ準備
		//ペットデータ
		List<PetData> petList = petDataMapper.showPetByUserId(user.getUserId());
		List<Integer> petIdList = new ArrayList<>();
		//＊＊＊０番用ダミーデータ
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Date date = formatter.parse("2000/1/1");
		PetData allPetsData = new PetData(0, "All", 0, 0, date, "All.jpg", date, date, "none", 0, 0, 0);
		petList.add(0, allPetsData);
	
		//インベントリデータ準備
		Map<Integer, List<InventoryData>> petInventoryMap = new HashMap<>();
		List<InventoryData> allInventoryList = userMapper.selectInventoryByUserId(user.getUserId());

		//連絡先データ準備
		Map<Integer, List<ContactData>> petContactMap = new HashMap<>();
		List<ContactData> allContactList = userMapper.selectContactByUserId(user.getUserId());
		
		//各種データ取得
		for (PetData pet : petList) {
			int petId = pet.getPetId();
			petIdList.add(petId); // リストにpetIdを追加します
			
			//０番が上書きされる為、後程、０を足す
			List<InventoryData> petInventory = inventoryMapper.showInventoryForPet(petId);
			petInventoryMap.put(petId, petInventory);
			List<ContactData> petContact = contactMapper.showContactForPet(petId);
			petContactMap.put(petId, petContact);
		}
		//配列に０番データを足す
		petInventoryMap.put(0, allInventoryList);
		petContactMap.put(0,allContactList);

		//データを渡す
		model.addAttribute("petList", petList);
		model.addAttribute("petIdList", petIdList); 
		model.addAttribute("petInventoryMap", petInventoryMap);
		model.addAttribute("petContactMap", petContactMap);
		return "Front/Main";
	}
	
	@GetMapping("/login")
	public String login(Model model, User user) throws Exception {
		model.addAttribute("user", user);
		
		return "Front/Login";
	}
	@GetMapping("/logout")
	public String logout(HttpSession session) throws Exception{
		session.invalidate();
		
		return "redirect:/login";
	}
}