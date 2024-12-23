package com.turu.controllers;

import com.turu.model.Pengguna;
import com.turu.service.DataTidurService;
import com.turu.service.PenggunaService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Controller
public class PenggunaController {

    private final PenggunaService penggunaService;
    private final DataTidurService dataTidurService;

    public PenggunaController(PenggunaService penggunaService, DataTidurService dataTidurService) {
        this.penggunaService = penggunaService;
        this.dataTidurService = dataTidurService;
    }

    @GetMapping("/akun")
    public String profile(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();

        Optional<Pengguna> pengguna = penggunaService.findByUsername(username);
        if (pengguna.isEmpty()) {
            throw new RuntimeException("Pengguna tidak ditemukan!");
        }

        model.addAttribute("pengguna", pengguna.get());
        return "profile";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("pengguna", new Pengguna());
        model.addAttribute("errors", new HashMap<>());
        return "register";
    }

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Username atau password salah.");
        }
        return "login";
    }

    @PostMapping("/register")
    public String registerPengguna(@ModelAttribute Pengguna pengguna, Model model) {
        Map<String, String> errors = new HashMap<>();

        // Check for username duplication
        Optional<Pengguna> existingPengguna = penggunaService.findByUsername(pengguna.getUsername());
        if (existingPengguna.isPresent()) {
            errors.put("usernameError", "Username sudah dipakai. Silakan gunakan username lain");
        }

        // Validate username
        if (pengguna.getUsername() == null || pengguna.getUsername().trim().isEmpty()
                || pengguna.getUsername().contains(" ")) {
            errors.put("usernameError", "Username tidak boleh kosong dan tidak boleh mengandung spasi.");
        }

        // Validate password
        if (pengguna.getPassword() == null || pengguna.getPassword().length() < 4) {
            errors.put("passwordError", "Kata Sandi minimal 4 karakter.");
        }

        // Check for errors
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "register"; // Return to the register page with errors
        }

        try {
            penggunaService.savePengguna(pengguna);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }
    @GetMapping("/hapus-data-tidur")
    public String hapusDataTidur(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();

        Optional<Pengguna> pengguna = penggunaService.findByUsername(username);
        if (pengguna.isEmpty()) {
            throw new RuntimeException("Pengguna tidak ditemukan!");
        }

        // Hapus semua data tidur pengguna
        dataTidurService.hapusSemuaDataTidur(pengguna.get());
        model.addAttribute("message", "Semua data tidur berhasil dihapus.");
        return "redirect:/akun";
    }
}
