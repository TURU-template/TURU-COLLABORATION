package com.turu.model;

import java.time.Duration;

public class Analisis {
    private double rataWaktuBangun;
    private double rataSkorTidur;
    private Duration rentangTidur;

    // Getter dan Setter
    public double getRataWaktuBangun() {
        return rataWaktuBangun;
    }

    public void setRataWaktuBangun(double rataWaktuBangun) {
        this.rataWaktuBangun = rataWaktuBangun;
    }

    public double getRataSkorTidur() {
        return rataSkorTidur;
    }

    public void setRataSkorTidur(double rataSkorTidur) {
        this.rataSkorTidur = rataSkorTidur;
    }

    public Duration getRentangTidur() {
        return rentangTidur;
    }

    public void setRentangTidur(Duration rentangTidur) {
        this.rentangTidur = rentangTidur;
    }
}
