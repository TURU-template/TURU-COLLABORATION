package com.turu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

//import jakarta.annotation.PostConstruct;

@Component
public class DatabaseInitializer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @jakarta.annotation.PostConstruct
    public void initializeDatabase() {
        System.out.println("DatabaseInitializer: Starting initialization...");
        try {
            // Check if the database 'dbturu' exists
            String checkDatabaseQuery = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = 'dbturu'";
            Integer databaseCount = jdbcTemplate.queryForObject(checkDatabaseQuery, Integer.class);

            System.out.println("Database count: " + databaseCount);

            if (databaseCount == null || databaseCount == 0) {
                // Create the database and switch to it
                System.out.println("Database 'dbturu' does not exist. Creating...");
                jdbcTemplate.execute("CREATE DATABASE dbturu");
                jdbcTemplate.execute("USE dbturu");

                // Create the Pengguna table
                System.out.println("Creating Pengguna table...");
                jdbcTemplate.execute(
                        "CREATE TABLE Pengguna (" +
                        "idAnggota INT AUTO_INCREMENT PRIMARY KEY, " +
                        "username VARCHAR(50) NOT NULL, " +
                        "password VARCHAR(255) NOT NULL, " +
                        "jk CHAR(1) NOT NULL CHECK (jk IN ('L', 'P')), " +
                        "tanggalLahir DATE NOT NULL" +
                        ")");
                System.out.println("Pengguna table created.");

                // Insert initial data
                System.out.println("Inserting initial data into Pengguna...");
                jdbcTemplate.batchUpdate(
                        "INSERT INTO Pengguna (username, password, jk, tanggalLahir) VALUES " +
                        "('Valdez', 'valdezganteng', 'L', '2005-11-22'), " +
                        "('Iqbal', 'password456', 'P', '1985-05-15'), " +
                        "('Tiroy', 'password789', 'L', '2000-08-10'), " +
                        "('Audrey', 'password101', 'P', '1995-02-28'), " +
                        "('Aisyah', 'password202', 'P', '1970-12-01'), " +
                        "('Valdez', 'valdezganteng', 'L', '2005-11-22'), " +
                        "('Valdez', 'valdezganteng', 'L', '2005-11-23')");
                System.out.println("Data inserted.");
            } else {
                System.out.println("Database 'dbturu' already exists. Initialization skipped.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing the database: " + e.getMessage());
        }
    }
}
