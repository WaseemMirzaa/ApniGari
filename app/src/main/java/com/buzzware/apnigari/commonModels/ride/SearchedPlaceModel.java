package com.buzzware.apnigari.commonModels.ride;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchedPlaceModel implements Parcelable {

    public String id;

    public double lat;
    public String hash;
    public double lng;

    public String status;
    public String address;

    public SearchedPlaceModel() {

    }

    public SearchedPlaceModel(Parcel in) {
        id = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        status = in.readString();
        hash = in.readString();
        address = in.readString();
    }

    public static final Creator<SearchedPlaceModel> CREATOR = new Creator<SearchedPlaceModel>() {
        @Override
        public SearchedPlaceModel createFromParcel(Parcel in) {
            return new SearchedPlaceModel(in);
        }

        @Override
        public SearchedPlaceModel[] newArray(int size) {
            return new SearchedPlaceModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(status);
        dest.writeString(hash);
        dest.writeString(address);
    }
}
