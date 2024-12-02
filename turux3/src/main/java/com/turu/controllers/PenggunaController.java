package com.turu.controllers;

import com.turu.model.Pengguna;
import com.turu.service.PenggunaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PenggunaController {

    private final PenggunaService penggunaService;

    public PenggunaController(PenggunaService penggunaService) {
        this.penggunaService = penggunaService;
    }

    @GetMapping("/list")
    public String showPenggunaList(Model model) {
        model.addAttribute("penggunaList", penggunaService.getAllPengguna());
        return "pengguna-list";
    }

    
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("pengguna", new Pengguna());
        return "register-pengguna";
    }

    @PostMapping("/register")
    public String registerPengguna(@ModelAttribute Pengguna pengguna) {
        penggunaService.savePengguna(pengguna);
        return "redirect:/";
    }
}
