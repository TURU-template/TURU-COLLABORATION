package com.turu.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DataTidurTest {

    private DataTidur dataTidur;

    @BeforeEach
    public void setUp() {
        dataTidur = new DataTidur();
        // Set up objek Pengguna jika perlu
        Pengguna pengguna = new Pengguna();
        dataTidur.setIdPengguna(pengguna); // Pastikan Pengguna telah diset
    }

    @Test
    public void testHitungDurasi() {
        // Arrange: Set waktuMulai dan waktuSelesai
        LocalDateTime waktuMulai = LocalDateTime.of(2024, 12, 30, 22, 0); // 22:00
        LocalDateTime waktuSelesai = LocalDateTime.of(2024, 12, 31, 6, 0); // 06:00
        dataTidur.setWaktuMulai(waktuMulai);
        dataTidur.setWaktuSelesai(waktuSelesai);

        // Act: Hitung durasi
        dataTidur.hitungDurasi();

        // Assert: Durasi harusnya 8 jam
        assertEquals(LocalTime.of(8, 0), dataTidur.getDurasi());
    }

    @Test
    public void testHitungSkorUnderSleep() {
        // Arrange: Set waktuMulai dan waktuSelesai
        LocalDateTime waktuMulai = LocalDateTime.of(2024, 12, 1, 00, 0); // 22:00
        LocalDateTime waktuSelesai = LocalDateTime.of(2025, 1, 1, 5, 30); // 05:30
        dataTidur.setWaktuMulai(waktuMulai);
        dataTidur.setWaktuSelesai(waktuSelesai);
        dataTidur.hitungDurasi();

        // Act: Hitung skor untuk usia 30 (dewasa)
        dataTidur.hitungSkor(30);

        // Assert: Skor harus dihitung, di bawah 100
        assertTrue(dataTidur.getSkor() < 100);
    }

    @Test
    public void testHitungSkorOptimalSleep() {
        // Arrange: Set waktuMulai dan waktuSelesai
        LocalDateTime waktuMulai = LocalDateTime.of(2024, 12, 31, 22, 0); // 22:00
        LocalDateTime waktuSelesai = LocalDateTime.of(2025, 1, 1, 7, 0); // 07:00
        dataTidur.setWaktuMulai(waktuMulai);
        dataTidur.setWaktuSelesai(waktuSelesai);
        dataTidur.hitungDurasi();

        // Act: Hitung skor untuk usia 30 (dewasa)
        dataTidur.hitungSkor(30);

        // Assert: Skor harus mendekati 100
        assertEquals(100, dataTidur.getSkor());
    }

    @Test
    public void testHitungSkorOverSleep() {
        // Arrange: Set waktuMulai dan waktuSelesai
        LocalDateTime waktuMulai = LocalDateTime.of(2024, 12, 31, 22, 0); // 22:00
        LocalDateTime waktuSelesai = LocalDateTime.of(2025, 1, 1, 9, 30); // 09:30
        dataTidur.setWaktuMulai(waktuMulai);
        dataTidur.setWaktuSelesai(waktuSelesai);
        dataTidur.hitungDurasi();

        // Act: Hitung skor untuk usia 30 (dewasa)
        dataTidur.hitungSkor(30);

        // Assert: Skor harus dihitung, di bawah 100
        assertTrue(dataTidur.getSkor() < 100);
    }

    @Test
    public void testInvalidAge() {
        // Act & Assert: Test invalid age input
        LocalDateTime waktuMulai = LocalDateTime.of(2024, 12, 30, 22, 0); // 22:00
        LocalDateTime waktuSelesai = LocalDateTime.of(2024, 12, 31, 6, 0); // 06:00
        dataTidur.setWaktuMulai(waktuMulai);
        dataTidur.setWaktuSelesai(waktuSelesai);

        // Act: Hitung durasi
        dataTidur.hitungDurasi();
        dataTidur.hitungSkor(10);
    }
}
