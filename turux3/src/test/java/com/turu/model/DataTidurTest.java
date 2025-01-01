package com.turu.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
    public void testRemajaUnderSleep() {
        // Path 1: Age = 16, Durasi = 6 Jam
        LocalDateTime waktuMulai = LocalDateTime.of(2024, 12, 31, 23, 0);    // 23:00
        LocalDateTime waktuSelesai = LocalDateTime.of(2025, 1, 1, 5, 0);     // 05:00
        dataTidur.setWaktuMulai(waktuMulai);
        dataTidur.setWaktuSelesai(waktuSelesai);
        dataTidur.hitungDurasi();

        dataTidur.hitungSkor(16);
        assertTrue(dataTidur.getSkor() < 100);
        assertEquals(58, dataTidur.getSkor());
    }

    @Test
    public void testRemajaIdealSleep() {
        // Path 2: Age = 16, Durasi = 8 Jam
        LocalDateTime waktuMulai = LocalDateTime.of(2024, 12, 31, 22, 0);    // 22:00
        LocalDateTime waktuSelesai = LocalDateTime.of(2025, 1, 1, 6, 0);     // 06:00
        dataTidur.setWaktuMulai(waktuMulai);
        dataTidur.setWaktuSelesai(waktuSelesai);
        dataTidur.hitungDurasi();

        dataTidur.hitungSkor(16);

        assertEquals(100, dataTidur.getSkor());
    }

    @Test
    public void testRemajaOverSleep() {
        // Path 3: Age = 16, Durasi = 11 Jam
        LocalDateTime waktuMulai = LocalDateTime.of(2024, 12, 31, 21, 0);    // 21:00
        LocalDateTime waktuSelesai = LocalDateTime.of(2025, 1, 1, 8, 0);     // 08:00
        dataTidur.setWaktuMulai(waktuMulai);
        dataTidur.setWaktuSelesai(waktuSelesai);
        dataTidur.hitungDurasi();

        dataTidur.hitungSkor(16);

        assertTrue(dataTidur.getSkor() < 100);
        assertEquals(79, dataTidur.getSkor());
    }

    @Test
    public void testDewasaUnderSleep() {
        // Path 4: Age = 24, Durasi = 6 Jam
        LocalDateTime waktuMulai = LocalDateTime.of(2024, 12, 31, 23, 0);    // 23:00
        LocalDateTime waktuSelesai = LocalDateTime.of(2025, 1, 1, 5, 0);     // 05:00
        dataTidur.setWaktuMulai(waktuMulai);
        dataTidur.setWaktuSelesai(waktuSelesai);
        dataTidur.hitungDurasi();

        dataTidur.hitungSkor(24);

        assertTrue(dataTidur.getSkor() < 100);
        assertEquals(79, dataTidur.getSkor());
    }

    @Test
    public void testDewasaIdealSleep() {
        // Path 5: Age = 24, Durasi = 8 Jam
        LocalDateTime waktuMulai = LocalDateTime.of(2024, 12, 31, 22, 0);    // 22:00
        LocalDateTime waktuSelesai = LocalDateTime.of(2025, 1, 1, 6, 0);     // 06:00
        dataTidur.setWaktuMulai(waktuMulai);
        dataTidur.setWaktuSelesai(waktuSelesai);
        dataTidur.hitungDurasi();

        dataTidur.hitungSkor(24);

        assertEquals(100, dataTidur.getSkor());
    }

    @Test
    public void testDewasaOverSleep() {
        // Path 6: Age = 24, Durasi = 11 Jam
        LocalDateTime waktuMulai = LocalDateTime.of(2024, 12, 31, 21, 0);    // 21:00
        LocalDateTime waktuSelesai = LocalDateTime.of(2025, 1, 1, 8, 0);     // 08:00
        dataTidur.setWaktuMulai(waktuMulai);
        dataTidur.setWaktuSelesai(waktuSelesai);
        dataTidur.hitungDurasi();

        dataTidur.hitungSkor(24);

        assertTrue(dataTidur.getSkor() < 100);
    }

    @Test
    public void testLansiaUnderSleep() {
        // Path 7: Age = 65, Durasi = 6 Jam
        LocalDateTime waktuMulai = LocalDateTime.of(2024, 12, 31, 23, 0);    // 23:00
        LocalDateTime waktuSelesai = LocalDateTime.of(2025, 1, 1, 5, 0);     // 05:00
        dataTidur.setWaktuMulai(waktuMulai);
        dataTidur.setWaktuSelesai(waktuSelesai);
        dataTidur.hitungDurasi();

        dataTidur.hitungSkor(65);

        assertTrue(dataTidur.getSkor() < 100);
    }

    @Test
    public void testLansiaIdealSleep() {
        // Path 8: Age = 65, Durasi = 7 Jam
        LocalDateTime waktuMulai = LocalDateTime.of(2024, 12, 31, 22, 0);    // 22:00
        LocalDateTime waktuSelesai = LocalDateTime.of(2025, 1, 1, 5, 0);     // 05:00
        dataTidur.setWaktuMulai(waktuMulai);
        dataTidur.setWaktuSelesai(waktuSelesai);
        dataTidur.hitungDurasi();

        dataTidur.hitungSkor(65);

        assertEquals(100, dataTidur.getSkor());
    }

    @Test
    public void testLansiaOverSleep() {
        // Path 9: Age = 65, Durasi = 11 Jam
        LocalDateTime waktuMulai = LocalDateTime.of(2024, 12, 31, 21, 0);    // 21:00
        LocalDateTime waktuSelesai = LocalDateTime.of(2025, 1, 1, 8, 0);     // 08:00
        dataTidur.setWaktuMulai(waktuMulai);
        dataTidur.setWaktuSelesai(waktuSelesai);
        dataTidur.hitungDurasi();

        dataTidur.hitungSkor(65);

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
        assertThrows(IllegalArgumentException.class, () -> dataTidur.hitungSkor(10));
    }
}
