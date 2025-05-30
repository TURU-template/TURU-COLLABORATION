package com.turu.controllers;

import com.turu.model.Audio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class RadioController {

    @GetMapping("/radio2")
    public String displayRadioPage(Model model) {
        // Data Audio
        List<Audio> ambiens = new ArrayList<>();
        ambiens.add(new Audio("Api", "../asset/songs/Api.mp3", 40));
        ambiens.add(new Audio("Ombak", "../asset/songs/Ombak.mp3", 35));
        ambiens.add(new Audio("Burung", "/asset/songs/Burung.mp3", 30));
        ambiens.add(new Audio("Jangkrik", "/asset/songs/Jangkrik.mp3", 50));
        ambiens.add(new Audio("Hujan", "/asset/songs/Hujan.mp3", 60));

        List<Audio> loFi = new ArrayList<>();
        loFi.add(new Audio("Monoman", "/asset/songs/Monoman.mp3", 45));
        loFi.add(new Audio("Twilight", "/asset/songs/Twilight.mp3", 50));
        loFi.add(new Audio("Yasumu", "/asset/songs/Yasumu.mp3", 35));

        List<Audio> colors = new ArrayList<>();
        colors.add(new Audio("White Noise", "https://whitenoise.tmsoft.com/wntv/noise_white-0.mp3", 30));
        colors.add(new Audio("Blue Noise", "https://whitenoise.tmsoft.com/wntv/noise_blue-0.mp3", 25));
        colors.add(new Audio("Brown Noise", "https://whitenoise.tmsoft.com/wntv/noise_brown-0.mp3", 35));
        colors.add(new Audio("Pink Noise", "https://whitenoise.tmsoft.com/wntv/noise_pink-0.mp3", 20));

        // Passing Data ke Model
        model.addAttribute("ambiens", ambiens);
        model.addAttribute("loFi", loFi);
        model.addAttribute("colors", colors);

        return "radio2"; // Make sure the HTML file is named `radio.html`
    }
}
