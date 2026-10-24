package com.example.ingles;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public class ApiService {
    @POST("api/Students")Call<Response>saveStudent(@Header("Authorization")String token, @Body Student student);
}