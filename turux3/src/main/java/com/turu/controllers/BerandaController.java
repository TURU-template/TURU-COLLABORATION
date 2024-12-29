package com.turu.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BerandaController {
    // Define the button's state
    private String state = "tidur"; // Initial state (tidur, tidur-active, bangun)

    private PenggunaService penggunaService;
    private DataTidurService dataTidurService;
    private PenggunaRepository penggunaRepository;
    ZoneId zone = ZoneId.of("Asia/Jakarta");
    @Autowired
    public BerandaController(DataTidurService dataTidurService, PenggunaService penggunaService,
            PenggunaRepository penggunaRepository) {
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
        Pengguna pengguna = getLoggedInPengguna();
        boolean isSleeping = pengguna.isState();
        model.addAttribute("state", isSleeping);
        DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        DateTimeFormatter formatter4 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (isSleeping) {
            model.addAttribute("buttonLabel", "Tombol Bangun");
            DataTidur ongoingSession = dataTidurService.cariTerbaruDataTidur(pengguna);
            if (ongoingSession != null) {
                model.addAttribute("startTime", ongoingSession.getWaktuMulai());
            }
        } else {
            model.addAttribute("buttonLabel", "Tombol Tidur");
        }

        DataTidur latestSleep = dataTidurService.cariTerbaruDataTidur(pengguna);
        model.addAttribute("dataTidur", latestSleep);
        if (latestSleep != null && latestSleep.getWaktuSelesai() != null) {
            // Calculate age
            int age = Period.between(pengguna.getTanggalLahir(), ZonedDateTime.now(zone).toLocalDate()).getYears();

            // Determine min and max hours based on age
            double minHours;
            double maxHours;

            if (age <= 17) {
                minHours = 8;
                maxHours = 10;
            } else if (age >= 18 && age <= 64) {
                minHours = 7;
                maxHours = 9;
            } else {
                minHours = 7;
                maxHours = 8;
            }

            // Get sleep duration in hours using the existing durasi field
            LocalTime durasi = latestSleep.getDurasi();
            double sleepHours = durasi.getHour() + (durasi.getMinute() / 60.0);
            
            // Pass data tidur ke html
            String waktu = "........";
            model.addAttribute("waktu", waktu);
            model.addAttribute("durasi", "----");
                if (latestSleep != null && latestSleep.getWaktuSelesai() == null){
                    model.addAttribute("waktuMulaiFormatted", latestSleep.getWaktuMulai().format(formatter3));
                    waktu = latestSleep.getWaktuMulai().format(formatter4) + " —  ...";
                    model.addAttribute("waktu", waktu);
                } else if (latestSleep != null && latestSleep.getWaktuSelesai() != null) {
                    model.addAttribute("waktuMulaiFormatted", latestSleep.getWaktuMulai().format(formatter3));
                    model.addAttribute("waktuSelesaiFormatted", latestSleep.getWaktuSelesai().format(formatter3));
                    waktu = latestSleep.getWaktuMulai().format(formatter4) + " — " + latestSleep.getWaktuSelesai().format(formatter4); 
                    model.addAttribute("waktu", waktu);
                    String durasi2 = durasi.getHour() + " j " + durasi.getMinute() + " m";
                    model.addAttribute("durasi", durasi2);
                }
            
            // Get start time hour for owl condition
            int startHour = latestSleep.getWaktuMulai().getHour();

            String mascot;
            String mascotName;
            String mascotDescription;

            // Determine mascot based on conditions
            if (startHour >= 1 && startHour <= 13) {
                mascot = "🦉";
                mascotName = "Burung Hantu";
                mascotDescription = String.format("Anda telah tidur %.1f jam, tetapi tidur anda terbalik", sleepHours);
            } else if (sleepHours >= minHours && sleepHours <= maxHours) {
                mascot = "🦁";
                mascotName = "Singa";
                mascotDescription = "Anda telah tidur sesuai anjuran. Tidur Anda ideal";
            } else if (sleepHours > maxHours) {
                mascot = "🐨";
                mascotName = "Koala Pemalas";
                mascotDescription = "Anda telah tidur melebihi anjuran, tidur Anda berlebihan";
            } else {
                mascot = "🦈";
                mascotName = "Hiu";
                mascotDescription = "Anda telah tidur di bawah anjuran, tidur Anda kurang";
            }

            // Add mascot and sleep data attributes to model
            model.addAttribute("mascot", mascot);
            model.addAttribute("mascotName", mascotName);
            model.addAttribute("mascotDescription", mascotDescription);
            model.addAttribute("sleepScore", latestSleep.getSkor());
        } else {
            // Default values if no sleep data is available
            model.addAttribute("mascot", "😴");
            model.addAttribute("mascotName", "Belum ada data");
            model.addAttribute("mascotDescription", "Silakan klik Tombol Tidur untuk mulai memonitoring tidur Anda");
            model.addAttribute("sleepScore", "-");
        }
        // STATISTIK
        LocalDate today = ZonedDateTime.now(zone).toLocalDate();
        LocalDate startDate = today.minusDays(6);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd MMMM yyyy");

        String[] fullDayLabels = new String[7];
        String[] dateLabels = new String[7];
        String[] dayLabels = new String[7];

        LocalDate currentDate = startDate;
        for (int i = 0; i < 7; i++) {
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            fullDayLabels[i] = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("id")); // Full day in
            // Indonesian

            // Dynamic short day labels based on requirements
            switch (dayOfWeek) {
                case SUNDAY -> dayLabels[i] = "Mg";
                case MONDAY -> dayLabels[i] = "Sn";
                case TUESDAY -> dayLabels[i] = "Sl";
                case WEDNESDAY -> dayLabels[i] = "Rb";
                case THURSDAY -> dayLabels[i] = "Km";
                case FRIDAY -> dayLabels[i] = "Jm";
                case SATURDAY -> dayLabels[i] = "Sb";
            }

            dateLabels[i] = currentDate.format(formatter);
            currentDate = currentDate.plusDays(1);
        }

        // Fetch user-specific sleep data
        List<DataTidur> sleepData = dataTidurService.getDataTidurMingguan(startDate, today, pengguna);

        int[] sleepScores = new int[7]; // Default scores as 0
        for (DataTidur data : sleepData) {
            int dayIndex = (int) ChronoUnit.DAYS.between(startDate, data.getTanggal());
            if (dayIndex >= 0 && dayIndex < 7) {
                sleepScores[dayIndex] = data.getSkor();
            }
        }

        // Add attributes to the model
        model.addAttribute("dateRange", startDate.format(formatter2) + " - Hari Ini");
        model.addAttribute("sleepScores", sleepScores);
        model.addAttribute("dayLabels", dayLabels);
        model.addAttribute("fullDayLabels", fullDayLabels);
        model.addAttribute("dateLabels", dateLabels);

        return "beranda";
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
    public ResponseEntity<Map<String, Object>> addEnd(@RequestBody AddEndRequest request) {
        Map<String, Object> response = new HashMap<>();
        Pengguna pengguna = getLoggedInPengguna();
        boolean isDeleted = false;
        DataTidur dt = dataTidurService.cariTerbaruDataTidur(pengguna);
        
        isDeleted = dataTidurService.addEnd(request.getEndTime(), pengguna);
        
        pengguna.setState(false);
        penggunaRepository.save(pengguna);
        response.put("message", "End time and duration calculated successfully!");
        response.put("isDeleted", isDeleted);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/get-session-data")
    @ResponseBody
    public ResponseEntity<SessionData> getSessionData() {
        Pengguna pengguna = getLoggedInPengguna();

        if (pengguna.isState()) {
            DataTidur ongoingSession = dataTidurService.cariTerbaruDataTidur(pengguna);
            if (ongoingSession != null) {
                SessionData sessionData = new SessionData(pengguna.isState(), ongoingSession.getWaktuMulai());
                return ResponseEntity.ok(sessionData);
            }
        }
        return ResponseEntity.ok(new SessionData(pengguna.isState(), null));
    }

    public static class SessionData {
        private boolean state;
        private LocalDateTime startTime;

        public SessionData(boolean state, LocalDateTime startTime) {
            this.state = state;
            this.startTime = startTime;
        }

        public boolean isState() {
            return state;
        }

        public LocalDateTime getStartTime() {
            return startTime;
        }
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
    @PostMapping("/tambahTidur")
    public String tambahTidur(@ModelAttribute DataTidur dataTidur, RedirectAttributes redirectAttributes, Model model){
        Pengguna pengguna = getLoggedInPengguna();
        String message;
        if (dataTidurService.cekDuplikatDataTidur(dataTidur, pengguna)){
            message = "Anda telah memiliki data tidur pada tanggal " + dataTidur.getWaktuSelesai().toLocalDate();
            redirectAttributes.addFlashAttribute("message", message);
            redirectAttributes.addFlashAttribute("openModal", true);
            redirectAttributes.addFlashAttribute("dataTidur", dataTidur);
            return "redirect:/beranda"; // Redirect to refresh the page
        } else {
            dataTidurService.tambah(dataTidur, pengguna);
            message = "Data tidur berhasil ditambahkan";
            redirectAttributes.addFlashAttribute("message", message);
            redirectAttributes.addFlashAttribute("openModal", false);
            return "redirect:/beranda"; // Redirect to refresh the page
        }

    }
    @PostMapping("/editTidur")
    public String editTidur(@RequestParam("waktuMulai") String waktuMulai, @RequestParam("waktuSelesai") String waktuSelesai){
        DataTidur dataTidur = dataTidurService.cariTerbaruDataTidur(getLoggedInPengguna());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime mulai = LocalDateTime.parse(waktuMulai, formatter);
        LocalDateTime selesai = LocalDateTime.parse(waktuSelesai, formatter);
        dataTidur.setWaktuMulai(mulai);
        dataTidur.setWaktuSelesai(selesai);
        dataTidurService.update(dataTidur, getLoggedInPengguna());
        return  "redirect:/beranda";
    }

}
