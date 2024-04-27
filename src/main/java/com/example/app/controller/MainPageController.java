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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.app.domain.ContactData;
import com.example.app.domain.InventoryData;
import com.example.app.domain.PetData;
import com.example.app.domain.User;
import com.example.app.mapper.ContactMapper;
import com.example.app.mapper.InventoryMapper;
import com.example.app.mapper.PetDataMapper;
import com.example.app.mapper.UserMapper;
import com.example.app.service.InventoryService;
import com.example.app.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainPageController {
	private final PetDataMapper petDataMapper;
	private final InventoryMapper inventoryMapper;
	private final InventoryService inventoryService;
	private final ContactMapper contactMapper;
	private final UserService userService;
	private final UserMapper userMapper;

	@GetMapping("/login")
	public String login(Model model, User user) throws Exception {
		model.addAttribute("user", user);

		return "Front/Login";
	}

	@PostMapping("/loginCheck")
	public String membership(HttpSession session, @Valid User user, Errors errors, BindingResult bindingResult)
			throws Exception {
		//ログイン記述
		if (bindingResult.hasErrors()) {
			System.out.println("hasErrors");
			return "redirect:/login";
		}

		if (!userService.isCorrectIdAndPassword(user.getUserLogin(), user.getUserPassword())) {
			bindingResult.rejectValue("userLogin", "error.incorrect_id_password");
			System.out.println("incorrect");
			return "redirect:/login";
		}
		String userLogin = user.getUserLogin();
		user = userMapper.selectUserByUserLogin(userLogin);
		user = userMapper.selectUserByUserLogin(user.getUserLogin());
		session.setAttribute("user", user);
		return "redirect:/main";

	}

	@GetMapping("/logout")
	public String logout(HttpSession session) throws Exception {
		session.invalidate();

		return "redirect:/login";
	}

	@GetMapping("/main")
	public String showPets(HttpSession session, @ModelAttribute InventoryData selectedInventoryData, @ModelAttribute InventoryData newInventoryData, Model model)
			throws Exception {
		User user = (User) session.getAttribute("user");
		//各種データ準備
		//ペットデータ
		List<PetData> petList = petDataMapper.showUserPetByUserId(user.getUserId());
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

			//0番が上書きされる為、後程、0を足す
			List<InventoryData> petInventory = inventoryMapper.showInventoryForPet(petId);
			petInventoryMap.put(petId, petInventory);
			List<ContactData> petContact = contactMapper.showContactForPet(petId);
			petContactMap.put(petId, petContact);
		}
		//配列に0番データを足す
		petInventoryMap.put(0, allInventoryList);
		petContactMap.put(0, allContactList);
		
		//データを渡す
		model.addAttribute("petList", petList);
		model.addAttribute("petIdList", petIdList);
		model.addAttribute("petInventoryMap", petInventoryMap);
		model.addAttribute("petContactMap", petContactMap);
		model.addAttribute("selectedInventoryData", selectedInventoryData);
		model.addAttribute("newInventoryData", newInventoryData);
		return "Front/Main";
	}

	@PostMapping("/addInventory")
	public String addInventory(@ModelAttribute InventoryData inventoryAddData, @RequestParam List<Integer> petIdList,
	        HttpSession session) throws Exception {
	    User user = (User) session.getAttribute("user");
	    Integer userId = user.getUserId();

	    inventoryAddData.setUserId(userId);
	    inventoryMapper.addInventory(inventoryAddData);
	    Integer inventoryId = inventoryAddData.getInventoryId();

	    System.out.println("inventory id:" + inventoryId);
	    for (Integer petId : petIdList) {
	        System.out.println(petId);
	        inventoryAddData.setPetId(petId);
	        inventoryMapper.addPetInventory(inventoryAddData);
	    }

	    return "redirect:/main";
	}

	@PostMapping("/updateInventory")
	public String updateInventory(@ModelAttribute InventoryData selectedInventoryData, Integer inventoryIdInput,
			@RequestParam List<Integer> petIdList,
			HttpSession session) throws Exception {
		User user = (User) session.getAttribute("user");
		Integer userId = user.getUserId();
		
		//checkが外れた場合、DBから論理削除する。
		//userIdに登録されているアイテムを検索（ユーザー間での重複対策）
		boolean flgUserIdExist = inventoryMapper.findPetInventoryByUserId(userId);
		if (flgUserIdExist = true) {
			for (Integer petId : petIdList) {
				boolean flgPetIdExist = inventoryMapper.findPetInventoryByPetId(petId);
				//pet_idにアイテムを検索。
				if(flgPetIdExist != true) {
					selectedInventoryData.setUserId(userId);
					selectedInventoryData.setPetId(petId);
					inventoryMapper.addPetInventory(selectedInventoryData);
				}
			}
		}
		//登録されている場合は、activate(0→1)する。
		inventoryMapper.updateInventory(selectedInventoryData.getInventoryId());
		return "redirect:/main";
	}

	//Ajaxにて選択されたインベントリのデータを取得
	@GetMapping("/getSelectedInventoryData")
	@ResponseBody
	public InventoryData selectedInventoryData(Integer selectedInventoryId) throws Exception {
	    InventoryData selectedInventoryData = inventoryMapper.getInventoryByInventoryId(selectedInventoryId);
	    return selectedInventoryData;
	}
	
	//Ajaxにてボタン押下後、pet_inventoryに記録されている且つ、
	//activeが１の場合、
	//html上のチェックボックスにチェックを入れる。
	@GetMapping("/getInventoryPetActiveList")
	@ResponseBody
	public Map<Integer, Boolean> inventoryPetActiveList(Integer selectedInventoryId) throws Exception {
	    List<InventoryData> inventoryPetCheckList  = inventoryMapper.inventoryPetCheckList(selectedInventoryId);
	    Map<Integer, Boolean> inventoryPetActiveList = new HashMap<>();
	    for(InventoryData activePet : inventoryPetCheckList) {
	        inventoryPetActiveList.put(activePet.getPetId(), true);
	    }
	    return inventoryPetActiveList;
	}
}