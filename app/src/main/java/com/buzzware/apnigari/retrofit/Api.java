package com.buzzware.apnigari.retrofit;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface Api {

    @FormUrlEncoded
    @POST()
    Call<String> getPlaces(@Url String url, @Field("email") String ema);

    // payment api
    @Headers("Accept: application/json")
    @POST("/bandy/api/iridegetEphemeralKey")
    Call<String> getEphemeralKey(@Body RequestBody body);

    @Headers("Accept: application/json")
    @POST("/widgets/paymentMethods")
    Call<String> getPaymentMethods(@Body RequestBody body);


    @Headers("Accept: application/json")
    @POST("/widgets/detatch")
    Call<String> deAttachPaymentMethod(@Body RequestBody body);


    @Headers("Accept: application/json")
    @POST("/bandy/api/accoutpaymentnew")
    Call<String> getSecret(@Body RequestBody body);

    @Headers("Accept: application/json")
    @POST("/bandy/Api/createclient")
    Call<String> createClient(@Body RequestBody requestBody);

    @Headers("Accept: application/json")
    @POST("/bandy/Api/paymentintent")
    Call<String> stripePaymentIntent(@Body RequestBody requestBody);

    @Headers("Accept: application/json")
    @POST("/bandy/Api/createaccount")
    Call<String> createAccount(@Body RequestBody requestBody);

    @Headers("Accept: application/json")
    @POST("/bandy/Api/linkaccount")
    Call<String> linkAccount(@Body RequestBody requestBody);

    @Headers("Accept: application/json")
    @POST("/bandy/Api/iridefirststep")
    Call<String> createCustomer(@Body RequestBody requestBody);

    @Headers("Accept: application/json")
    @POST("/bandy/Api/iridedeletecard")
    Call<String> deleteMethod(@Body RequestBody requestBody);

    @Headers("Accept: application/json")
    @POST("/bandy/Api/irideaccoutpayment")
    Call<String> accountPayment(@Body RequestBody requestBody);

    @Headers("Accept: application/json")
    @POST("/bandy/Api/cancelpayemt")
    Call<String> cancelPayment(@Body RequestBody requestBody);

    @Headers("Accept: application/json")
    @POST("/bandy/Api/checkaccount")
    Call<String> checkAccount(@Body RequestBody body);

    @Headers("Accept: application/json")
    @POST("/bandy/Api/checkaccount")
    Call<String> stripeCards(@Body RequestBody body);
//
//    @GET("/v1/customers/{customer_id}/sources/{sk}")
//    Call<CardListResponse> getAllCards(@Path(value = "customer_id") String customerId,
//                                       @Path(value = "sk") String secretKey, @Query("object") String object,
//                                       @Query("limit") int limit);

    @Headers("Accept: application/json")
    @DELETE("/v1/customers/{path}")
    Call<String> deleteCard(@Path("path") String path, @Header("Authorization") String token);

//    curl https://api.stripe.com/v1/customers/cus_AJ6yY15pe9xOZe/sources \
//            -u sk_test_4eC39HqLyjWDarjtT1zdp7dc: \
//            -d source=tok_mastercard
    @Headers("Accept: application/json")
    @DELETE("/v1/customers/{path}")
    Call<String> createCard(@Path("path") String path, @Header("Authorization") String token, @Body RequestBody body);

}
