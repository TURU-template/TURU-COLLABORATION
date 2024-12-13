package com.turu.controllers;

import com.turu.model.Pengguna;
import com.turu.service.PenggunaService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class PenggunaController {

    private final PenggunaService penggunaService;

    public PenggunaController(PenggunaService penggunaService) {
        this.penggunaService = penggunaService;
    }

    @GetMapping("/akun")
    public String profile(Model model) {
        // Mendapatkan username pengguna yang login
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        // Cari data pengguna berdasarkan username
        Optional<Pengguna> pengguna = penggunaService.findByUsername(username);
        if (pengguna.isEmpty()) {
            throw new RuntimeException("Pengguna tidak ditemukan!");
        }
        model.addAttribute("pengguna", pengguna.get());
        return "profile"; // Mengarahkan ke file HTML profile.html
    }
    @GetMapping("/list")
    public String showPenggunaList(Model model) {
        model.addAttribute("penggunaList", penggunaService.getAllPengguna());
        return "pengguna-list";
    }

    
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("pengguna", new Pengguna());
        return "register";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/register")
    public String registerPengguna(@ModelAttribute Pengguna pengguna) {
        penggunaService.savePengguna(pengguna);
        return "redirect:/login";
    }
}
