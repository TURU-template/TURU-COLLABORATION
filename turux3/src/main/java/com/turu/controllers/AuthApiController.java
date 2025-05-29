package com.turu.controllers;

import com.turu.model.LoginRequest;
import com.turu.model.LoginResponse;
import com.turu.model.Pengguna;
import com.turu.repository.PenggunaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api") // endpoint menjadi /api/login
public class AuthApiController {

    @Autowired
    private PenggunaRepository penggunaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Pengguna pengguna = penggunaRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (pengguna == null || !passwordEncoder.matches(request.getPassword(), pengguna.getPassword())) {
            // Jika pengguna tidak ditemukan atau password tidak cocok
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Username atau password salah"));
        }

        LoginResponse response = new LoginResponse(pengguna.getId(), pengguna.getUsername());
        return ResponseEntity.ok(response);
    }
}
