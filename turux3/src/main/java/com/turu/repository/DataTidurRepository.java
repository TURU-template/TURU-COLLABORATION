// src/main/java/com/turu/repository/DataTidurRepository.java
package com.turu.repository;

import com.turu.model.DataTidur;
import com.turu.model.Pengguna; // Pastikan ini diimpor
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalDateTime; // Tambahkan ini jika metode menggunakan LocalDateTime
import java.util.List;
import java.util.Optional;

public interface DataTidurRepository extends JpaRepository<DataTidur, Long> {

    // Finds all DataTidur records within a date range (inclusive) for any user.
    // Biasanya ini akan mengembalikan List, bukan Optional.
    List<DataTidur> findByTanggalBetween(LocalDate startDate, LocalDate endDate);

    // Finds all DataTidur records for a specific Pengguna object. Returns an empty list if no records.
    List<DataTidur> findByPengguna(Pengguna pengguna); // Ini benar jika ingin query by Pengguna object

    // Finds the latest DataTidur record for a specific Pengguna object, ordered by waktuMulai descending.
    Optional<DataTidur> findTopByPenggunaOrderByWaktuMulaiDesc(Pengguna pengguna);
    
    // Finds the latest DataTidur record for a specific Pengguna ID, ordered by waktuMulai descending.
    // Ini adalah yang paling sering digunakan untuk query single latest record berdasarkan ID relasi.
    Optional<DataTidur> findTopByPengguna_IdOrderByWaktuMulaiDesc(Integer penggunaId); // Paling efisien untuk ID

    // Finds the top 7 latest DataTidur records for a specific Pengguna ID, ordered by waktuMulai descending.
    Optional<DataTidur> findTop7ByPengguna_IdOrderByWaktuMulaiDesc(Integer penggunaId); // Parameter type matches userId (Integer)

    // Counts DataTidur records for a specific Pengguna ID using a custom JPQL query.
    Long countByPengguna_Id(@Param("penggunaId") Integer penggunaId); // PERBAIKAN: Gunakan pengguna_Id di Query Annotation

    // Retrieves sleep scores by date for a specific Pengguna ID, ordered by tanggal.
    @Query("SELECT d.tanggal, d.skor FROM DataTidur d WHERE d.pengguna.id = :penggunaId ORDER BY d.tanggal")
    List<Object[]> findSkorByTanggalAndPenggunaId(@Param("penggunaId") Integer userId); // Tetap bagus, pakai _Id

    // Finds DataTidur records for a specific Pengguna object within a date range, ordered by date.
    List<DataTidur> findByPenggunaAndTanggalBetween(Pengguna pengguna, LocalDate startDate, LocalDate endDate);

    // === PERBAIKAN PENTING UNTUK RIWAYAT TIDUR LENGKAP ===
    // Mengembalikan semua DataTidur untuk pengguna tertentu, diurutkan dari yang terbaru ke terlama
    // berdasarkan waktuSelesai. Ini menggunakan ID pengguna untuk query yang jelas.
    List<DataTidur> findByPengguna_IdOrderByWaktuSelesaiDesc(Integer penggunaId);

    // === HAPUS METODE INI ===
    // static Optional<DataTidur> findLatestByUserId(Long userId) {
    //     throw new UnsupportedOperationException("Unimplemented method 'findLatestByUserId'");
    // }
}