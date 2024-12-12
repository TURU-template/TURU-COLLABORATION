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
    private int skor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getWaktuMulai() {
        return waktuMulai;
    }

    public void setWaktuMulai(LocalDateTime waktuMulai) {
        this.waktuMulai = waktuMulai;
    }

    public LocalDateTime getWaktuSelesai() {
        return waktuSelesai;
    }

    public void setWaktuSelesai(LocalDateTime waktuSelesai) {
        this.waktuSelesai = waktuSelesai;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public LocalTime getDurasi() {
        return durasi;
    }

    public void setDurasi(LocalTime durasi) {
        this.durasi = durasi;
    }

    public int getSkor() {
        return skor;
    }

    public void setSkor(int skor) {
        this.skor = skor;
    }

    public void hitungDurasi() {
        LocalDateTime adjustedWaktuSelesai = waktuSelesai;
        if (waktuSelesai.isBefore(waktuMulai)) {
            adjustedWaktuSelesai = waktuSelesai.plusDays(1);
        }
        Duration duration = Duration.between(waktuMulai, adjustedWaktuSelesai);
        setDurasi(LocalTime.ofSecondOfDay(duration.getSeconds()));
    }

    public void hitungSkor() {}

}
