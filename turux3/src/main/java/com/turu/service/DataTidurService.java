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

    public void hapusSemuaDataTidur(Pengguna pengguna) {
        List<DataTidur> dataTidurList = dataTidurRepository.findByPengguna(pengguna);
        if (!dataTidurList.isEmpty()) {
            dataTidurRepository.deleteAll(dataTidurList);
        }
    }

    // TIDUR
    public void addStart(LocalDateTime time, Pengguna pengguna) {
        // Tambahkan 7 jam ke waktu mulai
        LocalDateTime adjustedTime = time.plusHours(7);

        dt.setWaktuMulai(adjustedTime); // Gunakan waktu yang sudah diubah
        dt.setIdPengguna(pengguna);
        dataTidurRepository.save(dt);
    }

    public void addEnd(LocalDateTime time, Pengguna pengguna) {
        // Tambahkan 7 jam ke waktu selesai
        LocalDateTime adjustedTime = time.plusHours(7);

        DataTidur dt = cariTerbaruDataTidur(pengguna);
        dt.setWaktuSelesai(adjustedTime); // Gunakan waktu yang sudah diubah
        dt.setTanggal(adjustedTime.toLocalDate());
        dt.hitungDurasi();
        dt.hitungSkor(Period.between(pengguna.getTanggalLahir(), LocalDate.now()).getYears());
        dataTidurRepository.save(dt);
    }

    // STATISTIK
    public List<DataTidur> getDataTidurMingguan(LocalDate startDate, LocalDate endDate, Pengguna pengguna) {
        return dataTidurRepository.findByPenggunaAndTanggalBetween(pengguna, startDate, endDate);
    }

    public DataTidur cariTerbaruDataTidur(Pengguna pengguna) {
        return dataTidurRepository.findTopByPenggunaOrderByWaktuMulaiDesc(pengguna);
    }

    public List<DataTidur> getAllDataTidur() {
        return dataTidurRepository.findAll();
    }

    public List<Object[]> getSkorByTanggalAndPengguna(Long penggunaId) {
        return dataTidurRepository.findSkorByTanggalAndPenggunaId(penggunaId);
    }
}
