package com.turu.controllers;

import com.turu.dto.LoginRequest;
import com.turu.dto.LoginResponse; // Pastikan ini tidak punya 'token' field
import com.turu.model.Pengguna;
import com.turu.repository.PenggunaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder; // <--- TETAPKAN INI
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api") // Semua endpoint di controller ini akan diawali /api/
public class AuthApiController {

    @Autowired
    private PenggunaRepository penggunaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // <--- TETAPKAN INI

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Pengguna pengguna = penggunaRepository.findByUsername(request.getUsername())
                .orElse(null);

        // Membandingkan password yang di-submit dengan password terenkripsi di DB
        // Ini HANYA BEKERJA jika Anda mempertahankan PasswordEncoder dan password di DB sudah di-hash
        if (pengguna == null || !passwordEncoder.matches(request.getPassword(), pengguna.getPassword())) {
            // Jika pengguna tidak ditemukan atau password tidak cocok
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Username atau password salah"));
        }

        // LoginResponse tanpa token (sudah dikonfirmasi bahwa Anda tidak menginginkan token)
        LoginResponse response = new LoginResponse(pengguna.getId(), pengguna.getUsername(), pengguna.getJk(), pengguna.getTanggalLahir());
        System.out.println(response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");
        String jk = payload.get("jk");
        String tanggalLahirStr = payload.get("tanggal_lahir");

        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username dan password wajib diisi"));
        }

        Optional<Pengguna> existingUser = penggunaRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "Username sudah digunakan"));
        }

        Pengguna pengguna = new Pengguna();
        pengguna.setUsername(username);
        // Enkripsi password sebelum disimpan ke DB
        pengguna.setPassword(passwordEncoder.encode(password)); // <--- TETAPKAN INI


        if (jk != null && (jk.equalsIgnoreCase("L") || jk.equalsIgnoreCase("P"))) {
            pengguna.setJk(jk);
        }

        if (tanggalLahirStr != null && !tanggalLahirStr.isBlank()) {
            try {
                pengguna.setTanggalLahir(LocalDate.parse(tanggalLahirStr));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Format tanggal_lahir tidak valid (YYYY-MM-DD)"));
            }
        }

        penggunaRepository.save(pengguna);

        return ResponseEntity.ok(Map.of("message", "Registrasi berhasil"));
    }
}