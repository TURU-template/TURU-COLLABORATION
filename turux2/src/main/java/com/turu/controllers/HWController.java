package com.turu.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HWController {		
	@GetMapping("/")
	public String displayPageDefault(Model model) {
		model.addAttribute("sapa", "Selamat malam, ");
		model.addAttribute("nama", "Default");
		return "beranda.html"; //ini harus keluar, terus buka di folder Static
	}
	
	@GetMapping("/index")
	public String displayPageIndex(Model model) {
		model.addAttribute("sapa", "Selamat malam, ");
		model.addAttribute("nama", "Iqbal ganteng tp kamu akses lewat index ya");
		return "beranda.html"; //ini harus keluar, terus buka di folder Static
	}
	
	@GetMapping("/beranda")
	public String displayPageBeranda(Model model) {
		model.addAttribute("sapa", "Selamat malam, ");
		model.addAttribute("nama", "Iqbal ganteng");
		return "beranda.html"; //ini harus keluar, terus buka di folder Static
	}
	
	@GetMapping("/radio")
	public String displayPageRadio(Model model) {
		model.addAttribute("sapa", "Selamat malam, ");
		model.addAttribute("nama", "Iqbal ganteng");
		return "radio.html"; //ini harus keluar, terus buka di folder Static
	}
	
	
	@GetMapping("/akun")
	public String displayPageAku() {
		return "profile.html"; //ini aksesnya langsung dr folder Templates
	}
	
	
	@GetMapping("/anggota")
	@ResponseBody
	public String displayAnggota() {
		return "Kelompok IMPAL x PBO";
	}
}
	