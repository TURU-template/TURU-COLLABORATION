package com.turu.service;

import com.turu.model.Pengguna;
import com.turu.repository.PenggunaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

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

    public Optional<Pengguna> findByUsername(String username) {
        return penggunaRepository.findByUsername(username);
    }

    public void savePengguna(Pengguna pengguna) {
        pengguna.setPassword(passwordEncoder.encode(pengguna.getPassword()));
        penggunaRepository.save(pengguna);
    }

    // private boolean isValidUsername(String username) {
    //     // Username tidak boleh null, kosong, atau mengandung spasi
    //     return username != null && !username.trim().isEmpty() && !username.contains(" ");
    // }

    // private boolean isValidPassword(String password) {
    //     // Password harus minimal 4 karakter
    //     return password != null && password.length() >= 4;
    // }

    // private boolean isValidAge(LocalDate birthdate) {
    //     if (birthdate == null) {
    //         return false; // Tanggal lahir harus diisi
    //     }
    //     LocalDate today = LocalDate.now();
    //     Period age = Period.between(birthdate, today);
    //     return age.getYears() >= 13;
    // }

    public String login(String username, String password) {
        Optional<Pengguna> penggunaOptional = penggunaRepository.findByUsername(username);

        if (penggunaOptional.isEmpty()) {
            return "Username tidak ditemukan."; // If no user is found
        }

        Pengguna pengguna = penggunaOptional.get();
        if (!passwordEncoder.matches(password, pengguna.getPassword())) {
            return "Username dan password tidak cocok."; // If password does not match
        }

        return "Login berhasil."; // If login is successful
    }

}
