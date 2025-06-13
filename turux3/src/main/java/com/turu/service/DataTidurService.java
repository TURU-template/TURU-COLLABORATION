package com.turu.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.Duration;

import com.turu.model.Analisis;
import com.turu.model.DataTidur;
import com.turu.model.Pengguna;
import com.turu.repository.DataTidurRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional; // Tambahkan ini untuk cariTerbaruDataTidur

@Service
public class DataTidurService {
    // PENTING: Variabel dt ini adalah sumber masalah thread safety.
    // Namun, sesuai permintaan Anda untuk tidak mengubah, kita akan membiarkannya.
    // Harap pahami bahwa ini bisa menyebabkan perilaku tidak terduga dalam lingkungan multi-thread/concurrent.
    DataTidur dt = new DataTidur(); // <-- DIJAGA SEPERTI INI SESUAI PERMINTAAN

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
        // Tambahkan 7 jam ke waktu mulai
        LocalDateTime adjustedTime = time.plusHours(7);

        // Perhatikan: Menggunakan variabel instance dt. Ini bermasalah pada konkurensi.
        dt.setWaktuMulai(adjustedTime);
        dt.setIdPengguna(pengguna);
        dataTidurRepository.save(dt);
    }

    public boolean addEnd(LocalDateTime time, Pengguna pengguna) {
        // Tambahkan 7 jam ke waktu selesai
        LocalDateTime adjustedTime = time.plusHours(7);

        // Perhatikan: cariTerbaruDataTidur mengembalikan DataTidur atau null jika tidak ada.
        // Anda perlu memastikan cariTerbaruDataTidur di DataTidurRepository mengembalikan Optional<DataTidur>
        // dan di sini menangani kasus null.
        DataTidur dtToUpdate = cariTerbaruDataTidur(pengguna);
        
        // Menangani jika tidak ada data tidur terbaru (misalnya, sesi tidur belum dimulai)
        if (dtToUpdate == null) {
            // Log this or throw a specific exception if this state indicates an error
            System.err.println("Error: No active sleep record found for user " + pengguna.getUsername() + " to end.");
            return false; // Mengindikasikan gagal mengakhiri
        }

        dtToUpdate.setWaktuSelesai(adjustedTime);
        dtToUpdate.setTanggal(adjustedTime.toLocalDate());
        dtToUpdate.hitungDurasi();
        
        int usia = 0;
        if (pengguna.getTanggalLahir() != null) { // <-- SAFE: Tambahkan null check di sini
             usia = Period.between(pengguna.getTanggalLahir(), ZonedDateTime.now(zone).toLocalDate()).getYears();
        }
        dtToUpdate.hitungSkor(usia);

        if (dtToUpdate.getDurasi().isBefore(LocalTime.of(0, 15, 0))) {
            dataTidurRepository.deleteById(dtToUpdate.getId());
            return true;
        } else {
            dataTidurRepository.save(dtToUpdate);
            return false;
        }
    }

    // Metode ini untuk menambah data tidur manual
    public void tambah(DataTidur dt, Pengguna pengguna){
        // Ini adalah metode yang Anda sebutkan sudah ada.
        // Perhatikan: dt yang diterima adalah parameter, bukan variabel instance.
        // Pastikan dt yang diberikan sudah memiliki waktuMulai dan waktuSelesai yang benar.
        dt.setIdPengguna(pengguna);
        dt.setTanggal(dt.getWaktuSelesai().toLocalDate());
        dt.hitungDurasi();
        
        int usia = 0;
        if (pengguna.getTanggalLahir() != null) { // <-- SAFE: Tambahkan null check di sini
             usia = Period.between(pengguna.getTanggalLahir(), ZonedDateTime.now(zone).toLocalDate()).getYears();
        }
        dt.hitungSkor(usia);
        dataTidurRepository.save(dt);
    }

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
        dt.hitungDurasi();
        
        int usia = 0;
        if (pengguna.getTanggalLahir() != null) { // <-- SAFE: Tambahkan null check di sini
             usia = Period.between(pengguna.getTanggalLahir(), ZonedDateTime.now(zone).toLocalDate()).getYears();
        }
        dt.hitungSkor(usia);
        dt.setTanggal(dt.getWaktuSelesai().toLocalDate());
        dataTidurRepository.save(dt);
    }

    public Analisis analisisTidur(Integer userId) { // Gunakan Long untuk userId di repository
        List<DataTidur> dataTidurList = dataTidurRepository.findTop7ByPenggunaIdOrderByWaktuMulaiDesc(userId);
        
        // Handle case where no data is found
        if (dataTidurList.isEmpty()) {
            return new Analisis(); // Return a default/empty analysis object
        }

        Duration totalActualSleepDuration = Duration.ZERO; // Menggunakan ini untuk rata-rata durasi
        int totalSkor = 0;
        
        // Inisialisasi dengan data pertama, pastikan list tidak kosong
        LocalDateTime waktuTidurPertama = dataTidurList.get(0).getWaktuMulai();
        LocalDateTime waktuBangunTerakhir = dataTidurList.get(0).getWaktuSelesai();

        for (DataTidur dataTidur : dataTidurList) {
            // Hitung durasi tidur dari objek DataTidur
            if (dataTidur.getDurasi() != null) {
                totalActualSleepDuration = totalActualSleepDuration.plus(Duration.ofMinutes(dataTidur.getDurasi().toSecondOfDay() / 60));
            }
            totalSkor += dataTidur.getSkor();

            if (dataTidur.getWaktuMulai() != null && dataTidur.getWaktuMulai().isBefore(waktuTidurPertama)) {
                waktuTidurPertama = dataTidur.getWaktuMulai();
            }
            if (dataTidur.getWaktuSelesai() != null && dataTidur.getWaktuSelesai().isAfter(waktuBangunTerakhir)) {
                waktuBangunTerakhir = dataTidur.getWaktuSelesai();
            }
        }

        double rataRataDurasiTidurDetik = (double) totalActualSleepDuration.getSeconds() / dataTidurList.size();
        double rataSkorTidur = (double) totalSkor / dataTidurList.size();
        Duration rentangTidur = Duration.between(waktuTidurPertama, waktuBangunTerakhir);

        Analisis statistikTidur = new Analisis();
        statistikTidur.setRataWaktuBangun(rataRataDurasiTidurDetik); // Sesuaikan nama setter di Analisis
        statistikTidur.setRataSkorTidur(rataSkorTidur);
        statistikTidur.setRentangTidur(rentangTidur);

        return statistikTidur;
    }

    // STATISTIK
    public List<DataTidur> getDataTidurMingguan(LocalDate startDate, LocalDate endDate, Pengguna pengguna) {
        return dataTidurRepository.findByPenggunaAndTanggalBetween(pengguna, startDate, endDate);
    }

    // Perhatikan: Menggunakan Optional untuk penanganan null yang lebih baik
    public DataTidur cariTerbaruDataTidur(Pengguna pengguna) {
        // Pastikan Anda sudah menambahkan findTop1ByPenggunaOrderByWaktuMulaiDesc di DataTidurRepository
        Optional<DataTidur> latestRecord = dataTidurRepository.findTopByPenggunaOrderByWaktuMulaiDesc(pengguna);
        return latestRecord.orElse(null); // Mengembalikan null jika tidak ada data
    }

    public List<DataTidur> getAllDataTidur() {
        return dataTidurRepository.findAll();
    }

    public List<Object[]> getSkorByTanggalAndPengguna(Integer userId) { // Gunakan Long untuk userId di repository
        return dataTidurRepository.findSkorByTanggalAndPenggunaId(userId);
    }

    // Implementasi metode tambahManual
    public void tambahManual(LocalDateTime startTime, LocalDateTime endTime, Pengguna pengguna) {
        // Penyesuaian waktu seperti di addStart/addEnd jika diperlukan
        LocalDateTime adjustedStartTime = startTime.plusHours(7);
        LocalDateTime adjustedEndTime = endTime.plusHours(7);

        // Membuat instance baru DataTidur (PENTING untuk menghindari masalah dt instance variable)
        DataTidur manualDt = new DataTidur();
        manualDt.setWaktuMulai(adjustedStartTime);
        manualDt.setWaktuSelesai(adjustedEndTime);
        manualDt.setIdPengguna(pengguna);
        manualDt.setTanggal(adjustedEndTime.toLocalDate()); // Tanggal berdasarkan waktu selesai

        // Menghitung durasi dan skor
        manualDt.hitungDurasi();
        int usia = 0;
        if (pengguna.getTanggalLahir() != null) {
            usia = Period.between(pengguna.getTanggalLahir(), ZonedDateTime.now(zone).toLocalDate()).getYears();
        }
        manualDt.hitungSkor(usia);
        
        // Pengecekan duplikat sebelum menyimpan
        if (cekDuplikatDataTidur(manualDt, pengguna)) { // Memanggil cekDuplikatDataTidur yang sudah ada
            // Anda bisa melempar exception di sini untuk memberitahu controller
            throw new IllegalArgumentException("Data tidur untuk tanggal ini sudah ada.");
        }
        
        dataTidurRepository.save(manualDt);
    }
}