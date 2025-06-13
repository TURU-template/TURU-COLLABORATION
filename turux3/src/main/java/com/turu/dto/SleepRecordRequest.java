// src/main/java/com/turu/dto/SleepRecordRequest.java
package com.turu.dto;

import java.time.LocalDateTime;

public class SleepRecordRequest {
    private Integer userId; // ID pengguna
    private LocalDateTime startTime; // Waktu mulai tidur
    private LocalDateTime endTime;   // Waktu bangun

    // Konstruktor kosong
    public SleepRecordRequest() {
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}