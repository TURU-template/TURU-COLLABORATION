package com.turu.repository;

import java.time.LocalDate;
import com.turu.model.DataTidur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DataTidurRepository extends JpaRepository<DataTidur, Long> {
    List<DataTidur> findByPenggunaIdAndTanggalBetween(Long penggunaId, LocalDate startDate, LocalDate endDate);
}
