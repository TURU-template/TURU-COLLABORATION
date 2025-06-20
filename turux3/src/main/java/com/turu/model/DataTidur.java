package com.turu.model;

import jakarta.persistence.*;
import java.time.*;
@Entity
@Table(name = "data_tidur")
public class DataTidur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime waktuMulai;
    private LocalDateTime waktuSelesai;
    private LocalDate tanggal;
    private LocalTime durasi;
    private Integer skor;

    @ManyToOne
    @JoinColumn(name = "pengguna_id", nullable = false)
    private Pengguna pengguna;

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}

    public LocalDateTime getWaktuMulai() {return waktuMulai;}
    public void setWaktuMulai(LocalDateTime waktuMulai) {this.waktuMulai = waktuMulai;}

    public LocalDateTime getWaktuSelesai() {return waktuSelesai;}
    public void setWaktuSelesai(LocalDateTime waktuSelesai) {this.waktuSelesai = waktuSelesai;}

    public LocalDate getTanggal() {return tanggal;}
    public void setTanggal(LocalDate tanggal) {this.tanggal = tanggal;}

    public LocalTime getDurasi() {return durasi;}
    public void setDurasi(LocalTime durasi) {this.durasi = durasi;}

    public Integer getSkor() {return skor;}
    public void setSkor(Integer skor) {this.skor = skor;}
    
    public void hitungDurasi() {
        LocalDateTime adjustedWaktuSelesai = waktuSelesai;
        if (waktuSelesai.isBefore(waktuMulai)) {
            adjustedWaktuSelesai = waktuSelesai.plusDays(1);
        }
        Duration duration = Duration.between(waktuMulai, adjustedWaktuSelesai);
        if (duration.getSeconds() >= 86400){
            duration = Duration.ofSeconds(86399);
        }
        setDurasi(LocalTime.ofSecondOfDay(duration.getSeconds()));
    }

    public void hitungSkor(int age) {
        double minHours;
        double maxHours;
        double sleepDuration = 0;
        if (durasi.getSecond() >= 31) {
            sleepDuration = durasi.getHour() + (((double)(durasi.getMinute()+1))/60);
        } else {
            sleepDuration = durasi.getHour() + (((double)durasi.getMinute())/60);
        } 
         
        // Rekomendasi durasi tidur berdasarkan umur
        if (age >=13 && age <= 17) { // remaja
            minHours = 8;
            maxHours = 10;
        } else if (age >= 18 && age <= 64) { // dewasa
            minHours = 7;
            maxHours = 9;
        } else if (age == 65) {
            minHours = 7;
            maxHours = 8;
        } else {
            throw new IllegalArgumentException("Umur harus diantara 13 - 65");
        }

        // Hitung pergeseran
        double deviation = 0;
        if (sleepDuration < minHours) {
            deviation = (minHours - sleepDuration) * 60; 
        } else if (sleepDuration > maxHours) {
            deviation = (sleepDuration - maxHours) * 60; 
        }

        // Pengurangan score
        double reduction = deviation * 0.35; // minus 0,35 per menit
        int score = (int) Math.max(100 - reduction, 1); // Ensure score is within range [1, 100]

        setSkor(score);
    }
    public void setIdPengguna(Pengguna pengguna ){
        this.pengguna = pengguna;
    }
    public Pengguna getIdPengguna(){
        return this.pengguna;
    }
}
