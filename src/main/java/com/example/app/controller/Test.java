package com.example.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Test {
	Map<Integer, Boolean> testList = new HashMap<>();
	
	@GetMapping("/test")
	public String testing(Model model)throws Exception{
		testList.put(1, false);
		testList.put(2, false);
		testList.put(3, false);
		testList.put(4, false);
		testList.put(5, false);
		
		model.addAttribute("testList",testList);
		System.out.println(testList);
		return "Test/test";
	}
	@GetMapping("/change")
	List<Integer> checkboxList() throws Exception{
		return checkboxList();
	}
}
