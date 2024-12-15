package com.turu.controllers;

import com.turu.service.DataTidurService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class BerandaController {
	// Define the button's state
	private String state = "tidur"; // Initial state (tidur, tidur-active, bangun)

	private final DataTidurService dataTidurService;

	public BerandaController(DataTidurService dataTidurService) {
		this.dataTidurService = dataTidurService;
	}

	@GetMapping("/beranda")
	public String beranda(Model model) {
		// Pass the current state to the frontend
		model.addAttribute("buttonState", state);
		model.addAttribute("buttonLabel", getLabelForState(state));
		model.addAttribute("buttonClass", getClassForState(state));
		model.addAttribute("buttonIcon", getIconForState(state));

		// Statistik tidur pengguna mingguan
		Long penggunaId = 1L; // Gantilah ini sesuai ID pengguna login
		Map<String, Integer> sleepStats = dataTidurService.getWeeklySleepStats(penggunaId);
		model.addAttribute("sleepStats", sleepStats);
		
		return "beranda"; // Thymeleaf template
	}

	@PostMapping("/beranda/toggle-button")
	public String toggleButton() throws InterruptedException {
		// Transition logic for the button
		switch (state) {
			case "tidur":
				state = "tidur-active";
				Thread.sleep(3000); // Simulate delay
				state = "bangun";
				break;
			case "bangun":
				state = "tidur";
				break;
		}
		return "redirect:/beranda"; // Redirect to refresh the page
	}

	// Helper methods for button attributes
	private String getLabelForState(String state) {
		switch (state) {
			case "tidur":
				return "Tombol Tidur";
			case "tidur-active":
				return "Tombol Tidur";
			case "bangun":
				return "Tombol Bangun";
			default:
				return "";
		}
	}

	private String getClassForState(String state) {
		switch (state) {
			case "tidur":
				return "lilac";
			case "tidur-active":
				return "purple";
			case "bangun":
				return "purple";
			default:
				return "";
		}
	}

	private String getIconForState(String state) {
		switch (state) {
			case "tidur":
				return "bi-moon-fill";
			case "tidur-active":
				return "bi-moon-stars-fill";
			case "bangun":
				return "bi-sunrise-fill";
			default:
				return "";
		}
	}
}
