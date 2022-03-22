package com.buzzware.apnigari.activities.home.mo;

public class Price {

    public String initialFee;
    public String pricePerMile;
    public String pricePerMin;
    public String costOfVehicle;

    public double getInitialFee() {
        return Double.parseDouble(initialFee);
    }

    public double getPricePerMile() {
        return Double.parseDouble(pricePerMile);
    }

    public double getPricePerMin() {
        return Double.parseDouble(pricePerMin);
    }

    public double getCostOfVehicle() {
        return Double.parseDouble(costOfVehicle);
    }
}
