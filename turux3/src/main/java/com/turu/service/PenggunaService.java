package com.turu.service;

import com.turu.model.Pengguna;
import com.turu.repository.PenggunaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PenggunaService {

    private final PenggunaRepository penggunaRepository;

    public PenggunaService(PenggunaRepository penggunaRepository) {
        this.penggunaRepository = penggunaRepository;
    }

    public List<Pengguna> getAllPengguna() {
        return penggunaRepository.findAll();
    }

    public void savePengguna(Pengguna pengguna) {
        penggunaRepository.save(pengguna);
    }
}
