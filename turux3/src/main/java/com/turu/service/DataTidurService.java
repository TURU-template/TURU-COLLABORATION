// src/main/java/com/turu/service/DataTidurService.java
package com.turu.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.Duration; // Pastikan Duration terimport
import java.time.ZoneId;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.turu.model.Analisis; // Asumsi kelas Analisis sudah ada
import com.turu.model.DataTidur;
import com.turu.model.Pengguna;
import com.turu.repository.DataTidurRepository; // Pastikan repository terimport dengan benar
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired; // Tambahkan import Autowired

@Service
public class DataTidurService {

    // Menggunakan constructor injection adalah praktik terbaik
    private final DataTidurRepository dataTidurRepository;
    private final ZoneId zone = ZoneId.of("Asia/Jakarta"); // Tetap pertahankan zona waktu jika digunakan

    // Constructor Injection untuk DataTidurRepository
    @Autowired // @Autowired opsional jika hanya ada satu constructor
    public DataTidurService(DataTidurRepository dataTidurRepository) {
        this.dataTidurRepository = dataTidurRepository;
    }

    /**
     * Menghapus semua data tidur untuk pengguna tertentu.
     * Menggunakan method repository findByPengguna_IdOrderByWaktuSelesaiDesc.
     * @param pengguna Objek Pengguna.
     */
    public void hapusSemuaDataTidur(Pengguna pengguna) {
        // Menggunakan findByPengguna_IdOrderByWaktuSelesaiDesc yang mengembalikan List
        List<DataTidur> dataTidurList = dataTidurRepository.findByPengguna_IdOrderByWaktuSelesaiDesc(pengguna.getId());
        if (!dataTidurList.isEmpty()) {
            dataTidurRepository.deleteAll(dataTidurList);
        }
    }

    /**
     * Menambahkan record awal tidur.
     * @param time Waktu mulai tidur.
     * @param pengguna Objek Pengguna.
     */
    public void addStart(LocalDateTime time, Pengguna pengguna) {
        LocalDateTime adjustedTime = time.plusHours(7); // Penyesuaian zona waktu +7 jam
        DataTidur newDt = new DataTidur();
        newDt.setWaktuMulai(adjustedTime);
        newDt.setIdPengguna(pengguna); // Set relasi Pengguna
        newDt.setTanggal(adjustedTime.toLocalDate());
        dataTidurRepository.save(newDt);
    }

    /**
     * Mengakhiri record tidur yang sedang berjalan.
     * @param time Waktu selesai tidur.
     * @param pengguna Objek Pengguna.
     * @return true jika record dihapus karena durasi terlalu singkat, false jika disimpan.
     */
    public boolean addEnd(LocalDateTime time, Pengguna pengguna) {
        LocalDateTime adjustedTime = time.plusHours(7); // Penyesuaian zona waktu +7 jam

        // Menggunakan findTopByPengguna_IdOrderByWaktuMulaiDesc untuk mencari sesi aktif terbaru
        Optional<DataTidur> optionalDtToUpdate = dataTidurRepository.findTopByPengguna_IdOrderByWaktuMulaiDesc(pengguna.getId());
        
        if (optionalDtToUpdate.isEmpty()) {
            System.err.println("Error: Tidak ada sesi tidur aktif untuk diakhiri untuk user " + pengguna.getUsername() + ".");
            return false;
        }
        DataTidur dtToUpdate = optionalDtToUpdate.get();

        if (dtToUpdate.getWaktuMulai() == null) {
            System.err.println("Error: Waktu mulai null untuk sesi tidur aktif user " + pengguna.getUsername());
            return false;
        }
        // Jika sesi sudah diakhiri sebelumnya (sudah ada waktuSelesai)
        if (dtToUpdate.getWaktuSelesai() != null) {
            System.err.println("Error: Sesi tidur untuk user " + pengguna.getUsername() + " sudah diakhiri sebelumnya.");
            return false; // Sesi sudah diakhiri
        }

        dtToUpdate.setWaktuSelesai(adjustedTime);
        dtToUpdate.setTanggal(adjustedTime.toLocalDate()); // Perbarui tanggal jika waktu selesai berpindah hari
        dtToUpdate.hitungDurasi(); // Panggil hitungDurasi di model DataTidur
        
        int usia = 0;
        if (pengguna.getTanggalLahir() != null) {
            usia = Period.between(pengguna.getTanggalLahir(), ZonedDateTime.now(zone).toLocalDate()).getYears();
        } else {
            System.err.println("Peringatan: Tanggal lahir pengguna null untuk perhitungan skor. Menggunakan usia 0.");
        }
        dtToUpdate.hitungSkor(usia); // Panggil hitungSkor di model DataTidur

        // Cek durasi minimal untuk disimpan (misal: kurang dari 15 menit dihapus)
        // Pastikan getDurasi() mengembalikan LocalTime dan isBefore berfungsi
        if (dtToUpdate.getDurasi() != null && dtToUpdate.getDurasi().isBefore(LocalTime.of(0, 15, 0))) {
            dataTidurRepository.deleteById(dtToUpdate.getId());
            return true; // Mengindikasikan dihapus karena terlalu singkat
        } else {
            dataTidurRepository.save(dtToUpdate);
            return false; // Mengindikasikan berhasil diakhiri dan disimpan
        }
    }

