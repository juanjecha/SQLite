package com.example.ingles.api;

/** Datos que se mandan al registrar un alumno (los nombres = llaves del JSON) */
public class ApiRequest {

    private final String nombre;
    private final String matricula;
    private final String correo;
    private final String carrera;
    private final String contrasena;

    public ApiRequest(String nombre, String matricula, String correo, String carrera, String contrasena) {
        this.nombre = nombre;
        this.matricula = matricula;
        this.correo = correo;
        this.carrera = carrera;
        this.contrasena = contrasena;
    }
}
