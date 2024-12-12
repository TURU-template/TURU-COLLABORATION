package com.turu.model;

import java.time.LocalDate;

import jakarta.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
public class Pengguna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idAnggota;
    private String username;
    private String password;
    private String jk;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalLahir;

    // Getters and Setters
    public int getIdAnggota() {
        return idAnggota;
    }

    public void setIdAnggota(int idAnggota) {
        this.idAnggota = idAnggota;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJk() {
        return jk;
    }

    public void setJk(String jk) {
        this.jk = jk;
    }

    public LocalDate getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(LocalDate tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }
}
