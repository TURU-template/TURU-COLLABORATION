// src/main/java/com/turu/controllers/DataTidurApiController.java
package com.turu.controllers;

import com.turu.dto.SleepRecordRequest;
import com.turu.dto.StartSleepRequest;
import com.turu.dto.EndSleepRequest;
import com.turu.model.Analisis;
import com.turu.model.DataTidur;
import com.turu.model.Pengguna;
import com.turu.repository.PenggunaRepository;
import com.turu.service.DataTidurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    // Helper untuk mendapatkan Pengguna
    private Optional<Pengguna> getPenggunaById(Integer userId) {
        return penggunaRepository.findById(userId);
    }

    @PostMapping("/sleep-records/manual") // POST /api/sleep-records/manual
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
            // Panggil metode service yang mengembalikan void
            dataTidurService.tambahManual(request.getStartTime(), request.getEndTime(), pengguna);

            // Karena service mengembalikan void, kita harus mengambil record terbaru setelah disimpan.
            // Ini akan mengambil record yang baru saja ditambahkan (mengasumsikan ini adalah yang terbaru).
            DataTidur savedRecord = dataTidurService.cariTerbaruDataTidur(pengguna);

            if (savedRecord == null) {
                // Ini seharusnya tidak terjadi jika tambahManual berhasil,
                // tapi sebagai fallback untuk kasus di mana cariTerbaruDataTidur gagal.
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


    @PostMapping("/sleep/start") // POST /api/sleep/start
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
            // Panggil metode service yang mengembalikan void
            dataTidurService.addStart(request.getStartTime(), pengguna);

            // Karena service mengembalikan void, kita harus mengambil record terbaru setelah disimpan.
            // Ini akan mengambil record yang baru saja ditambahkan (mengasumsikan ini adalah yang terbaru).
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

    @PostMapping("/sleep/end") // POST /api/sleep/end
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
            if (deletedBecauseTooShort) {
                return ResponseEntity.ok(Map.of("message", "Sleep record was too short and deleted."));
            } else {
                // Ambil record yang baru saja diakhiri/diperbarui
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

    @GetMapping("/sleep-analysis/{userId}") // GET /api/sleep-analysis/{userId}
    public ResponseEntity<?> getSleepAnalysis(@PathVariable Integer userId) {
        Optional<Pengguna> optionalPengguna = getPenggunaById(userId);
        if (optionalPengguna.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Pengguna not found with ID: " + userId));
        }

        Analisis analisis = dataTidurService.analisisTidur(userId);

        // Handle case where no data is returned from service
        if (analisis == null || analisis.getRataWaktuBangun() == 0 && analisis.getRataSkorTidur() == 0 && analisis.getRentangTidur() == null) {
            return ResponseEntity.ok(Map.of(
                "message", "No sleep data available for analysis.",
                "averageSleepDurationSeconds", 0.0,
                "averageSleepDurationFormatted", "0.0 Jam",
                "averageSleepScore", 0.0,
                "overallSleepSpanSeconds", 0L,
                "mascot", "❓",
                "mascotName", "Tidak Ada Data",
                "mascotDescription", "Belum ada cukup data tidur untuk analisis.",
                "suggestion", "Mulai lacak tidur Anda untuk mendapatkan analisis personal."
            ));
        }

        String mascot = "😴";
        String mascotName = "Koala Pemalas";
        String mascotDescription = "Kamu tidur nyenyak semalam!";
        String saran = "Anda memerlukan tidur 30 menit lebih awal dari tidur kebiasaan Anda, atau bangun lebih akhir 30 menit dari kebiasaan bangun anda.";
        double rataRataDurasiJam = analisis.getRataWaktuBangun() / 3600.0;

        if (rataRataDurasiJam >= 7 && rataRataDurasiJam <= 9) {
            mascot = "🦁";
            mascotName = "Singa Prima";
            mascotDescription = "Tidur Anda sudah cukup baik!";
            saran = "Pertahankan pola tidur yang baik ini untuk kesehatan optimal.";
        } else if (rataRataDurasiJam < 6) {
            mascot = "🦉";
            mascotName = "Burung Hantu";
            mascotDescription = "Tidur Anda kurang dari yang direkomendasikan.";
            saran = "Coba tambah durasi tidur Anda 1-2 jam per malam secara bertahap.";
        } else if (rataRataDurasiJam > 9) {
            mascot = "🐼";
            mascotName = "Panda Tidur";
            mascotDescription = "Anda mungkin tidur terlalu banyak.";
            saran = "Tidur berlebihan kadang bisa berdampak negatif. Perhatikan bagaimana perasaan Anda.";
        }

        return ResponseEntity.ok(Map.of(
            "averageSleepDurationSeconds", analisis.getRataWaktuBangun(),
            "averageSleepDurationFormatted", String.format("%.1f Jam", rataRataDurasiJam),
            "averageSleepScore", analisis.getRataSkorTidur(),
            "overallSleepSpanSeconds", analisis.getRentangTidur() != null ? analisis.getRentangTidur().getSeconds() : 0L,
            "mascot", mascot,
            "mascotName", mascotName,
            "mascotDescription", mascotDescription,
            "suggestion", saran
        ));
    }

    @GetMapping("/sleep-stats/weekly/{userId}") // GET /api/sleep-stats/weekly/{userId}
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

    @GetMapping("/sleep-records/latest/{userId}") // GET /api/sleep-records/latest/{userId}
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