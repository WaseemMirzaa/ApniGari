package com.buzzware.apnigari.commonModels.ride;

import java.util.List;

public class RideModel {

    public String id;

    public String userId;

    public String pm_id;

    public String driverId;

    public String vehicleId;

    public long bookingDate;

    public Double driverLat;

    public Double driverLng;

    public String username;

    public String rideType;

    public TripDetail tripDetail;

    public String status;

    public String price;
    public List<String> fcmTokens;


    public RideModel() {

    }

    public RideModel(String userId, String driverId, String vehicleId, long bookingDate, Double driverLat, Double driverLng, String status) {
        this.userId = userId;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.bookingDate = bookingDate;
        this.driverLat = driverLat;
        this.driverLng = driverLng;
        this.status = status;
    }

}
