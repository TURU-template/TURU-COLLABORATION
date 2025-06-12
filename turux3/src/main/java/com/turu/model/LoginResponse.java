package com.turu.model;

import java.time.LocalDate;
import java.util.Map;

public class LoginResponse {
    private Integer id;
    private String username;
    private String jk;
    private LocalDate tanggalLahir;

    public LoginResponse() {}

    public LoginResponse(Integer id, String username, String jk, LocalDate tanggalLahir) {
        this.id = id;
        this.username = username;
        this.jk = jk;
        this.tanggalLahir = tanggalLahir;
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getJk() {
        return jk;
    }
    public void setJk(String jk){
        this.jk = jk;
    }
    public LocalDate getTanggalLahir(){
        return tanggalLahir;
    }
    public void setTanggalLahir(LocalDate tanggalLahir){
        this.tanggalLahir = tanggalLahir;
    }
}
