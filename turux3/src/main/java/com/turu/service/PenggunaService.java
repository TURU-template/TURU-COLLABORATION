package com.turu.service;

import com.turu.model.Pengguna;
import com.turu.repository.PenggunaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PenggunaService {

    private final PenggunaRepository penggunaRepository;
    private final PasswordEncoder passwordEncoder;

    public PenggunaService(PenggunaRepository penggunaRepository, PasswordEncoder passwordEncoder) {
        this.penggunaRepository = penggunaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Pengguna> getAllPengguna() {
        return penggunaRepository.findAll();
    }

    public void savePengguna(Pengguna pengguna) {
        // Validasi username
        if (!isValidUsername(pengguna.getUsername())) {
            throw new IllegalArgumentException("Username tidak boleh kosong dan tidak boleh mengandung spasi.");
        }

        // Validasi password
        if (!isValidPassword(pengguna.getPassword())) {
            throw new IllegalArgumentException("Password harus memiliki panjang minimal 4 karakter.");
        }
        pengguna.setPassword(passwordEncoder.encode(pengguna.getPassword()));
        penggunaRepository.save(pengguna);
    }

    private boolean isValidUsername(String username) {
        // Username tidak boleh null, kosong, atau mengandung spasi
        return username != null && !username.trim().isEmpty() && !username.contains(" ");
    }

    private boolean isValidPassword(String password) {
        // Password harus minimal 4 karakter
        return password != null && password.length() >= 4;
    }
}
