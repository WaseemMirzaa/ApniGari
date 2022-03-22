package com.buzzware.apnigari.activities.home.mo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class VehicleModel implements Parcelable {

    public String id;

    public String backInCarUrl;

    public String frontInCarUrl;

    public String frontCarUrl;

    public String insuranceUrl;

    public String leftCarUrl;

    public String licenseUrl;

    public String color;

    public String make;

    public String model;

    public String rearCarUrl;

    public String registrationUrl;

    public String rightCarUrl;

    public String tagNumber;

    public String licensePlateNumber;

    public String userId;

    public String year;

    @SerializedName("noofDoors")
    public String noOfDoors;

    @SerializedName("noofSeatbelts")
    public String noOfSeatBelts;

    public String name;

    public VehicleModel() {

    }

    public String getName() {
        return (name != null) ? name : "";
    }

    protected VehicleModel(Parcel in) {
        id = in.readString();
        backInCarUrl = in.readString();
        frontInCarUrl = in.readString();
        frontCarUrl = in.readString();
        insuranceUrl = in.readString();
        leftCarUrl = in.readString();
        licenseUrl = in.readString();
        make = in.readString();
        model = in.readString();
        rearCarUrl = in.readString();
        registrationUrl = in.readString();
        rightCarUrl = in.readString();
        tagNumber = in.readString();
        userId = in.readString();
        year = in.readString();
        noOfDoors = in.readString();
        noOfSeatBelts = in.readString();
        name = in.readString();
        color = in.readString();
        licensePlateNumber = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(backInCarUrl);
        dest.writeString(frontInCarUrl);
        dest.writeString(frontCarUrl);
        dest.writeString(insuranceUrl);
        dest.writeString(leftCarUrl);
        dest.writeString(licenseUrl);
        dest.writeString(make);
        dest.writeString(model);
        dest.writeString(rearCarUrl);
        dest.writeString(registrationUrl);
        dest.writeString(rightCarUrl);
        dest.writeString(tagNumber);
        dest.writeString(userId);
        dest.writeString(year);
        dest.writeString(noOfDoors);
        dest.writeString(noOfSeatBelts);
        dest.writeString(name);
        dest.writeString(color);
        dest.writeString(licensePlateNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VehicleModel> CREATOR = new Creator<VehicleModel>() {
        @Override
        public VehicleModel createFromParcel(Parcel in) {
            return new VehicleModel(in);
        }

        @Override
        public VehicleModel[] newArray(int size) {
            return new VehicleModel[size];
        }
    };

    public String getId() {

        if (id == null)

            return "";

        return id;
    }

    public String getBackInCarUrl() {

        if (backInCarUrl == null)

            return "";

        return backInCarUrl;
    }

    public String getFrontInCarUrl() {

        if (backInCarUrl == null)

            return "";

        return backInCarUrl;
    }

    public String getFrontCarUrl() {

        if (backInCarUrl == null)

            return "";

        return backInCarUrl;
    }

    public String getInsuranceUrl() {

        if (insuranceUrl == null)

            return "";

        return insuranceUrl;
    }

    public String getLeftCarUrl() {

        if (leftCarUrl == null)

            return "";

        return leftCarUrl;
    }

    public String getLicenseUrl() {

        if (leftCarUrl == null)

            return "";

        return licenseUrl;
    }

    public String getMake() {

        if (make == null)

            return "";

        return make;
    }

    public String getModel() {

        if (model == null)

            return "";

        return model;
    }

    public String getRearCarUrl() {

        if (rearCarUrl == null)

            return "";

        return rearCarUrl;
    }

    public String getRegistrationUrl() {

        if (registrationUrl == null)

            return "";

        return registrationUrl;
    }

    public String getRightCarUrl() {

        if (rightCarUrl == null)

            return "";

        return rightCarUrl;
    }

    public String getTagNumber() {

        if (tagNumber == null)

            return "";

        return tagNumber;
    }

    public String getUserId() {

        if (userId == null)

            return "";

        return userId;
    }

    public String getYear() {

        if (year == null)

            return "";

        return year;
    }

    public String getNoOfDoors() {

        if (noOfDoors == null)

            return "";

        return noOfDoors;
    }

    public String getNoOfSeatBelts() {

        if (noOfSeatBelts == null)

            return "";

        return noOfSeatBelts;
    }

    public String getColor() {

        return (color == null) ? "" : color;

    }

    public String getLicensePlateNumber() {

        return (licensePlateNumber == null) ? "" : licensePlateNumber;

    }
}
