package com.turu.model;

// Child Class: Lo-Fi
public class LoFi extends Audio {
    private String pencipta;

    // Constructor
    public LoFi(String judul, String source, int volume, String pencipta) {
        super(judul, source, volume);
        this.pencipta = pencipta;
    }

    // Getter dan Setter untuk pencipta
    public String getPencipta() {
        return pencipta;
    }

    public void setPencipta(String pencipta) {
        this.pencipta = pencipta;
    }

    // Method playMix
    // @Override
    // public void playMix() {
    //     System.out.println("Memainkan Lo-Fi mix oleh: " + pencipta + 
    //                        ", judul: " + getName() + 
    //                        ", sumber: " + getUrl() + 
    //                        ", dengan volume: " + getVolume());
    // }
}
