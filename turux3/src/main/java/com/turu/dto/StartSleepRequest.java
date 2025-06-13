// src/main/java/com/turu/dto/StartSleepRequest.java
package com.turu.dto;

import java.time.LocalDateTime;

public class StartSleepRequest {
    private Integer userId;
    private LocalDateTime startTime; // Waktu mulai tidur

    // Konstruktor kosong
    public StartSleepRequest() {
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
}