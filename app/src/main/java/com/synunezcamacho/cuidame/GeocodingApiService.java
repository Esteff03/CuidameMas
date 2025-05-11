package com.synunezcamacho.cuidame;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeocodingApiService {
    @GET("geocode/json")
    Call<GeocodingResponse> geocodeAddress(
            @Query("address") String address,
            @Query("key") String apiKey
    );
}

