package com.turu.repository;

import com.turu.model.Pengguna;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PenggunaRepository extends JpaRepository<Pengguna, Integer> {

    Optional<Pengguna> findByUsername(String username);
}
