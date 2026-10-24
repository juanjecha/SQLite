package com.example.ingles.api;

import android.content.Context;
import android.content.SharedPreferences;

/** Guarda el token JWT en el teléfono para usarlo en las siguientes peticiones */
public class Sesion {

    private static final String PREFS = "sesion_ingles";

    public static void guardar(Context ctx, String token, String matricula, String nombre) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putString("token", token)
                .putString("matricula", matricula)
                .putString("nombre", nombre)
                .apply();
    }

    /** Para mandarlo así en las rutas protegidas: @Header("Authorization") */
    public static String header(Context ctx) {
        return "Bearer " + ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("token", "");
    }

    public static void cerrar(Context ctx) {
        SharedPreferences.Editor e = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
        e.clear().apply();
    }
}
