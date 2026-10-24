package com.example.ingles.api;

import com.google.gson.annotations.SerializedName;

/** Respuesta de POST /api/login/alumno */
public class LoginResponse {

    public String token;

    @SerializedName("primera_vez")
    public boolean primeraVez;

    public DatosAlumno alumno;

    public static class DatosAlumno {
        public String id;
        public String nombre;
        public String matricula;
    }
}
