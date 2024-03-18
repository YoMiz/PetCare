package com.example.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.app.mapper.PetDataMapper;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MainPageController {
	private final PetDataMapper petDataMapper;
	@GetMapping("/")
	public String showPets(Model model) throws Exception {
		model.addAttribute("petList",petDataMapper.showPets());
		System.out.println(petDataMapper.showPets());
		
		return "Front/Main";
	}
}
