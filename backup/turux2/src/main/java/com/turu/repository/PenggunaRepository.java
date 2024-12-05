package com.turu.repository;

import com.turu.model.Pengguna;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PenggunaRepository {

    private final JdbcTemplate jdbcTemplate;

    public PenggunaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Pengguna> findAll() {
        String sql = "SELECT * FROM Pengguna";
        return jdbcTemplate.query(sql, new PenggunaRowMapper());
    }

    public void save(Pengguna pengguna) {
        String sql = "INSERT INTO Pengguna (username, password, jk, tanggalLahir) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, pengguna.getUsername(), pengguna.getPassword(), pengguna.getJk(), pengguna.getTanggalLahir());
    }

    static class PenggunaRowMapper implements RowMapper<Pengguna> {
        @Override
        public Pengguna mapRow(ResultSet rs, int rowNum) throws SQLException {
            Pengguna pengguna = new Pengguna();
            pengguna.setIdAnggota(rs.getInt("idAnggota"));
            pengguna.setUsername(rs.getString("username"));
            pengguna.setPassword(rs.getString("password"));
            pengguna.setJk(rs.getString("jk"));
            pengguna.setTanggalLahir(rs.getDate("tanggalLahir"));
            return pengguna;
        }
    }
}