    /**
     * Menambahkan record tidur secara manual.
     * @param startTime Waktu mulai tidur.
     * @param endTime Waktu selesai tidur.
     * @param pengguna Objek Pengguna.
     * @throws IllegalArgumentException jika data tidur duplikat.
     */
    public void tambahManual(LocalDateTime startTime, LocalDateTime endTime, Pengguna pengguna) {
        // Penyesuaian zona waktu +7 jam untuk konsistensi
        LocalDateTime adjustedStartTime = startTime.plusHours(7);
        LocalDateTime adjustedEndTime = endTime.plusHours(7);

        DataTidur manualDt = new DataTidur();
        manualDt.setWaktuMulai(adjustedStartTime);
        manualDt.setWaktuSelesai(adjustedEndTime);
        manualDt.setIdPengguna(pengguna); // Set relasi Pengguna
        manualDt.setTanggal(adjustedEndTime.toLocalDate());

        // Pastikan hitungDurasi dan hitungSkor tidak bergantung pada save ke DB terlebih dahulu
        manualDt.hitungDurasi();
        
        int usia = 0;
        if (pengguna.getTanggalLahir() != null) {
            usia = Period.between(pengguna.getTanggalLahir(), ZonedDateTime.now(zone).toLocalDate()).getYears();
        } else {
             System.err.println("Peringatan: Tanggal lahir pengguna null untuk perhitungan skor manual. Menggunakan usia 0.");
        }
        manualDt.hitungSkor(usia);
        
        // Pengecekan duplikat sebelum menyimpan
        if (cekDuplikatDataTidur(manualDt, pengguna)) {
            throw new IllegalArgumentException("Data tidur untuk tanggal ini sudah ada.");
        }
        
        dataTidurRepository.save(manualDt);
    }

    /**
     * Memeriksa apakah ada data tidur yang sudah ada untuk tanggal yang sama bagi pengguna tertentu.
     * @param dt DataTidur yang akan dicek.
     * @param pengguna Objek Pengguna.
     * @return true jika ada duplikat, false jika tidak.
     */
    public boolean cekDuplikatDataTidur(DataTidur dt, Pengguna pengguna){
        // Menggunakan findByPengguna yang mengembalikan List berdasarkan objek Pengguna
        // Asumsi DataTidurRepository memiliki List<DataTidur> findByPengguna(Pengguna pengguna);
        List<DataTidur> existingData = dataTidurRepository.findByPengguna(pengguna);
        for (DataTidur data : existingData){
            // Cek berdasarkan tanggal record
            if (data.getTanggal() != null && dt.getTanggal() != null && data.getTanggal().equals(dt.getTanggal())){
                return true ;
            }
        }
        return false ;
    }

