// src/main/java/com/turu/service/DataTidurService.java
package com.turu.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.Duration; // Pastikan Duration terimport

import com.turu.model.Analisis; // Asumsi kelas Analisis sudah ada
import com.turu.model.DataTidur;
import com.turu.model.Pengguna;
import com.turu.repository.DataTidurRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;  // Untuk sorting
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DataTidurService {


    ZoneId zone = ZoneId.of("Asia/Jakarta");
    private DataTidurRepository dataTidurRepository;

    public DataTidurService(DataTidurRepository dataTidurRepository) {
        this.dataTidurRepository = dataTidurRepository;
    }

    public void hapusSemuaDataTidur(Pengguna pengguna) {
        List<DataTidur> dataTidurList = dataTidurRepository.findByPengguna(pengguna);
        if (!dataTidurList.isEmpty()) {
            dataTidurRepository.deleteAll(dataTidurList);
        }
    }

    // TIDUR - START
    public void addStart(LocalDateTime time, Pengguna pengguna) {
        LocalDateTime adjustedTime = time.plusHours(7);
        DataTidur newDt = new DataTidur(); // <-- PENTING: Membuat instance baru
        newDt.setWaktuMulai(adjustedTime);
        newDt.setIdPengguna(pengguna);
        // Pastikan tanggal diset agar nanti bisa di-query berdasarkan tanggal
        newDt.setTanggal(adjustedTime.toLocalDate()); 
        dataTidurRepository.save(newDt);
    }

    public boolean addEnd(LocalDateTime time, Pengguna pengguna) {
        LocalDateTime adjustedTime = time.plusHours(7);

        // Menggunakan Optional untuk penanganan yang lebih baik
        Optional<DataTidur> optionalDtToUpdate = dataTidurRepository.findTopByPenggunaOrderByWaktuMulaiDesc(pengguna);
        
        if (optionalDtToUpdate.isEmpty()) {
            System.err.println("Error: No active sleep record found for user " + pengguna.getUsername() + " to end.");
            return false; // Mengindikasikan gagal mengakhiri
        }
        DataTidur dtToUpdate = optionalDtToUpdate.get();

        // Pastikan waktu mulai tidak null sebelum menghitung durasi
        if (dtToUpdate.getWaktuMulai() == null) {
             System.err.println("Error: Start time is null for the active sleep record for user " + pengguna.getUsername());
             return false; // Sesi tidak valid
        }

        dtToUpdate.setWaktuSelesai(adjustedTime);
        dtToUpdate.setTanggal(adjustedTime.toLocalDate());
        dtToUpdate.hitungDurasi(); // Hitung durasi setelah waktu selesai diset
        
        int usia = 0;
        if (pengguna.getTanggalLahir() != null) {
            usia = Period.between(pengguna.getTanggalLahir(), ZonedDateTime.now(zone).toLocalDate()).getYears();
        }
        dtToUpdate.hitungSkor(usia);

        // Cek durasi minimal untuk disimpan
        if (dtToUpdate.getDurasi().isBefore(LocalTime.of(0, 15, 0))) { // Durasi kurang dari 15 menit
            dataTidurRepository.deleteById(dtToUpdate.getId());
            return true; // Mengindikasikan dihapus karena terlalu singkat
        } else {
            dataTidurRepository.save(dtToUpdate);
            return false; // Mengindikasikan berhasil diakhiri dan disimpan
        }
    }

    // Metode ini untuk menambah data tidur manual
    public void tambah(DataTidur dt, Pengguna pengguna){
        // Ini adalah metode yang Anda sebutkan sudah ada.
        // PENTING: Asumsi dt yang masuk ke sini sudah diisi waktuMulai dan waktuSelesai
        // Serta sudah ada penyesuaian +7 jam di sisi controller/caller jika diperlukan.
        // Jika tidak, Anda perlu menambahkannya di sini.
        // Untuk konsistensi, mari kita sesuaikan di sini juga:
        if (dt.getWaktuMulai() != null && dt.getWaktuSelesai() != null) {
            dt.setWaktuMulai(dt.getWaktuMulai().plusHours(7));
            dt.setWaktuSelesai(dt.getWaktuSelesai().plusHours(7));
        }

        dt.setIdPengguna(pengguna);
        dt.setTanggal(dt.getWaktuSelesai().toLocalDate());
        dt.hitungDurasi();
        
        int usia = 0;
        if (pengguna.getTanggalLahir() != null) {
            usia = Period.between(pengguna.getTanggalLahir(), ZonedDateTime.now(zone).toLocalDate()).getYears();
        }
        dt.hitungSkor(usia);
        
        dataTidurRepository.save(dt);
    }

    // Metode ini digunakan oleh Controller untuk cek duplikat
    public boolean cekDuplikatDataTidur(DataTidur dt, Pengguna pengguna){
        List<DataTidur> A = dataTidurRepository.findByPengguna(pengguna);
        for (DataTidur data : A){
            if (data.getTanggal().equals(dt.getWaktuSelesai().toLocalDate())){
                return true ;
            }
        }
        return false ;
    }

    public void update(DataTidur dt, Pengguna pengguna){
        // PENTING: Asumsi dt yang masuk ke sini sudah diisi waktuMulai dan waktuSelesai
        // dan sudah disesuaikan +7 jam jika diperlukan di sisi caller.
        dt.hitungDurasi();
        
        int usia = 0;
        if (pengguna.getTanggalLahir() != null) {
            usia = Period.between(pengguna.getTanggalLahir(), ZonedDateTime.now(zone).toLocalDate()).getYears();
        }
        dt.hitungSkor(usia);
        dt.setTanggal(dt.getWaktuSelesai().toLocalDate()); // Perbarui tanggal juga jika waktu selesai berubah
        dataTidurRepository.save(dt);
    }

    // =========================================================================================
    // METODE ANALISIS TIDUR YANG DIMODIFIKASI SECARA MENYELURUH
    // Sekarang metode ini akan melakukan semua perhitungan analisis kompleks
    // =========================================================================================
    public Analisis analisisTidur(Integer userId, Pengguna pengguna) { // Menerima objek Pengguna
        // 1. Ambil semua data tidur pengguna
        List<DataTidur> allSleepRecords = dataTidurRepository.findByPengguna(pengguna);
        
        // Filter hanya data tidur yang lengkap dan urutkan dari yang terbaru
        List<DataTidur> completedSleepRecords = allSleepRecords.stream()
            .filter(record -> record.getWaktuSelesai() != null && record.getWaktuMulai() != null)
            .sorted(Comparator.comparing(DataTidur::getWaktuSelesai).reversed()) // Urutkan dari yang terbaru
            .collect(Collectors.toList());

        // 2. Cek jumlah data tidur yang lengkap
        if (completedSleepRecords.size() < 7) {
            // Jika kurang dari 7, kembalikan objek Analisis dengan pesan khusus
            // Controller harus memeriksa ini.
            Analisis analisis = new Analisis();
            analisis.setMessage("Not enough completed sleep data for analysis.");
            analisis.setRequiredData(7);
            analisis.setCurrentData(completedSleepRecords.size());
            analisis.setSuggestion("Anda membutuhkan minimal 7 data tidur yang lengkap untuk mendapatkan hasil analisis tidur anda.");
            return analisis;
        }

        // 3. Ambil 7 data tidur terakhir
        List<DataTidur> last7SleepRecords = completedSleepRecords.subList(0, Math.min(completedSleepRecords.size(), 7));

        long totalSleepDurationSeconds = 0;
        long sumStartSecondOfDay = 0;
        long sumEndSecondOfDay = 0;

        LocalTime minStartTime = LocalTime.MAX;
        LocalTime maxStartTime = LocalTime.MIN;
        LocalTime minEndTime = LocalTime.MAX;
        LocalTime maxEndTime = LocalTime.MIN;

        int totalScore = 0;

        // Loop melalui 7 data tidur terakhir untuk perhitungan
        for (DataTidur record : last7SleepRecords) {
            // Durasi tidur
            if (record.getDurasi() != null) {
                totalSleepDurationSeconds += record.getDurasi().toSecondOfDay();
            }

            // Skor
            totalScore += record.getSkor();

            // Waktu mulai (untuk rata-rata dan variabilitas)
            LocalTime startTime = record.getWaktuMulai().toLocalTime();
            sumStartSecondOfDay += startTime.toSecondOfDay();
            if (startTime.isBefore(minStartTime)) minStartTime = startTime;
            if (startTime.isAfter(maxStartTime)) maxStartTime = startTime;

            // Waktu selesai/bangun (untuk rata-rata dan variabilitas)
            LocalTime endTime = record.getWaktuSelesai().toLocalTime();
            sumEndSecondOfDay += endTime.toSecondOfDay();
            if (endTime.isBefore(minEndTime)) minEndTime = endTime;
            if (endTime.isAfter(maxEndTime)) maxEndTime = endTime;
        }

        // Perhitungan Rata-rata
        double averageSleepDuration = (double) totalSleepDurationSeconds / last7SleepRecords.size(); // dalam detik
        double averageScore = (double) totalScore / last7SleepRecords.size();
        LocalTime averageStartTime = LocalTime.ofSecondOfDay((long) (sumStartSecondOfDay / last7SleepRecords.size()));
        LocalTime averageEndTime = LocalTime.ofSecondOfDay((long) (sumEndSecondOfDay / last7SleepRecords.size()));

        // Perhitungan Variabilitas
        Duration sleepTimeVariability = Duration.between(minStartTime, maxStartTime).abs();
        Duration wakeTimeVariability = Duration.between(minEndTime, maxEndTime).abs();
        
        // Batas toleransi untuk konsistensi (misal: 45 menit)
        Duration consistencyTolerance = Duration.ofMinutes(45); // Bisa disesuaikan

        boolean isSleepTimeConsistent = sleepTimeVariability.compareTo(consistencyTolerance) <= 0;
        boolean isWakeTimeConsistent = wakeTimeVariability.compareTo(consistencyTolerance) <= 0;

        // Dapatkan rentang tidur ideal berdasarkan usia
        int age = Period.between(pengguna.getTanggalLahir(), ZonedDateTime.now(zone).toLocalDate()).getYears();
        double minIdealHours;
        double maxIdealHours;
        if (age <= 17) { minIdealHours = 8; maxIdealHours = 10; }
        else if (age >= 18 && age <= 64) { minIdealHours = 7; maxIdealHours = 9; }
        else { minIdealHours = 7; maxIdealHours = 8; }
        
        double averageSleepDurationHours = averageSleepDuration / 3600.0;
        boolean isDurationIdeal = averageSleepDurationHours >= minIdealHours && averageSleepDurationHours <= maxIdealHours;


        // Penentuan Maskot dan Saran
        String mascot = "❓"; // Default jika tidak ada yang cocok
        String mascotName = "Tidak Ada Data";
        String mascotDescription = "Belum ada cukup data tidur untuk analisis.";
        List<String> suggestions = new ArrayList<>();

        if (isDurationIdeal) {
            mascot = "🦁"; // Singa Prima
            mascotName = "Singa Prima";
            mascotDescription = "Durasi tidur Anda sudah ideal!";
        } else if (averageSleepDurationHours < minIdealHours) {
            mascot = "🦉"; // Burung Hantu (kurang tidur)
            mascotName = "Burung Hantu";
            mascotDescription = "Durasi tidur Anda kurang dari yang direkomendasikan.";
        } else { // averageSleepDurationHours > maxIdealHours
            mascot = "🐼"; // Panda Tidur (tidur berlebihan)
            mascotName = "Panda Tidur";
            mascotDescription = "Durasi tidur Anda melebihi yang direkomendasikan.";
        }

        // Tambahkan saran berdasarkan konsistensi
        if (!isSleepTimeConsistent) {
            suggestions.add("Waktu tidur Anda kurang konsisten (variasi " + formatDurationShort(sleepTimeVariability) + "). Coba tetapkan jadwal tidur yang lebih teratur.");
        }
        if (!isWakeTimeConsistent) {
            suggestions.add("Waktu bangun Anda kurang konsisten (variasi " + formatDurationShort(wakeTimeVariability) + "). Usahakan bangun pada jam yang sama setiap hari.");
        }
        
        // Saran durasi (jika belum ideal)
        if (!isDurationIdeal) {
            if (averageSleepDurationHours < minIdealHours) {
                 suggestions.add(String.format("Anda tidur rata-rata %.1f jam, idealnya %.0f-%0.f jam. Coba tambah durasi tidur Anda.", averageSleepDurationHours, minIdealHours, maxIdealHours));
            } else { // > maxIdealHours
                 suggestions.add(String.format("Anda tidur rata-rata %.1f jam, idealnya %.0f-%0.f jam. Tidur berlebihan kadang bisa berdampak negatif.", averageSleepDurationHours, minIdealHours, maxIdealHours));
            }
        } else {
             // Jika durasi ideal, tambahkan saran yang sesuai
             suggestions.add("Pertahankan durasi tidur yang ideal ini untuk kesehatan optimal.");
        }

        // Gabungkan saran menjadi satu string
        String finalSuggestion = suggestions.isEmpty() ? "Terus lacak tidur Anda untuk mendapatkan saran yang lebih personal." : String.join("\n", suggestions);


        // Bangun objek Analisis yang baru dan lebih kaya
        Analisis finalAnalisis = new Analisis();
        finalAnalisis.setRataWaktuBangun(averageSleepDuration); // Ini sekarang rata-rata durasi tidur dalam detik
        finalAnalisis.setRataSkorTidur(averageScore);
        finalAnalisis.setRentangTidur(Duration.between(averageStartTime.atDate(LocalDate.MIN), averageEndTime.atDate(LocalDate.MIN))); // Gunakan rentang antara rata-rata waktu tidur dan bangun (bukan min-max dari semua data)
                                                                                                                        // Atau definisikan rentang total dari data yang diambil.
                                                                                                                        // Untuk contoh ini, saya menggunakan rentang rata-rata saja.
        finalAnalisis.setMascot(mascot); // Jika Analisis punya setter untuk mascot
        finalAnalisis.setMascotName(mascotName); // Jika Analisis punya setter untuk mascotName
        finalAnalisis.setMascotDescription(mascotDescription); // Jika Analisis punya setter untuk mascotDescription
        finalAnalisis.setSuggestion(finalSuggestion); // Jika Analisis punya setter untuk suggestion

        // Tambahkan atribut baru ke objek Analisis (jika model Analisis mendukungnya)
        // finalAnalisis.setAverageSleepTime(averageStartTime);
        // finalAnalisis.setAverageWakeTime(averageEndTime);
        // finalAnalisis.setSleepTimeVariability(sleepTimeVariability);
        // finalAnalisis.setWakeTimeVariability(wakeTimeVariability);
        // finalAnalisis.setIsSleepTimeConsistent(isSleepTimeConsistent);
        // finalAnalisis.setIsWakeTimeConsistent(isWakeTimeConsistent);

        return finalAnalisis;
    }

    // Helper untuk memformat Duration menjadi string pendek (contoh: "1j 30m")
    private String formatDurationShort(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        if (hours > 0 && minutes > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else if (hours > 0) {
            return String.format("%dh", hours);
        } else {
            return String.format("%dm", minutes);
        }
    }


    // =========================================================================================
    // METODE LAINNYA
    // =========================================================================================

    public List<DataTidur> getDataTidurMingguan(LocalDate startDate, LocalDate endDate, Pengguna pengguna) {
        return dataTidurRepository.findByPenggunaAndTanggalBetween(pengguna, startDate, endDate);
    }

    // Implementasi metode baru getDataTidurByPengguna
    
    public List<DataTidur> getDataTidurByPengguna(Pengguna pengguna) {
        // Ambil semua data tidur untuk pengguna, diurutkan berdasarkan waktu selesai terbaru
        return dataTidurRepository.findByPenggunaOrderByWaktuSelesaiDesc(pengguna);
    }

    // Perhatikan: Menggunakan Optional untuk penanganan null yang lebih baik
    public DataTidur cariTerbaruDataTidur(Pengguna pengguna) {
        // Pastikan Anda sudah menambahkan findTopByPenggunaOrderByWaktuMulaiDesc di DataTidurRepository
        Optional<DataTidur> latestRecord = dataTidurRepository.findTopByPenggunaOrderByWaktuMulaiDesc(pengguna);
        return latestRecord.orElse(null); // Mengembalikan null jika tidak ada data
    }

    public List<DataTidur> getAllDataTidur() {
        return dataTidurRepository.findAll();
    }

    public List<Object[]> getSkorByTanggalAndPengguna(Integer userId) {
        return dataTidurRepository.findSkorByTanggalAndPenggunaId(userId);
    }

    // Implementasi metode tambahManual
    public void tambahManual(LocalDateTime startTime, LocalDateTime endTime, Pengguna pengguna) {
        LocalDateTime adjustedStartTime = startTime.plusHours(7);
        LocalDateTime adjustedEndTime = endTime.plusHours(7);

        DataTidur manualDt = new DataTidur(); // <-- PENTING: Membuat instance baru
        manualDt.setWaktuMulai(adjustedStartTime);
        manualDt.setWaktuSelesai(adjustedEndTime);
        manualDt.setIdPengguna(pengguna);
        manualDt.setTanggal(adjustedEndTime.toLocalDate());

        manualDt.hitungDurasi();
        int usia = 0;
        if (pengguna.getTanggalLahir() != null) {
            usia = Period.between(pengguna.getTanggalLahir(), ZonedDateTime.now(zone).toLocalDate()).getYears();
        }
        manualDt.hitungSkor(usia);
        
        // Pengecekan duplikat sebelum menyimpan (controller akan menangani IllegalArgumentException)
        if (cekDuplikatDataTidur(manualDt, pengguna)) {
            throw new IllegalArgumentException("Data tidur untuk tanggal ini sudah ada.");
        }
        
        dataTidurRepository.save(manualDt);
    }
}