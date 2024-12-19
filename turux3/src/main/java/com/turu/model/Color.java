package com.turu.model;

// Child Class: Color
public class Color extends Audio {

    // Constructor
    public Color(String judul, String source, int volume) {
        super(judul, source, volume);
    }

    // Method playIndividual
    public void playIndividual() {
        System.out.println("Memainkan track individual: " + getName() + 
                           ", sumber: " + getUrl() + 
                           ", dengan volume: " + getVolume());
    }
}
