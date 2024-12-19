package com.turu.repository;

import com.turu.model.DataTidur;
import com.turu.model.Pengguna;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DataTidurRepository extends JpaRepository<DataTidur, Long> {
    List<DataTidur> findByTanggalBetween(LocalDate startDate, LocalDate endDate);

    // Find data tidur terbaru dari pengguna
    @Query("SELECT d FROM DataTidur d WHERE d.pengguna = :pengguna ORDER BY d.waktuMulai DESC  LIMIT 1")
    DataTidur findTopByPenggunaOrderByWaktuMulaiDesc(@Param("pengguna") Pengguna pengguna);
}
