package com.buzzware.apnigari.activities.home.dialog.bottomSheet.mo.geoCode;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReverseGeoCodeResponse {

    public PlusCode plus_code;

    @SerializedName("results")
    public List<ReverseGeoCode> results;

    public String status;
}
