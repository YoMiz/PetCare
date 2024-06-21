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
		session.setAttribute("user", user);
		return "redirect:/main";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) throws Exception {
		session.invalidate();

		return "redirect:/login";
	}

	@GetMapping("/main")
	public String showPets(HttpSession session, @ModelAttribute InventoryData selectedInventoryData,
			@ModelAttribute InventoryData newInventoryData, Model model)
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
		Map<Integer, List<InventoryData>> inventoryPetMap = new HashMap<>();
		List<InventoryData> allInventoryList = userMapper.selectInventoryByUserId(user.getUserId());

		//連絡先データ準備
		Map<Integer, List<ContactData>> petContactMap = new HashMap<>();
		List<ContactData> allContactList = userMapper.selectContactByUserId(user.getUserId());

		//各種データ取得
		for (PetData pet : petList) {
			int petId = pet.getPetId();
			petIdList.add(petId); // リストにpetIdを追加します

			//0番が上書きされる為、後程、0を足す
			List<InventoryData> inventoryPet = inventoryMapper.showInventoryForPet(petId);
			inventoryPetMap.put(petId, inventoryPet);
			List<ContactData> petContact = contactMapper.showContactForPet(petId);
			petContactMap.put(petId, petContact);
		}
		//配列に0番データを足す
		inventoryPetMap.put(0, allInventoryList);
		petContactMap.put(0, allContactList);

		//データを渡す
		model.addAttribute("petList", petList);
		model.addAttribute("petIdList", petIdList);
		model.addAttribute("inventoryPetMap", inventoryPetMap);
		model.addAttribute("petContactMap", petContactMap);
		model.addAttribute("selectedInventoryData", selectedInventoryData);
		model.addAttribute("newInventoryData", newInventoryData);
		return "Front/Main";
	}

	@PostMapping("/addInventory")
	public String addInventory(@ModelAttribute InventoryData inventoryAddData,
			@RequestParam(required = false) List<Integer> inventoryPetActiveIdList,
			HttpSession session) throws Exception {
		User user = (User) session.getAttribute("user");
		Integer userId = user.getUserId();
		if (inventoryPetActiveIdList != null) {
			inventoryService.addInventory(userId, inventoryAddData, inventoryPetActiveIdList);
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
		boolean flgUserIdExist = inventoryMapper.findinventoryPetByUserId(userId);
		if (flgUserIdExist = true) {
			for (Integer petId : petIdList) {
				boolean flgPetIdExist = inventoryMapper.findinventoryPetByPetId(petId);
				//pet_idにアイテムを検索。
				if (flgPetIdExist != true) {
					selectedInventoryData.setUserId(userId);
					selectedInventoryData.setPetId(petId);
					inventoryMapper.addInventoryPet(selectedInventoryData);
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
	@GetMapping("/getInventoryPetActiveList")
	@ResponseBody
	public Map<Integer, Integer> getActivePetsForUser(Integer selectedInventoryId, HttpSession session, Model model) throws Exception {
	    // 選択されたインベントリアイテムに関連する全てのペットを取得
	    List<Integer> selectedInventoryPetList = inventoryMapper.showPetByInventoryId(selectedInventoryId);
	    Map<Integer, Integer> inventoryPetActiveList = new HashMap<>();
	    int inventoryPetActiveStatus = 0;
	    for(Integer petId : selectedInventoryPetList) {
	        // ここでペットIDに対するインベントリデータが複数存在する可能性があるため、リストで結果を受け取ります。
	        List<InventoryData> inventoryDataList = inventoryMapper.getInventoryByPetId(petId);
	        // アクティブなペットのみをマップに追加します。
	        for(InventoryData inventoryData : inventoryDataList) {
	            inventoryPetActiveStatus = inventoryData.getInventoryPetStatus();
	            if(inventoryPetActiveStatus == 1) {
	                inventoryPetActiveList.put(inventoryData.getPetId(), 1);
	                break; // 同じペットIDに対して複数のアクティブなインベントリデータがないと仮定します。
	            }
	        }
	    }
	    return inventoryPetActiveList;
	}

	/*
		//Ajaxにてボタン押下後、pet_inventoryに記録されている且つ、
		//activeが１の場合、
		//html上のチェックボックスにチェックを入れる。
		@GetMapping("/getInventoryPetActiveList")
		@ResponseBody
		public Map<Integer, Integer> getActivePetsForUser(Integer selectedInventoryId, HttpSession session, Model model) throws Exception {
			User user = (User)session.getAttribute("user");
			
			//Userに纏わる全てのペットを取得
		    List<PetData> userAllPetList = new ArrayList<PetData>();
		    userAllPetList = userMapper.selectPetByUserId(user.getUserId());
		    List<Integer> userAllPetIdList = new ArrayList<Integer>();
		    Integer petId;
		    //Userに纏わる全てのペットIDのみを抽出しList化
		    for(PetData petData : userAllPetList) {
		    	petId = petData.getPetId();
		    	userAllPetIdList.add(petId);
		    }
		    //ペットIDより、インベントリのデータを付与する
		    List<InventoryData> inventoryPetDataList = new ArrayList<InventoryData>();
		    for(Integer userPetId : userAllPetIdList) {
		    	InventoryData inventoryPetData = (InventoryData)inventoryMapper.getInventoryByPetId(userPetId);
		    	inventoryPetDataList.add(inventoryPetData);
		    }
		    
		    Map<Integer, Integer> inventoryPetActiveList = new HashMap<>();
		    int inventoryPetActiveStatus = 0;
		    for(InventoryData inventoryData : inventoryPetDataList) {
		    	inventoryPetActiveStatus = inventoryData.getInventoryPetStatus();
		    	if(inventoryPetActiveStatus == 1) {
		    		inventoryPetActiveList.put(inventoryData.getPetId(), 1);
		    	}
		    }
		    return inventoryPetActiveList;
		}*/
}