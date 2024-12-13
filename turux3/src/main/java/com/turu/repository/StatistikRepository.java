package com.turu.repository;

import com.turu.model.Statistik;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatistikRepository extends JpaRepository<Statistik, Long> {
    Statistik findByPenggunaId(Long penggunaId);
}