    /**
     * Memperbarui record DataTidur yang sudah ada.
     * @param dt Objek DataTidur yang akan diperbarui.
     * @param pengguna Objek Pengguna.
     */
    public void update(DataTidur dt, Pengguna pengguna){
        dt.hitungDurasi();
        
        int usia = 0;
        if (pengguna.getTanggalLahir() != null) {
            usia = Period.between(pengguna.getTanggalLahir(), ZonedDateTime.now(zone).toLocalDate()).getYears();
        } else {
            System.err.println("Peringatan: Tanggal lahir pengguna null untuk perhitungan skor update. Menggunakan usia 0.");
        }
        dt.hitungSkor(usia);
        dt.setTanggal(dt.getWaktuSelesai().toLocalDate()); // Perbarui tanggal juga jika waktu selesai berubah
        dataTidurRepository.save(dt);
    }

    /**
     * Melakukan analisis tidur komprehensif untuk pengguna tertentu.
     * Membutuhkan minimal 7 data tidur lengkap untuk analisis penuh.
     * @param userId ID pengguna.
     * @param pengguna Objek Pengguna.
     * @return Objek Analisis berisi hasil, maskot, dan saran.
     */
    public Analisis analisisTidur(Integer userId, Pengguna pengguna) {
        // Menggunakan findByPengguna_IdOrderByWaktuSelesaiDesc untuk mengambil semua riwayat
        List<DataTidur> allSleepRecords = dataTidurRepository.findByPengguna_IdOrderByWaktuSelesaiDesc(userId);
        
        List<DataTidur> completedSleepRecords = allSleepRecords.stream()
            .filter(record -> record.getWaktuSelesai() != null && record.getWaktuMulai() != null && record.getDurasi() != null && record.getSkor() != null) // Filter data yang lengkap
            .sorted(Comparator.comparing(DataTidur::getWaktuSelesai).reversed())
            .collect(Collectors.toList());

        if (completedSleepRecords.size() < 7) {
            Analisis analisis = new Analisis();
            analisis.setMessage("Not enough completed sleep data for analysis.");
            analisis.setRequiredData(7);
            analisis.setCurrentData(completedSleepRecords.size());
            analisis.setSuggestion("Anda membutuhkan minimal 7 data tidur yang lengkap untuk mendapatkan hasil analisis tidur anda.");
            analisis.setMascot("😊");
            analisis.setMascotName("Tidak Ada Data");
            analisis.setMascotDescription("Belum ada cukup data tidur untuk analisis.");
            analisis.setRataSkorTidur(0);
            return analisis;
        }

        List<DataTidur> last7SleepRecords = completedSleepRecords.subList(0, Math.min(completedSleepRecords.size(), 7));

        long totalSleepDurationSeconds = 0;
        long sumStartSecondOfDay = 0;
        long sumEndSecondOfDay = 0;

        LocalTime minStartTime = LocalTime.MAX;
        LocalTime maxStartTime = LocalTime.MIN;
        LocalTime minEndTime = LocalTime.MAX;
        LocalTime maxEndTime = LocalTime.MIN;

        int totalScore = 0;

        for (DataTidur record : last7SleepRecords) {
            totalSleepDurationSeconds += record.getDurasi().toSecondOfDay(); // Menggunakan LocalTime.toSecondOfDay()
            totalScore += record.getSkor();

            LocalTime startTime = record.getWaktuMulai().toLocalTime();
            sumStartSecondOfDay += startTime.toSecondOfDay();
            if (startTime.isBefore(minStartTime)) minStartTime = startTime;
            if (startTime.isAfter(maxStartTime)) maxStartTime = startTime;

            LocalTime endTime = record.getWaktuSelesai().toLocalTime();
            sumEndSecondOfDay += endTime.toSecondOfDay();
            if (endTime.isBefore(minEndTime)) minEndTime = endTime;
            if (endTime.isAfter(maxEndTime)) maxEndTime = endTime;
        }

        double averageSleepDuration = (double) totalSleepDurationSeconds / last7SleepRecords.size();
        double averageScore = (double) totalScore / last7SleepRecords.size();
        LocalTime averageStartTime = LocalTime.ofSecondOfDay((long) (sumStartSecondOfDay / last7SleepRecords.size()));
        LocalTime averageEndTime = LocalTime.ofSecondOfDay((long) (sumEndSecondOfDay / last7SleepRecords.size()));

        // Variabilitas: Duration.between membutuhkan Temporal dengan tanggal. Gunakan atDate(LocalDate.MIN) untuk konversi sementara.
        Duration sleepTimeVariability = Duration.between(minStartTime.atDate(LocalDate.MIN), maxStartTime.atDate(LocalDate.MIN)).abs();
        Duration wakeTimeVariability = Duration.between(minEndTime.atDate(LocalDate.MIN), maxEndTime.atDate(LocalDate.MIN)).abs();
        
        Duration consistencyTolerance = Duration.ofMinutes(45);

        boolean isSleepTimeConsistent = sleepTimeVariability.compareTo(consistencyTolerance) <= 0;
        boolean isWakeTimeConsistent = wakeTimeVariability.compareTo(consistencyTolerance) <= 0;

        int ageUser = Period.between(pengguna.getTanggalLahir(), ZonedDateTime.now(zone).toLocalDate()).getYears();
        double minIdealHours;
        double maxIdealHours;
        if (ageUser <= 17) { minIdealHours = 8; maxIdealHours = 10; }
        else if (ageUser >= 18 && ageUser <= 64) { minIdealHours = 7; maxIdealHours = 9; }
        else { minIdealHours = 7; maxIdealHours = 8; }
        
        double averageSleepDurationHours = averageSleepDuration / 3600.0;
        boolean isDurationIdeal = averageSleepDurationHours >= minIdealHours && averageSleepDurationHours <= maxIdealHours;

        String mascot = "😊";
        String mascotName = "Tidak Ada Data";
        String mascotDescription = "Belum ada cukup data tidur untuk analisis.";
        List<String> suggestions = new ArrayList<>();

        if (isDurationIdeal) {
            mascot = "🦁";
            mascotName = "Singa Prima";
            mascotDescription = "Durasi tidur Anda sudah ideal!";
        } else if (averageSleepDurationHours < minIdealHours) {
            mascot = "🦉";
            mascotName = "Burung Hantu";
            mascotDescription = "Durasi tidur Anda kurang dari yang direkomendasikan.";
        } else {
            mascot = "🐼";
            mascotName = "Panda Tidur";
            mascotDescription = "Durasi tidur Anda melebihi yang direkomendasikan.";
        }

        if (!isSleepTimeConsistent) {
            suggestions.add("Waktu tidur Anda kurang konsisten (variasi " + formatDurationShort(sleepTimeVariability) + "). Coba tetapkan jadwal tidur yang lebih teratur.");
        }
        if (!isWakeTimeConsistent) {
            suggestions.add("Waktu bangun Anda kurang konsisten (variasi " + formatDurationShort(wakeTimeVariability) + "). Usahakan bangun pada jam yang sama setiap hari.");
        }
        
        if (!isDurationIdeal) {
            if (averageSleepDurationHours < minIdealHours) {
                 suggestions.add(String.format("Anda tidur rata-rata %.1f jam, idealnya %.0f-%0.f jam. Coba tambah durasi tidur Anda.", averageSleepDurationHours, minIdealHours, maxIdealHours));
            } else {
                 suggestions.add(String.format("Anda tidur rata-rata %.1f jam, idealnya %.0f-%0.f jam. Tidur berlebihan kadang bisa berdampak negatif.", averageSleepDurationHours, minIdealHours, maxIdealHours));
            }
        } else {
             suggestions.add("Pertahankan durasi tidur yang ideal ini untuk kesehatan optimal.");
        }

        String finalSuggestion = suggestions.isEmpty() ? "Terus lacak tidur Anda untuk mendapatkan saran yang lebih personal." : String.join("\n", suggestions);

        Analisis finalAnalisis = new Analisis();
        finalAnalisis.setRataWaktuBangun(averageSleepDuration);
        finalAnalisis.setRataSkorTidur((int) Math.round(averageScore));
        finalAnalisis.setRentangTidur(Duration.between(averageStartTime.atDate(LocalDate.MIN), averageEndTime.atDate(LocalDate.MIN)));
        finalAnalisis.setMascot(mascot);
        finalAnalisis.setMascotName(mascotName);
        finalAnalisis.setMascotDescription(mascotDescription);
        finalAnalisis.setSuggestion(finalSuggestion);

        // Jika model Analisis Anda memiliki setter untuk ini, aktifkan baris ini:
        // finalAnalisis.setAverageSleepTime(averageStartTime);
        // finalAnalisis.setAverageWakeTime(averageEndTime);
        // finalAnalisis.setSleepTimeVariability(sleepTimeVariability);
        // finalAnalisis.setWakeTimeVariability(wakeTimeVariability);
        // finalAnalisis.setIsSleepTimeConsistent(isSleepTimeConsistent);
        // finalAnalisis.setIsWakeTimeConsistent(isWakeTimeConsistent);

        return finalAnalisis;
    }

