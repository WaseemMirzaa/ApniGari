package com.buzzware.apnigari.retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Controller {

    public static final String Base_Url = "https://maps.googleapis.com";
    public static final String Base_Url_Payments = "https://buzzwaretech.com";
    public static final String Base_Url_Payments_CF = "https://us-central1-myclientsapp-16171.cloudfunctions.net";
    public static final String Base_Url_Stripe = "https://api.stripe.com";

    public static Retrofit retrofit = null;

    public static Api getApi(String url) {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder mOkHttpClient = new OkHttpClient.Builder();
        mOkHttpClient.connectTimeout(50, TimeUnit.SECONDS).writeTimeout(1, TimeUnit.MINUTES).readTimeout(50, TimeUnit.SECONDS);
        mOkHttpClient.hostnameVerifier((s, sslSession) -> true);

        Retrofit.Builder mBuilder = new Retrofit.Builder().baseUrl(url).addConverterFactory(ScalarsConverterFactory.create()).addConverterFactory(GsonConverterFactory.create());

        Retrofit mRetrofit = mBuilder.client(mOkHttpClient.addInterceptor(httpLoggingInterceptor).build()).build();

        Api mClient = mRetrofit.create(Api.class);

        return mClient;
    }


}
