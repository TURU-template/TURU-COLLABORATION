package com.turu.repository;

import com.turu.model.DataTidur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DataTidurRepository extends JpaRepository<DataTidur, Long> {
    List<DataTidur> findByPenggunaId(Long penggunaId);
}
