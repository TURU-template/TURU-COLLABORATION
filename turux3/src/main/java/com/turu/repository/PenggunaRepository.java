package com.turu.repository;

import com.turu.model.Pengguna;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PenggunaRepository extends JpaRepository<Pengguna, Integer> {

}
