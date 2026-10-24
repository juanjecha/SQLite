package com.example.ingles.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/** Rutas de CodeIgniter (app/Config/Routes.php) que usa la app del alumno */
public interface ApiService {

    // POST /api/alumnos/registrar  -> pantalla Registro
    @POST("alumnos/registrar")
    Call<ResponseBody> registrarAlumno(@Body ApiRequest alumno);

    // POST /api/login/alumno  -> pantalla Inicio de sesión
    @POST("login/alumno")
    Call<LoginResponse> loginAlumno(@Body LoginRequest datos);
}
