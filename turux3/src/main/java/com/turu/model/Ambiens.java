package com.turu.model;

// Child Class: Ambiens
public class Ambiens extends Audio {

    // Constructor
    public Ambiens(String judul, String source, int volume) {
        super(judul, source, volume);
    }

    // Method playMix
    public void playMix() {
        System.out.println("Memainkan ambiens mix dari judul: " + getUrl() + 
                           ", sumber: " + getUrl() + 
                           ", dengan volume: " + getVolume());
    }
}
