
package com.turu.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;
@Entity
@Table(name = "statistik")
public class Statistik {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate week;

    @OneToOne
    @JoinColumn(name = "pengguna_id", nullable = false)
    private Pengguna pengguna;

    @OneToMany(mappedBy = "statistik", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DataTidur> data;

    public Statistik() {
        this.data = new ArrayList<>(7);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getWeek() {
        return week;
    }

    public void setWeek(LocalDate week) {
        this.week = week;
    }

    public List<DataTidur> getData() {
        return data;
    }

    public void setData(List<DataTidur> data) {
        if (data.size() != 7) {
            throw new IllegalArgumentException("DataTidur list must have exactly 7 elements.");
        }
        this.data = data;
    }

    public void updateData(int dayIndex, DataTidur newData) {
        if (dayIndex < 0 || dayIndex >= 7) {
            throw new IndexOutOfBoundsException("Day index must be between 0 and 6.");
        }
        this.data.set(dayIndex, newData);
    }
    public void tampilkanData() {
        System.out.println("Statistik Mingguan:");
        for (int i = 0; i < data.size(); i++) {
            System.out.println("Hari ke-" + (i + 1) + ":");
            DataTidur dt = data.get(i);
            if (dt != null) {
                System.out.println("Waktu Mulai: " + dt.getWaktuMulai());
                System.out.println("Waktu Selesai: " + dt.getWaktuSelesai());
                System.out.println("Durasi: " + dt.getDurasi());
                System.out.println("Skor: " + dt.getSkor());
            } else {
                System.out.println("Data tidak tersedia.");
            }
            System.out.println();
        }
    }
}