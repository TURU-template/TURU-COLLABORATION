package com.turu.repository;

import com.turu.model.DataTidur;
import com.turu.model.Pengguna;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.time.LocalDate;
import java.util.List;

public interface DataTidurRepository extends JpaRepository<DataTidur, Long> {

    // Finds all DataTidur records within a date range (inclusive).
    List<DataTidur> findByTanggalBetween(LocalDate startDate, LocalDate endDate);

    // Finds all DataTidur records for a specific Pengguna.
    // If no records are found, returns an empty list, not null.
    List<DataTidur> findByPengguna(Pengguna pengguna);

    // Finds the latest DataTidur record for a specific Pengguna, ordered by waktuMulai descending.
    // Spring Data JPA's naming convention 'findTop' or 'findFirst' handles the LIMIT 1 implicitly.
    // Optional is used because there might be no records for the user.
    Optional<DataTidur> findTopByPenggunaOrderByWaktuMulaiDesc(Pengguna pengguna);

    // Finds the top 7 latest DataTidur records for a specific Pengguna, ordered by waktuMulai descending.
    // Spring Data JPA's naming convention 'findTopN' handles the LIMIT N implicitly.
    List<DataTidur> findTop7ByPenggunaIdOrderByWaktuMulaiDesc(Integer penggunaId); // Parameter type matches userId (Integer)

    // Counts DataTidur records for a specific Pengguna ID using a custom JPQL query.
    // Parameter type matches userId (Integer) for consistency.
    @Query("SELECT COUNT(d) FROM DataTidur d WHERE d.pengguna.id = :penggunaId")
    Long countByPenggunaId(@Param("penggunaId") Integer penggunaId);

    // Retrieves sleep scores by date for a specific Pengguna ID, ordered by tanggal.
    // Parameter type matches userId (Integer) for consistency.
    @Query("SELECT d.tanggal, d.skor FROM DataTidur d WHERE d.pengguna.id = :penggunaId ORDER BY d.tanggal")
    List<Object[]> findSkorByTanggalAndPenggunaId(@Param("penggunaId") Integer userId);

    // Finds DataTidur records for a specific Pengguna within a date range, ordered by date.
    List<DataTidur> findByPenggunaAndTanggalBetween(Pengguna pengguna, LocalDate startDate, LocalDate endDate);

    // IMPORTANT: This method was missing and caused a compilation error in DataTidurService.
    // It's crucial for `DataTidurService.getDataTidurByPengguna()` to get sorted data directly from DB.
    List<DataTidur> findByPenggunaOrderByWaktuSelesaiDesc(Pengguna pengguna);
}