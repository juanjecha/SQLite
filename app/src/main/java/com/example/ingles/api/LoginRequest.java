package com.example.ingles.api;

/** JSON que se manda al login: { "matricula": "...", "contrasena": "..." } */
public class LoginRequest {

    private final String matricula;
    private final String contrasena;

    public LoginRequest(String matricula, String contrasena) {
        this.matricula = matricula;
        this.contrasena = contrasena;
    }
}
