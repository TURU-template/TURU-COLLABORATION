package com.turu.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.time.*;
import com.turu.service.DataTidurService;


@Controller
public class BerandaController {
	// Define the button's state
	private String state = "tidur"; // Initial state (tidur, tidur-active, bangun)

	
	@GetMapping("/beranda")
	public String beranda(Model model) {
		// Pass the current state to the frontend
		model.addAttribute("buttonState", state);
		model.addAttribute("buttonLabel", getLabelForState(state));
		model.addAttribute("buttonClass", getClassForState(state));
		model.addAttribute("buttonIcon", getIconForState(state));
		
		return "beranda"; // Thymeleaf template
	}
	@GetMapping("/tips")
	public String tipsPage() {
		return "tips"; // Mengarah ke file templates/tips.html
	}

	@PostMapping("/beranda/toggle-button")
	public String toggleButton() throws InterruptedException {
		// Transition logic for the button
		switch (state) {
			case "tidur":
				state = "tidur-active";
				Thread.sleep(3000); // Simulate delay
				state = "bangun";
				DataTidurService dataTidurService = new DataTidurService();
				LocalDateTime time = LocalDateTime.now();
				dataTidurService.addStart(time);
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
				return "Tombol Bangun";
			case "bangun":
				return "Tombol Tidur";
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
