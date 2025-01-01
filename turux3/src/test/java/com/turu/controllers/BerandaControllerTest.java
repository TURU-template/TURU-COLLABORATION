package com.turu.controllers;

import com.turu.model.DataTidur;
import com.turu.model.Pengguna;
import com.turu.repository.PenggunaRepository;
import com.turu.service.DataTidurService;
import com.turu.service.PenggunaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@WebMvcTest(BerandaController.class)
class BerandaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataTidurService dataTidurService;

    @MockBean
    private PenggunaService penggunaService;

    @MockBean
    private PenggunaRepository penggunaRepository;

    private Pengguna pengguna;

    @BeforeEach
    void setUp() {
        pengguna = new Pengguna();
        pengguna.setUsername("test2");
        pengguna.setState(false);
        pengguna.setTanggalLahir(ZonedDateTime.now(ZoneId.of("Asia/Jakarta")).minusYears(25).toLocalDate());
    }

    @Test
    @WithMockUser(username = "test2", password = "test2")
    void testBerandaView() throws Exception {
        when(penggunaService.findByUsername("test2")).thenReturn(Optional.of(pengguna));

        ResultActions result = mockMvc.perform(get("/beranda"))
                .andExpect(status().isOk())
                .andExpect(view().name("beranda"))
                .andExpect(model().attributeExists("state", "mascot", "mascotName", "mascotDescription", "sleepScores"));

        // Additional assertions
        result.andExpect(model().attribute("state", false));
    }

    @Test
    @WithMockUser(username = "test2", password = "test2")
    void testAddStart() throws Exception {
        // Mocking the dependencies
        Pengguna pengguna = new Pengguna();
        pengguna.setUsername("test2");
        when(penggunaService.findByUsername("test2")).thenReturn(Optional.of(pengguna));
    
        // Serialize LocalDateTime to match expected JSON format
        String startTimeJson = "\"2025-01-01T10:00:00\"";
    
        // Perform the POST request with CSRF token
        mockMvc.perform(post("/api/add-start")
                        .with(csrf()) // Adds CSRF token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(startTimeJson))
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(content().string("Start time added successfully!")); // Verify response
    
        // Verify the service interaction
        Mockito.verify(dataTidurService).addStart(any(LocalDateTime.class), eq(pengguna));
        Mockito.verify(penggunaRepository).save(pengguna);
    }

    @Test
    @WithMockUser(username = "test2", password = "test2")
    void testAddEnd() throws Exception {
        Pengguna pengguna = new Pengguna();
        pengguna.setUsername("test2");
        pengguna.setState(true);
    
        DataTidur dataTidur = new DataTidur();
    
        // Mocking dependencies
        when(penggunaService.findByUsername("test2")).thenReturn(Optional.of(pengguna));
        when(dataTidurService.cariTerbaruDataTidur(pengguna)).thenReturn(dataTidur);
        when(dataTidurService.addEnd(any(LocalDateTime.class), eq(pengguna))).thenReturn(true);
    
        // Perform the request
        mockMvc.perform(post("/api/add-end")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"endTime\": \"2025-01-01T12:00:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("End time and duration calculated successfully!"))
                .andExpect(jsonPath("$.isDeleted").value(true));
    
        // Verify interactions
        Mockito.verify(dataTidurService).addEnd(any(LocalDateTime.class), eq(pengguna));
        Mockito.verify(penggunaRepository).save(pengguna);
    }

    @Test
    @WithMockUser(username = "test2", password = "test2")
    void testGetSessionData() throws Exception {
        DataTidur ongoingSession = new DataTidur();
        ongoingSession.setWaktuMulai(LocalDateTime.now());

        pengguna.setState(true);
        when(penggunaService.findByUsername("test2")).thenReturn(Optional.of(pengguna));
        when(dataTidurService.cariTerbaruDataTidur(any(Pengguna.class))).thenReturn(ongoingSession);

        mockMvc.perform(get("/api/get-session-data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value(true))
                .andExpect(jsonPath("$.startTime").exists());
    }

    @Test
    @WithMockUser(username = "test2")
    void testTambahTidur_SuccessfulAddition() throws Exception {
    Pengguna pengguna = new Pengguna();
    pengguna.setUsername("test2");

    DataTidur dataTidur = new DataTidur();
    dataTidur.setWaktuSelesai(LocalDateTime.now());

    when(penggunaService.findByUsername("test2")).thenReturn(Optional.of(pengguna));
    when(dataTidurService.cekDuplikatDataTidur(any(DataTidur.class), eq(pengguna))).thenReturn(false);

    mockMvc.perform(post("/tambahTidur")
                    .param("waktuMulai", "2025-01-01T10:00")
                    .param("waktuSelesai", "2025-01-01T12:00")
                    .flashAttr("dataTidur", dataTidur))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/beranda"))
            .andExpect(flash().attribute("message", "Data tidur berhasil ditambahkan"))
            .andExpect(flash().attribute("openModal", false));

    verify(dataTidurService).tambah(any(DataTidur.class), eq(pengguna));
    }

    @Test
    @WithMockUser(username = "test2")
    void testTambahTidur_DuplicateData() throws Exception {
    Pengguna pengguna = new Pengguna();
    pengguna.setUsername("test2");

    DataTidur dataTidur = new DataTidur();
    dataTidur.setWaktuSelesai(LocalDateTime.of(2025, 1, 1, 12, 0));

    when(penggunaService.findByUsername("test2")).thenReturn(Optional.of(pengguna));
    when(dataTidurService.cekDuplikatDataTidur(any(DataTidur.class), eq(pengguna))).thenReturn(true);

    mockMvc.perform(post("/tambahTidur")
                    .param("waktuMulai", "2025-01-01T10:00")
                    .param("waktuSelesai", "2025-01-01T12:00")
                    .flashAttr("dataTidur", dataTidur))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/beranda"))
            .andExpect(flash().attribute("message", "Anda telah memiliki data tidur pada tanggal 2025-01-01"))
            .andExpect(flash().attribute("openModal", true))
            .andExpect(flash().attribute("dataTidur", dataTidur));

    verify(dataTidurService, never()).tambah(any(DataTidur.class), eq(pengguna));
    }

    @Test
    @WithMockUser(username = "test2")
    void testEditTidur_Success() throws Exception {
    Pengguna pengguna = new Pengguna();
    pengguna.setUsername("test2");

    DataTidur dataTidur = new DataTidur();
    dataTidur.setWaktuMulai(LocalDateTime.of(2025, 1, 1, 10, 0));
    dataTidur.setWaktuSelesai(LocalDateTime.of(2025, 1, 1, 12, 0));

    when(penggunaService.findByUsername("test2")).thenReturn(Optional.of(pengguna));
    when(dataTidurService.cariTerbaruDataTidur(pengguna)).thenReturn(dataTidur);

    mockMvc.perform(post("/editTidur")
                    .param("waktuMulai", "2025-01-01T09:00")
                    .param("waktuSelesai", "2025-01-01T11:00"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/beranda"));

    verify(dataTidurService).update(argThat(updatedDataTidur -> 
            updatedDataTidur.getWaktuMulai().equals(LocalDateTime.of(2025, 1, 1, 9, 0)) &&
            updatedDataTidur.getWaktuSelesai().equals(LocalDateTime.of(2025, 1, 1, 11, 0))
    ), eq(pengguna));
    }

}
