package com.turu.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;

import com.turu.model.DataTidur;
import com.turu.repository.DataTidurRepository;
public class DataTidurService {
    DataTidur dt = new DataTidur();
    @Autowired
    private DataTidurRepository dataTidurRepository;
    public void addStart(LocalDateTime time){
        dt.setWaktuMulai(time);
        dt.setTanggal(time.toLocalDate().plusDays(1));
        dataTidurRepository.save(dt);
    }
    public List<DataTidur> getDataTidurMingguan(LocalDate startDate, LocalDate endDate) {
        return dataTidurRepository.findByTanggalBetween(startDate, endDate);
    }
}
