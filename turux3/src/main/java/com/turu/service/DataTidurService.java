package com.turu.service;

import java.time.LocalDateTime;


import com.turu.model.DataTidur;
import com.turu.repository.DataTidurRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
public class DataTidurService {
    DataTidur dt = new DataTidur();

    private DataTidurRepository dataTidurRepository;
    public DataTidurService(DataTidurRepository dataTidurRepository) {
        this.dataTidurRepository = dataTidurRepository;
    }
    public void addStart(LocalDateTime time){
        dt.setWaktuMulai(time);
        dt.setTanggal(time.toLocalDate().plusDays(1));
        dataTidurRepository.save(dt);
    }
    public List<DataTidur> getDataTidurMingguan(LocalDate startDate, LocalDate endDate) {
        return dataTidurRepository.findByTanggalBetween(startDate, endDate);
    }
}
