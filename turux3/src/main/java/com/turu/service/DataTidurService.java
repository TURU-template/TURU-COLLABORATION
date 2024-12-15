package com.turu.service;

import com.turu.model.DataTidur;
import com.turu.repository.DataTidurRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataTidurService {

    private final DataTidurRepository dataTidurRepository;

    public DataTidurService(DataTidurRepository dataTidurRepository) {
        this.dataTidurRepository = dataTidurRepository;
    }

    public Map<String, Integer> getWeeklySleepStats(Long penggunaId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(6); // Mulai dari 6 hari sebelum hari ini
        List<DataTidur> dataTidurList = dataTidurRepository.findByPenggunaIdAndTanggalBetween(penggunaId, startOfWeek, today);

        // Inisialisasi map untuk menyimpan statistik tidur
        Map<String, Integer> sleepStats = new LinkedHashMap<>();
        String[] days = {"S", "S", "R", "K", "J", "S", "M"}; // Hari dalam seminggu
        for (String day : days) {
            sleepStats.put(day, 0); // Default value = 0
        }

        // Mapping data tidur ke hari dalam seminggu
        for (DataTidur data : dataTidurList) {
            String dayName = days[data.getTanggal().getDayOfWeek().getValue() % 7];
            int durationInMinutes = data.getDurasi().getHour() * 60 + data.getDurasi().getMinute();
            sleepStats.put(dayName, durationInMinutes);
        }

        return sleepStats;
    }
}