    /**
     * Helper untuk memformat Duration menjadi string pendek (contoh: "1j 30m")
     * @param duration Objek Duration.
     * @return String format durasi.
     */
    private String formatDurationShort(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        if (hours > 0 && minutes > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else if (hours > 0) {
            return String.format("%dh", hours);
        } else if (minutes > 0){
            return String.format("%dm", minutes);
        } else {
            return "0m"; // Jika durasi 0
        }
    }

    /**
     * Mendapatkan data tidur mingguan untuk pengguna tertentu dalam rentang tanggal.
     * @param startDate Tanggal mulai.
     * @param endDate Tanggal berakhir.
     * @param pengguna Objek Pengguna.
     * @return Daftar DataTidur yang cocok.
     */
    // Perbaikan: Konversi ke List<DataTidur> karena findByPenggunaAndTanggalBetween mengembalikan List
    public List<DataTidur> getDataTidurMingguan(LocalDate startDate, LocalDate endDate, Pengguna pengguna) {
        return dataTidurRepository.findByPenggunaAndTanggalBetween(pengguna, startDate, endDate);
    }
    
    /**
     * Mencari data tidur terbaru untuk pengguna tertentu.
     * @param pengguna Objek Pengguna.
     * @return Objek DataTidur terbaru atau null jika tidak ada.
     */
    public DataTidur cariTerbaruDataTidur(Pengguna pengguna) {
        // Pastikan Anda sudah menambahkan findTopByPengguna_IdOrderByWaktuMulaiDesc di DataTidurRepository
        // dan cari by ID lebih konsisten dengan pola lain
        Optional<DataTidur> latestRecord = dataTidurRepository.findTopByPengguna_IdOrderByWaktuMulaiDesc(pengguna.getId());
        return latestRecord.orElse(null); // Mengembalikan null jika tidak ada data
    }

    /**
     * Mendapatkan semua data tidur yang tersimpan di database.
     * @return Daftar semua objek DataTidur.
     */
    public List<DataTidur> getAllDataTidur() {
        return dataTidurRepository.findAll();
    }

    /**
     * Mendapatkan skor tidur berdasarkan tanggal untuk pengguna tertentu.
     * @param userId ID pengguna.
     * @return Daftar Object[] berisi tanggal dan skor.
     */
    public List<Object[]> getSkorByTanggalAndPengguna(Integer userId) {
        return dataTidurRepository.findSkorByTanggalAndPenggunaId(userId);
    }
    
    /**
     * Mengambil semua riwayat tidur untuk pengguna tertentu, diurutkan dari yang paling baru.
     * Ini adalah metode yang akan dipanggil oleh DataTidurApiController.
     * @param pengguna Objek Pengguna.
     * @return Daftar DataTidur yang diurutkan.
     */
    public List<DataTidur> findByPenggunaOrderByWaktuSelesaiDesc(Pengguna pengguna) {
        // Menggunakan method repository findByPengguna_IdOrderByWaktuSelesaiDesc
        return dataTidurRepository.findByPengguna_IdOrderByWaktuSelesaiDesc(pengguna.getId());
    }

    public void tambah(DataTidur dt, Pengguna pengguna){
        dt.setIdPengguna(pengguna);
        dt.setTanggal(dt.getWaktuSelesai().toLocalDate());
        dt.hitungDurasi();
        dt.hitungSkor(Period.between(pengguna.getTanggalLahir(),  ZonedDateTime.now(zone).toLocalDate()).getYears());
        dataTidurRepository.save(dt);
    }
}