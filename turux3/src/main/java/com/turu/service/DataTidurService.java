package com.turu.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZonedDateTime;

import com.turu.model.DataTidur;
import com.turu.model.Pengguna;
import com.turu.repository.DataTidurRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.LocalDate;
import java.util.List;

@Service
public class DataTidurService {
    DataTidur dt = new DataTidur();
    ZoneId zone = ZoneId.of("Asia/Jakarta");
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

    public boolean addEnd(LocalDateTime time, Pengguna pengguna) {
        // Tambahkan 7 jam ke waktu selesai
        LocalDateTime adjustedTime = time.plusHours(7);

        DataTidur dt = cariTerbaruDataTidur(pengguna);
        dt.setWaktuSelesai(adjustedTime); // Gunakan waktu yang sudah diubah
        dt.setTanggal(adjustedTime.toLocalDate());
        dt.hitungDurasi();
        dt.hitungSkor(Period.between(pengguna.getTanggalLahir(), ZonedDateTime.now(zone).toLocalDate()).getYears());
        if (dt.getDurasi().isBefore(LocalTime.of(0, 15, 0))) {
            dataTidurRepository.deleteById(dt.getId());
            return true;
        } else {
            dataTidurRepository.save(dt);
            return false;
        }
        
    }
    public void tambah(DataTidur dt, Pengguna pengguna){
        dt.setIdPengguna(pengguna);
        dt.setTanggal(dt.getWaktuSelesai().toLocalDate());
        dt.hitungDurasi();
        dt.hitungSkor(Period.between(pengguna.getTanggalLahir(),  ZonedDateTime.now(zone).toLocalDate()).getYears());
        dataTidurRepository.save(dt);
    }
    public boolean cekDuplikatDataTidur(DataTidur dt, Pengguna pengguna){
        List<DataTidur> A = dataTidurRepository.findByPengguna(pengguna);
        for (DataTidur data : A){
            if (data.getTanggal().equals(dt.getWaktuSelesai().toLocalDate())){
                return true ;
            }
        }
        return false ;
    }
    public void update(DataTidur dt, Pengguna pengguna){
        dt.hitungDurasi();
        dt.hitungSkor(Period.between(pengguna.getTanggalLahir(),  ZonedDateTime.now(zone).toLocalDate()).getYears());
        dt.setTanggal(dt.getWaktuSelesai().toLocalDate());
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
