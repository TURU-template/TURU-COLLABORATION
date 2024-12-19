package com.turu.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.*;
import java.util.List;
import com.turu.service.DataTidurService;
import com.turu.model.DataTidur;
import com.turu.model.Pengguna;
import com.turu.repository.PenggunaRepository;
import com.turu.service.PenggunaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;

@Controller
public class BerandaController {
    // Define the button's state
    private String state = "tidur"; // Initial state (tidur, tidur-active, bangun)

    
    private PenggunaService penggunaService;
    private DataTidurService dataTidurService;
    private PenggunaRepository penggunaRepository;
    
    @Autowired
    public BerandaController(DataTidurService dataTidurService, PenggunaService penggunaService, PenggunaRepository penggunaRepository) {
        this.dataTidurService = dataTidurService;
        this.penggunaService = penggunaService;
        this.penggunaRepository = penggunaRepository;
    }
    @ModelAttribute("pengguna")
    public Pengguna getLoggedInPengguna() {
        
        // Retrieve the authentication object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
        // Check if the authentication is not null and the user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated!");
        }
    
        // Get the username from the principal object
        Object principal = authentication.getPrincipal();
        String username = (principal instanceof UserDetails)
                ? ((UserDetails) principal).getUsername()
                : principal.toString();
    
        // Find the user by username and return it, or throw exception if not found
        return penggunaService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Pengguna tidak ditemukan!"));
    }

    @GetMapping("/beranda")
    public String beranda(Model model) {
        // Pass the current state to the frontend
        model.addAttribute("buttonState", state);
        model.addAttribute("buttonLabel", getLabelForState(state));
        model.addAttribute("buttonClass", getClassForState(state));
        model.addAttribute("buttonIcon", getIconForState(state));
        model.addAttribute("state", getLoggedInPengguna().isState());


        LocalDate today = LocalDate.now();


        List<DataTidur> sleepData = dataTidurService.getDataTidurMingguan(today.minusDays(6), today);

        
        int[] sleepScores = new int[7]; // Default 0
        String[] dayLabels = {"S", "S", "R", "K", "J", "S", "M"};

       
        for (DataTidur data : sleepData) {
            int dayIndex = today.minusDays(6).until(data.getTanggal()).getDays();
            if (dayIndex >= 0 && dayIndex < 7) {
                sleepScores[dayIndex] = data.getSkor();
            }
        }

    
        model.addAttribute("sleepScores", sleepScores);
        model.addAttribute("dayLabels", dayLabels);

        return "beranda"; // Thymeleaf template
    }
    

    @GetMapping("/tips")
    public String tipsPage() {
        return "tips"; // Mengarah ke file templates/tips.html
    }

    @PostMapping("/beranda/toggle-button")
    public String toggleButton() throws InterruptedException {
        // Transition logic for the button
        switch (state) {
            case "tidur":
                state = "tidur-active";
                Thread.sleep(3000); // Simulate delay
                state = "bangun";
                break;
            case "bangun":
                state = "tidur";
                break;
        }
        return "redirect:/beranda"; // Redirect to refresh the page
    }

    // Helper methods for button attributes
    private String getLabelForState(String state) {
        switch (state) {
            case "tidur":
                return "Tombol Tidur";
            case "tidur-active":
                return "Tombol Bangun";
            case "bangun":
                return "Tombol Tidur";
            default:
                return "";
        }
    }

    private String getClassForState(String state) {
        switch (state) {
            case "tidur":
                return "lilac";
            case "tidur-active":
                return "purple";
            case "bangun":
                return "purple";
            default:
                return "";
        }
    }

    private String getIconForState(String state) {
        switch (state) {
            case "tidur":
                return "bi-moon-fill";
            case "tidur-active":
                return "bi-moon-stars-fill";
            case "bangun":
                return "bi-sunrise-fill";
            default:
                return "";
        }
    }
    @PostMapping("/api/add-start")
    @ResponseBody
    public ResponseEntity<String> addStart(@RequestBody LocalDateTime startTime) {
        Pengguna pengguna = getLoggedInPengguna();
        dataTidurService.addStart(startTime, pengguna);
        pengguna.setState(true);
        penggunaRepository.save(pengguna);
        return ResponseEntity.ok("Start time added successfully!");
    }

    @PostMapping("/api/add-end")
    @ResponseBody
    public ResponseEntity<String> addEnd(@RequestBody AddEndRequest request) {
        Pengguna pengguna = getLoggedInPengguna();
        dataTidurService.addEnd(request.getEndTime(), pengguna);
        pengguna.setState(false);
        penggunaRepository.save(pengguna);
        return ResponseEntity.ok("End time and duration calculated successfully!");
    }
    // DTO for addEnd
    public static class AddEndRequest {
        private LocalDateTime endTime;

        public LocalDateTime getEndTime() {
            return endTime;
        }

        public void setEndTime(LocalDateTime endTime) {
            this.endTime = endTime;
        }
    }
}
