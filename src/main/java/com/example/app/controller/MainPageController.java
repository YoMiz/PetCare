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
	public String showPets(HttpSession session, @ModelAttribute InventoryData selectedInventoryData, Model model)
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
		return "Front/Main";
	}

	@PostMapping("/addInventory")
	public String addInventory(@ModelAttribute InventoryData inventoryAddData, HttpSession session) throws Exception {
		User user = (User) session.getAttribute("user");
		Integer userId = user.getUserId();
		inventoryService.addToInventory(userId, inventoryAddData);

		return "redirect:/main";
	}

	@PostMapping("/updateInventory")
	public String updateInventory(@ModelAttribute InventoryData inventoryUpdateData, Integer inventoryIdInput,
			@RequestParam List<Integer> petIdList,
			HttpSession session) throws Exception {
		User user = (User) session.getAttribute("user");
		Integer userId = user.getUserId();
		
		//checkが外れた場合、DBから論理削除する。
		//仕組みの概要：
		//userIdに登録されているアイテムを検索（ユーザー間での重複対策）
		//pet_idにアイテムを検索。登録されていない場合は新規登録
		//登録されている場合は、activate(0→1)する。
		boolean flgUserIdExist = inventoryMapper.findPetInventoryByUserId(userId);
		if (flgUserIdExist = true) {
			for (Integer petId : petIdList) {
				boolean flgPetIdExist = inventoryMapper.findPetInventoryByPetId(petId);
				if(flgPetIdExist != true) {
					inventoryUpdateData.setUserId(userId);
					inventoryUpdateData.setPetId(petId);
					inventoryMapper.addPetInventory(inventoryUpdateData);
				}
			}
		}
		inventoryMapper.updateInventory(inventoryUpdateData);

		return "redirect:/main";
	}
	//更新ボタン押下後、pet_inventoryに記録されている且つ、
	//activeが１の場合、
	//html上のチェックボックスにチェックを入れる。
	@GetMapping("/petInventoryChecker")
	public List<InventoryData> checkInventoryPetList(InventoryData inventoryUpdateData) throws Exception {
	    List<InventoryData> inventoryPetCheckList = inventoryMapper.inventoryPetCheckList(inventoryUpdateData.getInventoryId());
	    System.out.println(inventoryPetCheckList);
	    return inventoryPetCheckList;
	}
	@GetMapping("/getSelectedInventoryData")
	@ResponseBody
	public InventoryData selectedInventoryData(Integer selectedInventoryId) throws Exception {
	    InventoryData selectedInventoryData = inventoryMapper.getInventoryByInventoryId(selectedInventoryId);
	    return selectedInventoryData;
	}

	@GetMapping("/getInventoryPetActiveList")
	@ResponseBody
	public Map<Integer, Boolean> inventoryPetActiveList(Integer selectedInventoryId) throws Exception {
	    List<InventoryData> inventoryPetCheckList  = inventoryMapper.inventoryPetCheckList(selectedInventoryId);
	    Map<Integer, Boolean> inventoryPetActiveList = new HashMap<>();
	    for(InventoryData activePet : inventoryPetCheckList) {
	        inventoryPetActiveList.put(activePet.getPetId(), true);
	    }
	    System.out.println(inventoryPetActiveList);
	    return inventoryPetActiveList;
	}
}