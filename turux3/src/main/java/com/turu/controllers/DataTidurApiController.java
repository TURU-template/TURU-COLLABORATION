// src/main/java/com/turu/controllers/DataTidurApiController.java
package com.turu.controllers;

import com.turu.dto.SleepRecordRequest;
import com.turu.dto.StartSleepRequest;
import com.turu.dto.EndSleepRequest;
import com.turu.model.Analisis;
import com.turu.model.DataTidur;
import com.turu.model.Pengguna;
import com.turu.repository.DataTidurRepository;
import com.turu.repository.PenggunaRepository;
import com.turu.service.DataTidurService;
import com.turu.service.PenggunaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
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
    private PenggunaService penggunaService; 
    
    @Autowired
    private DataTidurRepository dataTidurRepository;
    
    // Helper method to get Pengguna by ID safely
    private Optional<Pengguna> getPenggunaById(Integer userId) {
        return penggunaRepository.findById(userId);
    }

    // DTO for main mascot status response, including sleepScore
    public static class MainMascotStatus {
        private String mascot;
        private String mascotName;
        private String mascotDescription;
        private int sleepScore;

        public MainMascotStatus(String mascot, String mascotName, String mascotDescription, int sleepScore) {
            this.mascot = mascot;
            this.mascotName = mascotName;
            this.mascotDescription = mascotDescription;
            this.sleepScore = sleepScore;
        }

        // Getters
        public String getMascot() { return mascot; }
        public String getMascotName() { return mascotName; }
        public String getMascotDescription() { return mascotDescription; }
        public int getSleepScore() { return sleepScore; }

        // Setters
        public void setMascot(String mascot) { this.mascot = mascot; }
        public void setMascotName(String mascotName) { this.mascotName = mascotName; }
        public void setMascotDescription(String mascotDescription) { this.mascotDescription = mascotDescription; }
        public void setSleepScore(int sleepScore) { this.sleepScore = sleepScore; }
    }

    // Helper method untuk menentukan maskot berdasarkan logika yang diberikan
    // Parameter ketiga sekarang adalah LocalTime untuk durasi
    private String determineMascotEmoji(int age, LocalDateTime startTime, LocalTime durationTime, double minHours, double maxHours) {
        if (startTime == null || durationTime == null) {
            return "❓"; // Maskot default jika data tidak lengkap
        }

        int startHour = startTime.getHour();
        // Mengonversi LocalTime menjadi total jam tidur (double)
        double sleepHours = durationTime.getHour() + (durationTime.getMinute() / 60.0) + (durationTime.getSecond() / 3600.0);

        if (startHour >= 1 && startHour <= 13) {
            return "🦉";
        } else if (sleepHours >= minHours && sleepHours <= maxHours) {
            return "🦁";
        } else if (sleepHours > maxHours) {
            return "🐨";
        } else {
            return "🦈";
        }
    }

    // Helper method untuk mendapatkan nama maskot
    private String determineMascotName(String emoji) {
        switch (emoji) {
            case "🦉": return "Burung Hantu Malam";
            case "🦁": return "Singa Prima";
            case "🐨": return "Koala Pemalas";
            case "🦈": return "Hiu Agresif";
            default: return "Tidak Diketahui";
        }
    }
    
    // Helper method untuk mendapatkan deskripsi maskot
    private String determineMascotDescription(String emoji, double sleepHours) {
        switch (emoji) {
            case "🦉": return String.format("Anda telah tidur %.1f jam, tetapi tidur anda terbalik", sleepHours);
            case "🦁": return "Anda telah tidur sesuai anjuran. Tidur Anda ideal";
            case "🐨": return "Anda telah tidur melebihi anjuran, tidur Anda berlebihan";
            case "🦈": return "Anda telah tidur di bawah anjuran, tidur Anda kurang";
            default: return "Informasi lebih lanjut tidak tersedia.";
        }
    }

    @GetMapping("/main-mascot-status/{userId}")
    public ResponseEntity<?> getMainMascotStatus(@PathVariable Integer userId) {

        Optional<Pengguna> optionalPengguna = getPenggunaById(userId);
        if (optionalPengguna.isEmpty()) {
            System.err.println("DEBUG_BACKEND: Pengguna with ID " + userId + " not found for mascot status.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Pengguna tidak ditemukan dengan ID: " + userId));
        }
        Pengguna pengguna = optionalPengguna.get();

        LocalDate userDateOfBirth = pengguna.getTanggalLahir();
        if (userDateOfBirth == null) {
            System.err.println("DEBUG_BACKEND: Tanggal lahir not available for user ID " + userId + ".");
            return ResponseEntity.badRequest().body(Map.of("error", "Tanggal lahir pengguna tidak tersedia."));
        }

        int age = Period.between(userDateOfBirth, LocalDate.now()).getYears();
        System.out.println("DEBUG_BACKEND: User ID: " + userId + ", Calculated Age: " + age);

        double minHours;
        double maxHours;

        if (age <= 17) {
            minHours = 8;
            maxHours = 10;
        } else if (age >= 18 && age <= 64) {
            minHours = 7;
            maxHours = 9;
        } else { // age >= 65
            minHours = 7;
            maxHours = 8;
        }
        System.out.println("DEBUG_BACKEND: Ideal sleep range for age " + age + ": " + minHours + "-" + maxHours + " hours.");

        Optional<DataTidur> latestSleepOptional = dataTidurRepository.findTopByPengguna_IdOrderByWaktuMulaiDesc(userId);

        String mascot;
        String mascotName;
        String mascotDescription;
        int sleepScoreLastRecord = 0; // Default score if no record or score is null

        if (latestSleepOptional.isEmpty()) {
            mascot = "❓";
            mascotName = "Tidak Ada Data";
            mascotDescription = "Belum ada data tidur untuk dianalisis.";
            System.out.println("DEBUG_BACKEND: Tidak ada data tidur terakhir untuk pengguna " + userId + ". Menggunakan default maskot dan skor 0.");
        } else {
            DataTidur latestSleep = latestSleepOptional.get();
            LocalDateTime startTime = latestSleep.getWaktuMulai();
            LocalTime durationTime = latestSleep.getDurasi();

            if (latestSleep.getSkor() != null) { 
                sleepScoreLastRecord = latestSleep.getSkor();
            } else {
                System.out.println("DEBUG_BACKEND: Skor tidur terakhir untuk pengguna " + userId + " adalah null. Menggunakan default 0.");
            }

            if (startTime == null || durationTime == null) {
                mascot = "❓";
                mascotName = "Tidak Ada Data";
                mascotDescription = "Data waktu tidur tidak lengkap.";
                System.out.println("DEBUG_BACKEND: Data waktu tidur terakhir untuk pengguna " + userId + " tidak lengkap. Menggunakan default maskot.");
            } else {
                double sleepHours = durationTime.getHour() + (durationTime.getMinute() / 60.0) + (durationTime.getSecond() / 3600.0);

                mascot = determineMascotEmoji(age, startTime, durationTime, minHours, maxHours);
                mascotName = determineMascotName(mascot);
                mascotDescription = determineMascotDescription(mascot, sleepHours);
            }
        }
        
        System.out.println("DEBUG_BACKEND: Calculated Main Mascot: " + mascotName + ", Last Sleep Score: " + sleepScoreLastRecord);

        return ResponseEntity.ok(new MainMascotStatus(mascot, mascotName, mascotDescription, sleepScoreLastRecord));
    }


    @GetMapping("/get-session-data-flutter/{userId}")
    public ResponseEntity<SessionDataFlutter> getSessionDataForFlutter(@PathVariable Integer userId) {
        Optional<Pengguna> optionalPengguna = getPenggunaById(userId);
        if (optionalPengguna.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new SessionDataFlutter(false, null, "Pengguna not found with ID: " + userId));
        }
        Pengguna pengguna = optionalPengguna.get();

        if (pengguna.isState()) { 
            DataTidur ongoingSession = dataTidurService.cariTerbaruDataTidur(pengguna);
            if (ongoingSession != null && ongoingSession.getWaktuSelesai() == null) { 
                SessionDataFlutter sessionData = new SessionDataFlutter(pengguna.isState(), ongoingSession.getWaktuMulai(), "Active session found.");
                return ResponseEntity.ok(sessionData);
            }
        }
        return ResponseEntity.ok(new SessionDataFlutter(pengguna.isState(), null, "No active session."));
    }

    // DTO for session data response to Flutter
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


    @GetMapping("/sleep-analysis/{userId}")
    public ResponseEntity<?> getSleepAnalysis(@PathVariable Integer userId) {
        Optional<Pengguna> optionalPengguna = getPenggunaById(userId);
        if (optionalPengguna.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Pengguna not found with ID: " + userId));
        }
        Pengguna pengguna = optionalPengguna.get();

        Analisis analisisResult = dataTidurService.analisisTidur(userId, pengguna);

        if (analisisResult.getMessage() != null && analisisResult.getMessage().contains("Not enough sleep data")) {
            return ResponseEntity.ok(Map.of(
                "message", analisisResult.getMessage(),
                "requiredData", analisisResult.getRequiredData(),
                "currentData", analisisResult.getCurrentData(),
                "suggestion", analisisResult.getSuggestion(),
                "mascot", analisisResult.getMascot() != null ? analisisResult.getMascot() : "❓",
                "mascotName", analisisResult.getMascotName() != null ? analisisResult.getMascotName() : "Tidak Ada Data",
                "mascotDescription", analisisResult.getMascotDescription() != null ? analisisResult.getMascotDescription() : "Belum ada cukup data tidur untuk analisis.",
                "averageSleepScore", analisisResult.getRataSkorTidur()
            ));
        }

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
        responseMap.put("wakeTimeVariabilityMinutes", analisisResult.getWakeTimeVariability() != null ? analisisResult.getWakeTimeVariability().toMinutes() : 0L);
        responseMap.put("isSleepTimeConsistent", analisisResult.getIsSleepTimeConsistent());
        responseMap.put("isWakeTimeConsistent", analisisResult.getIsWakeTimeConsistent());

        return ResponseEntity.ok(responseMap);
    }

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
                "startTime", savedRecord.getWaktuMulai() != null ? savedRecord.getWaktuMulai().toString() : null,
                "endTime", savedRecord.getWaktuSelesai() != null ? savedRecord.getWaktuSelesai().toString() : null,
                "duration", savedRecord.getDurasi() != null ? savedRecord.getDurasi().toString() : null,
                "score", savedRecord.getSkor() != null ? savedRecord.getSkor() : 0,
                "date", savedRecord.getTanggal() != null ? savedRecord.getTanggal().toString() : null
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
            pengguna.setState(true);
            penggunaRepository.save(pengguna);
            DataTidur newRecord = dataTidurService.cariTerbaruDataTidur(pengguna);
            if (newRecord == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to retrieve newly started record."));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Sleep tracking started",
                "id", newRecord.getId(),
                "startTime", newRecord.getWaktuMulai() != null ? newRecord.getWaktuMulai().toString() : null
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
            pengguna.setState(false);
            penggunaRepository.save(pengguna);
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
                    "startTime", updatedRecord.getWaktuMulai() != null ? updatedRecord.getWaktuMulai().toString() : null,
                    "endTime", updatedRecord.getWaktuSelesai() != null ? updatedRecord.getWaktuSelesai().toString() : null,
                    "duration", updatedRecord.getDurasi() != null ? updatedRecord.getDurasi().toString() : null,
                    "score", updatedRecord.getSkor() != null ? updatedRecord.getSkor() : 0
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

        int todayIndex = today.getDayOfWeek().getValue() - 1; 

        String dateRangeStart = startDate.format(DateTimeFormatter.ofPattern("dd MMM"));
        String dateRangeEnd = today.format(DateTimeFormatter.ofPattern("dd MMM"));


        return ResponseEntity.ok(Map.of(
            "weeklyScores", weeklyScores,
            "dayLabels", dayLabels,
            "todayIndex", todayIndex,
            "dateRangeStart", dateRangeStart,
            "dateRangeEnd", dateRangeEnd
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
            "startTime", latestRecord.getWaktuMulai() != null ? latestRecord.getWaktuMulai().toString() : null,
            "endTime", latestRecord.getWaktuSelesai() != null ? latestRecord.getWaktuSelesai().toString() : null,
            "duration", latestRecord.getDurasi() != null ? latestRecord.getDurasi().toString() : null,
            "score", latestRecord.getSkor() != null ? latestRecord.getSkor() : 0,
            "date", latestRecord.getTanggal() != null ? latestRecord.getTanggal().toString() : null
        ));
    }
    

    @GetMapping("/sleep-records/history/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getSleepRecordsHistory(@PathVariable Integer userId) {
        Optional<Pengguna> optionalPengguna = getPenggunaById(userId);
        if (optionalPengguna.isEmpty()) {
            System.err.println("DEBUG_BACKEND: Pengguna with ID " + userId + " not found for sleep history.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        Pengguna pengguna = optionalPengguna.get();

        // Ambil umur pengguna untuk menentukan rentang jam tidur ideal
        LocalDate userDateOfBirth = pengguna.getTanggalLahir();
        if (userDateOfBirth == null) {
            System.err.println("DEBUG_BACKEND: Tanggal lahir tidak tersedia untuk pengguna " + userId + ". Tidak dapat menentukan maskot riwayat. Menggunakan umur default.");
            userDateOfBirth = LocalDate.of(2000, 1, 1);
        }
        int age = Period.between(userDateOfBirth, LocalDate.now()).getYears();

        double minHours;
        double maxHours;
        if (age <= 17) {
            minHours = 8;
            maxHours = 10;
        } else if (age >= 18 && age <= 64) {
            minHours = 7;
            maxHours = 9;
        } else { // age >= 65
            minHours = 7;
            maxHours = 8;
        }

        List<DataTidur> historyRecords = dataTidurService.findByPenggunaOrderByWaktuSelesaiDesc(pengguna);

        List<Map<String, Object>> responseList = new ArrayList<>();
        if (historyRecords.isEmpty()) {
            System.out.println("DEBUG_BACKEND: Tidak ada data tidur riwayat untuk pengguna " + userId + ".");
            return ResponseEntity.ok(Collections.emptyList());
        }

        for (DataTidur record : historyRecords) {
            Map<String, Object> recordMap = new HashMap<>();
            recordMap.put("id", record.getId());
            recordMap.put("startTime", record.getWaktuMulai() != null ? record.getWaktuMulai().toString() : null);
            recordMap.put("endTime", record.getWaktuSelesai() != null ? record.getWaktuSelesai().toString() : null);
            recordMap.put("duration", record.getDurasi() != null ? record.getDurasi().toString() : null);
            recordMap.put("score", record.getSkor() != null ? record.getSkor() : 0);
            recordMap.put("date", record.getTanggal() != null ? record.getTanggal().toString() : null);

            String emoji = "😊";
            if (record.getWaktuMulai() != null && record.getWaktuSelesai() != null && record.getDurasi() != null) {
                double sleepHours = record.getDurasi().getHour() + (record.getDurasi().getMinute() / 60.0) + (record.getDurasi().getSecond() / 3600.0);
                emoji = determineMascotEmoji(age, record.getWaktuMulai(), record.getDurasi(), minHours, maxHours);
            }
            recordMap.put("emoji", emoji);

            responseList.add(recordMap);
        }
        System.out.println("DEBUG_BACKEND: Mengirim " + responseList.size() + " riwayat tidur untuk pengguna " + userId + ".");
        return ResponseEntity.ok(responseList);
    }
}