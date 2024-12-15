package com.turu.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NavigationController {		
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
	
	// @GetMapping("/beranda")
	// public String displayPageBeranda(Model model) {
	// 	model.addAttribute("sapa", "Selamat malam, ");
	// 	model.addAttribute("nama", "Iqbal ganteng");
	// 	return "beranda.html"; //ini harus keluar, terus buka di folder Static
	// }
	
	@GetMapping("/radio")
	public String displayPageRadio(Model model) {
		model.addAttribute("sapa", "Selamat malam, ");
		model.addAttribute("nama", "Iqbal ganteng");
		return "radio.html"; //ini harus keluar, terus buka di folder Static
	}

	
	
	@GetMapping("/anggota")
	@ResponseBody
	public String displayAnggota() {
		return "Kelompok IMPAL x PBO";
	}

	
    @GetMapping("/lama-waktu-tidur")
    public String lamaWaktuTidurPage() {
        return "lama-waktu-tidur"; // Mengarah ke lama-waktu-tidur.html
    }
}
	