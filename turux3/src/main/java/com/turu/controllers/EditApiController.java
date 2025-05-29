// EditApiController.java
package com.turu.controllers;

import com.turu.model.Pengguna;
import com.turu.repository.PenggunaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class EditApiController {

    @Autowired
    private PenggunaRepository penggunaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PutMapping("/pengguna/{id}/edit-nama")
    public ResponseEntity<?> editNama(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        Optional<Pengguna> penggunaOpt = penggunaRepository.findById(id);
        if (penggunaOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Pengguna tidak ditemukan"));
        }

        String usernameBaru = payload.get("username");
        if (usernameBaru == null || usernameBaru.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username tidak boleh kosong"));
        }

        Pengguna pengguna = penggunaOpt.get();
        pengguna.setUsername(usernameBaru);
        penggunaRepository.save(pengguna);

        return ResponseEntity.ok(Map.of("message", "Username berhasil diperbarui"));
    }

    @PutMapping("/pengguna/{id}/edit-password")
    public ResponseEntity<?> editPassword(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        Optional<Pengguna> penggunaOpt = penggunaRepository.findById(id);
        if (penggunaOpt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Pengguna tidak ditemukan"));
        }

        String oldPassword = payload.get("oldPassword");
        String newPassword = payload.get("newPassword");

        if (oldPassword == null || newPassword == null || oldPassword.isBlank() || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Password lama dan baru harus diisi"));
        }

        Pengguna pengguna = penggunaOpt.get();
        if (!passwordEncoder.matches(oldPassword, pengguna.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Password lama salah"));
        }

        pengguna.setPassword(passwordEncoder.encode(newPassword));
        penggunaRepository.save(pengguna);

        return ResponseEntity.ok(Map.of("message", "Password berhasil diperbarui"));
    }
}