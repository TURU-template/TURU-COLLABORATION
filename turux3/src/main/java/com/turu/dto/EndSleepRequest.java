// src/main/java/com/turu/dto/EndSleepRequest.java
package com.turu.dto;

import java.time.LocalDateTime;

public class EndSleepRequest {
    private Integer userId;
    private LocalDateTime endTime; // Waktu bangun

    // Konstruktor kosong
    public EndSleepRequest() {
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}