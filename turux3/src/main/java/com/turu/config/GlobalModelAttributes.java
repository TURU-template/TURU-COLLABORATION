package com.turu.config;

import com.turu.model.Creator;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("creators")
    public List<Creator> globalCreatorsList() {
        return List.of(
            new Creator("🦊", "Iqbal", "https://instagram.com/otachiking"),
            new Creator("♠️", "Tiroy", "https://instagram.com/tiroy_hidayat"),
            new Creator("🐿️", "Zia", "https://instagram.com/mas__zia"),
            new Creator("😏", "Valdez", "https://instagram.com/valdezbrz"),
            new Creator("🐘", "Marcel", "https://instagram.com/marcelepaf"),
            new Creator("😻", "Audrey", "https://instagram.com/audreyathl"),
            new Creator("🐺", "Aisya", "https://instagram.com/aisyasfya")
        );
    }
}
