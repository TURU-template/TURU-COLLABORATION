// src/main/java/com/turu/controllers/DataTidurApiController.java
package com.turu.controllers;

import com.turu.dto.SleepRecordRequest;
import com.turu.dto.StartSleepRequest;
import com.turu.dto.EndSleepRequest;
import com.turu.model.Analisis; // Asumsi Analisis sudah ada dan diperbarui
import com.turu.model.DataTidur;
import com.turu.model.Pengguna;
import com.turu.repository.PenggunaRepository;
import com.turu.service.DataTidurService;
import com.turu.service.PenggunaService; // Diperlukan untuk menghitung usia atau logic lain di service

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class DataTidurApiController {

    @Autowired
    private DataTidurService dataTidurService;

    @Autowired
    private PenggunaRepository penggunaRepository;

    @Autowired
    private PenggunaService penggunaService; // Pastikan ini di-autowire jika diperlukan di service
    
    // ZoneId zone = ZoneId.of("Asia/Jakarta"); // Tidak lagi dibutuhkan di controller karena di handle service

    // Helper untuk mendapatkan Pengguna
    private Optional<Pengguna> getPenggunaById(Integer userId) {
        return penggunaRepository.findById(userId);
    }

    // =========================================================================================
    // ROUTE UNTUK FLUTTER: /api/get-session-data-flutter/{userId}
    // =========================================================================================
    @GetMapping("/get-session-data-flutter/{userId}")
    public ResponseEntity<SessionDataFlutter> getSessionDataForFlutter(@PathVariable Integer userId) {
        Optional<Pengguna> optionalPengguna = getPenggunaById(userId);
        if (optionalPengguna.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new SessionDataFlutter(false, null, "Pengguna not found with ID: " + userId));
        }
        Pengguna pengguna = optionalPengguna.get();

        if (pengguna.isState()) {
            DataTidur ongoingSession = dataTidurService.cariTerbaruDataTidur(pengguna);
            if (ongoingSession != null) {
                SessionDataFlutter sessionData = new SessionDataFlutter(pengguna.isState(), ongoingSession.getWaktuMulai(), "Active session found.");
                return ResponseEntity.ok(sessionData);
            }
        }
        return ResponseEntity.ok(new SessionDataFlutter(pengguna.isState(), null, "No active session."));
    }

    public static class SessionDataFlutter {
        private boolean state;
        private LocalDateTime startTime;
        private String message;

        public SessionDataFlutter(boolean state, LocalDateTime startTime, String message) {
            this.state = state;
            this.startTime = startTime;
            this.message = message;
        }

        public boolean isState() { return state; }
        public LocalDateTime getStartTime() { return startTime; }
        public String getMessage() { return message; }
    }

    // =========================================================================================
    // MODIFIKASI LENGKAP ENDPOINT SLEEP ANALYSIS: /api/sleep-analysis/{userId}
    // =========================================================================================
    @GetMapping("/sleep-analysis/{userId}")
    public ResponseEntity<?> getSleepAnalysis(@PathVariable Integer userId) {
        Optional<Pengguna> optionalPengguna = getPenggunaById(userId);
        if (optionalPengguna.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Pengguna not found with ID: " + userId));
        }
        Pengguna pengguna = optionalPengguna.get();

        // Panggil metode analisisTidur yang telah diperbarui di DataTidurService
        Analisis analisisResult = dataTidurService.analisisTidur(userId, pengguna);

        // Periksa apakah hasil analisis menunjukkan data tidak cukup
        if (analisisResult.getMessage() != null && analisisResult.getMessage().contains("Not enough sleep data")) {
            return ResponseEntity.ok(Map.of(
                "message", analisisResult.getMessage(),
                "requiredData", analisisResult.getRequiredData(),
                "currentData", analisisResult.getCurrentData(),
                "suggestion", analisisResult.getSuggestion() // Saran dari service
            ));
        }

        // Jika data cukup, kembalikan hasil analisis lengkap
        // Pastikan Analisis model Anda memiliki getter untuk semua field ini
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("message", "Sleep analysis complete.");
        responseMap.put("averageSleepDurationSeconds", analisisResult.getRataWaktuBangun());
        responseMap.put("averageSleepDurationFormatted", String.format("%.1f Jam", analisisResult.getRataWaktuBangun() / 3600.0));
        responseMap.put("averageSleepScore", analisisResult.getRataSkorTidur());
        responseMap.put("overallSleepSpanSeconds", analisisResult.getRentangTidur() != null ? analisisResult.getRentangTidur().getSeconds() : 0L);
        responseMap.put("mascot", analisisResult.getMascot());
        responseMap.put("mascotName", analisisResult.getMascotName());
        responseMap.put("mascotDescription", analisisResult.getMascotDescription());
        responseMap.put("suggestion", analisisResult.getSuggestion());
        responseMap.put("averageSleepTime", analisisResult.getAverageSleepTime() != null ? analisisResult.getAverageSleepTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A");
        responseMap.put("averageWakeTime", analisisResult.getAverageWakeTime() != null ? analisisResult.getAverageWakeTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A");
        responseMap.put("sleepTimeVariabilityMinutes", analisisResult.getSleepTimeVariability() != null ? analisisResult.getSleepTimeVariability().toMinutes() : 0L);
        responseMap.put("wakeTimeVariabilityMinutes", analisisResult.getWakeTimeVariability() != null ? analisisResult.getWakeTimeVariability().toMinutes() : 0L); // Corrected here
        responseMap.put("isSleepTimeConsistent", analisisResult.getIsSleepTimeConsistent());
        responseMap.put("isWakeTimeConsistent", analisisResult.getIsWakeTimeConsistent());

        return ResponseEntity.ok(responseMap); // Return the HashMap
    }

    // private String formatDurationShort(Duration duration) { ... } // Hapus, sudah di service

    // =========================================================================================
    // Endpoint yang sudah ada lainnya (addManualSleepRecord, startSleepTracking, endSleepTracking,
    // getWeeklySleepStats, getLatestSleepRecord)
    // =========================================================================================

    @PostMapping("/sleep-records/manual")
    public ResponseEntity<?> addManualSleepRecord(@RequestBody SleepRecordRequest request) {
        if (request.getUserId() == null || request.getStartTime() == null || request.getEndTime() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID, start time, and end time are required."));
        }
        Optional<Pengguna> optionalPengguna = getPenggunaById(request.getUserId());
        if (optionalPengguna.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Pengguna not found with ID: " + request.getUserId()));
        }
        Pengguna pengguna = optionalPengguna.get();
        if (request.getStartTime().isAfter(request.getEndTime())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Start time cannot be after end time."));
        }
        try {
            dataTidurService.tambahManual(request.getStartTime(), request.getEndTime(), pengguna);
            DataTidur savedRecord = dataTidurService.cariTerbaruDataTidur(pengguna);
            if (savedRecord == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to retrieve saved record after manual addition."));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Manual sleep record added successfully",
                "id", savedRecord.getId(),
                "startTime", savedRecord.getWaktuMulai(),
                "endTime", savedRecord.getWaktuSelesai(),
                "duration", savedRecord.getDurasi().toString(),
                "score", savedRecord.getSkor(),
                "date", savedRecord.getTanggal()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to add manual sleep record: " + e.getMessage()));
        }
    }

    @PostMapping("/sleep/start")
    public ResponseEntity<?> startSleepTracking(@RequestBody StartSleepRequest request) {
        if (request.getUserId() == null || request.getStartTime() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID and start time are required."));
        }
        Optional<Pengguna> optionalPengguna = getPenggunaById(request.getUserId());
        if (optionalPengguna.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Pengguna not found with ID: " + request.getUserId()));
        }
        Pengguna pengguna = optionalPengguna.get();
        try {
            dataTidurService.addStart(request.getStartTime(), pengguna);
            pengguna.setState(true); // Update state pengguna
            penggunaRepository.save(pengguna); // Simpan perubahan state
            DataTidur newRecord = dataTidurService.cariTerbaruDataTidur(pengguna);
            if (newRecord == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to retrieve newly started record."));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Sleep tracking started",
                "id", newRecord.getId(),
                "startTime", newRecord.getWaktuMulai()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to start sleep tracking: " + e.getMessage()));
        }
    }

    @PostMapping("/sleep/end")
    public ResponseEntity<?> endSleepTracking(@RequestBody EndSleepRequest request) {
        if (request.getUserId() == null || request.getEndTime() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User ID and end time are required."));
        }
        Optional<Pengguna> optionalPengguna = getPenggunaById(request.getUserId());
        if (optionalPengguna.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Pengguna not found with ID: " + request.getUserId()));
        }
        Pengguna pengguna = optionalPengguna.get();
        try {
            boolean deletedBecauseTooShort = dataTidurService.addEnd(request.getEndTime(), pengguna);
            pengguna.setState(false); // Update state pengguna
            penggunaRepository.save(pengguna); // Simpan perubahan state
            if (deletedBecauseTooShort) {
                return ResponseEntity.ok(Map.of("message", "Sleep record was too short and deleted."));
            } else {
                DataTidur updatedRecord = dataTidurService.cariTerbaruDataTidur(pengguna);
                if (updatedRecord == null || updatedRecord.getWaktuSelesai() == null) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to retrieve updated record or end time not set."));
                }
                return ResponseEntity.ok(Map.of(
                    "message", "Sleep tracking ended",
                    "id", updatedRecord.getId(),
                    "startTime", updatedRecord.getWaktuMulai(),
                    "endTime", updatedRecord.getWaktuSelesai(),
                    "duration", updatedRecord.getDurasi().toString(),
                    "score", updatedRecord.getSkor()
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to end sleep tracking: " + e.getMessage()));
        }
    }

    @GetMapping("/sleep-stats/weekly/{userId}")
    public ResponseEntity<?> getWeeklySleepStats(@PathVariable Integer userId) {
        Optional<Pengguna> optionalPengguna = getPenggunaById(userId);
        if (optionalPengguna.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Pengguna not found with ID: " + userId));
        }
        Pengguna pengguna = optionalPengguna.get();

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);

        List<Object[]> scoresByDate = dataTidurService.getSkorByTanggalAndPengguna(userId);

        Map<LocalDate, Integer> scoreMap = scoresByDate.stream()
            .collect(Collectors.toMap(
                arr -> (LocalDate) arr[0],
                arr -> (Integer) arr[1]
            ));

        List<Integer> weeklyScores = new ArrayList<>();
        List<String> dayLabels = new ArrayList<>();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE");

        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            Integer score = scoreMap.getOrDefault(date, 0);
            weeklyScores.add(score);
            dayLabels.add(date.format(dayFormatter));
        }

        int todayIndex = today.getDayOfWeek().getValue() % 7;

        return ResponseEntity.ok(Map.of(
            "weeklyScores", weeklyScores,
            "dayLabels", dayLabels,
            "todayIndex", todayIndex
        ));
    }

    @GetMapping("/sleep-records/latest/{userId}")
    public ResponseEntity<?> getLatestSleepRecord(@PathVariable Integer userId) {
        Optional<Pengguna> optionalPengguna = getPenggunaById(userId);
        if (optionalPengguna.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Pengguna not found with ID: " + userId));
        }
        Pengguna pengguna = optionalPengguna.get();

        DataTidur latestRecord = dataTidurService.cariTerbaruDataTidur(pengguna);

        if (latestRecord == null) {
            return ResponseEntity.ok(Map.of("message", "No sleep records found for this user."));
        }

        return ResponseEntity.ok(Map.of(
            "id", latestRecord.getId(),
            "startTime", latestRecord.getWaktuMulai(),
            "endTime", latestRecord.getWaktuSelesai(),
            "duration", latestRecord.getDurasi().toString(),
            "score", latestRecord.getSkor(),
            "date", latestRecord.getTanggal()
        ));
    }
}