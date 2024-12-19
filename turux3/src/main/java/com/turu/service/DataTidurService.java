package com.turu.service;

import java.time.LocalDateTime;
import java.time.Period;

import com.turu.model.DataTidur;
import com.turu.model.Pengguna;
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
    public void addStart(LocalDateTime time, Pengguna pengguna ){
        dt.setWaktuMulai(time);
        dt.setIdPengguna(pengguna);
        dataTidurRepository.save(dt);
    }
    public List<DataTidur> getDataTidurMingguan(LocalDate startDate, LocalDate endDate) {
        return dataTidurRepository.findByTanggalBetween(startDate, endDate);
    }
    public DataTidur cariTerbaruDataTidur(Pengguna pengguna) {
        return dataTidurRepository.findTopByPenggunaOrderByWaktuMulaiDesc(pengguna);
    }
    public void addEnd(LocalDateTime time, Pengguna pengguna){
        DataTidur dt = cariTerbaruDataTidur(pengguna);
        dt.setWaktuSelesai(time);
        dt.setTanggal(time.toLocalDate());
        dt.hitungDurasi();
        dt.hitungSkor(Period.between(pengguna.getTanggalLahir(), LocalDate.now()).getYears());
        dataTidurRepository.save(dt);
    }
    public List<DataTidur> getAllDataTidur() {
        return dataTidurRepository.findAll();
    }
}
