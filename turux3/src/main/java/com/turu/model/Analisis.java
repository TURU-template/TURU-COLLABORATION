package com.turu.model;

import java.time.Duration;
import java.time.LocalTime; // Import LocalTime

public class Analisis {
    // Field yang sudah ada
    private double rataWaktuBangun; // Ini sekarang rata-rata durasi tidur dalam detik
    private double rataSkorTidur;
    private Duration rentangTidur; // Ini bisa menjadi rentang total dari data yang dianalisis

    // Field baru untuk analisis data tidak cukup
    private String message;
    private Integer requiredData;
    private Integer currentData;

    // Field baru untuk hasil analisis detail
    private String mascot;
    private String mascotName;
    private String mascotDescription;
    private String suggestion;

    // Field baru untuk rata-rata waktu tidur/bangun
    private LocalTime averageSleepTime;
    private LocalTime averageWakeTime;

    // Field baru untuk variabilitas/konsistensi
    private Duration sleepTimeVariability;
    private Duration wakeTimeVariability;
    private Boolean isSleepTimeConsistent; // Menggunakan Boolean agar bisa null jika tidak dihitung
    private Boolean isWakeTimeConsistent;  // Menggunakan Boolean agar bisa null jika tidak dihitung


    // Constructor (opsional, bisa ditambahkan jika ingin inisialisasi awal)
    public Analisis() {
        // Default constructor untuk Spring
    }

    // --- Getter dan Setter untuk field yang sudah ada ---
    public double getRataWaktuBangun() {
        return rataWaktuBangun;
    }

    public void setRataWaktuBangun(double rataWaktuBangun) {
        this.rataWaktuBangun = rataWaktuBangun;
    }

    public double getRataSkorTidur() {
        return rataSkorTidur;
    }

    public void setRataSkorTidur(double rataSkorTidur) {
        this.rataSkorTidur = rataSkorTidur;
    }

    public Duration getRentangTidur() {
        return rentangTidur;
    }

    public void setRentangTidur(Duration rentangTidur) {
        this.rentangTidur = rentangTidur;
    }

    // --- Getter dan Setter untuk field baru (message dan data count) ---
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getRequiredData() {
        return requiredData;
    }

    public void setRequiredData(Integer requiredData) {
        this.requiredData = requiredData;
    }

    public Integer getCurrentData() {
        return currentData;
    }

    public void setCurrentData(Integer currentData) {
        this.currentData = currentData;
    }

    // --- Getter dan Setter untuk field baru (mascot, suggestion, dll.) ---
    public String getMascot() {
        return mascot;
    }

    public void setMascot(String mascot) {
        this.mascot = mascot;
    }

    public String getMascotName() {
        return mascotName;
    }

    public void setMascotName(String mascotName) {
        this.mascotName = mascotName;
    }

    public String getMascotDescription() {
        return mascotDescription;
    }

    public void setMascotDescription(String mascotDescription) {
        this.mascotDescription = mascotDescription;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    // --- Getter dan Setter untuk field baru (rata-rata waktu) ---
    public LocalTime getAverageSleepTime() {
        return averageSleepTime;
    }

    public void setAverageSleepTime(LocalTime averageSleepTime) {
        this.averageSleepTime = averageSleepTime;
    }

    public LocalTime getAverageWakeTime() {
        return averageWakeTime;
    }

    public void setAverageWakeTime(LocalTime averageWakeTime) {
        this.averageWakeTime = averageWakeTime;
    }

    // --- Getter dan Setter untuk field baru (variabilitas/konsistensi) ---
    public Duration getSleepTimeVariability() {
        return sleepTimeVariability;
    }

    public void setSleepTimeVariability(Duration sleepTimeVariability) {
        this.sleepTimeVariability = sleepTimeVariability;
    }

    public Duration getWakeTimeVariability() {
        return wakeTimeVariability;
    }

    public void setWakeTimeVariability(Duration wakeTimeVariability) {
        this.wakeTimeVariability = wakeTimeVariability;
    }

    public Boolean getIsSleepTimeConsistent() {
        return isSleepTimeConsistent;
    }

    public void setIsSleepTimeConsistent(Boolean isSleepTimeConsistent) {
        this.isSleepTimeConsistent = isSleepTimeConsistent;
    }

    public Boolean getIsWakeTimeConsistent() {
        return isWakeTimeConsistent;
    }

    public void setIsWakeTimeConsistent(Boolean isWakeTimeConsistent) {
        this.isWakeTimeConsistent = isWakeTimeConsistent;
    }
}