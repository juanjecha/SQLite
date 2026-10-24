package com.example.ingles.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Crea UNA sola instancia de Retrofit para toda la app.
 *
 * BASE_URL:
 *  - Emulador de Android Studio + "php spark serve":  http://10.0.2.2:8080/api/
 *    (10.0.2.2 es "localhost de tu PC" visto desde el emulador; 127.0.0.1 NO sirve)
 *  - Emulador + XAMPP/WAMP (htdocs):                  http://10.0.2.2/inglesBackend/public/api/
 *  - Celular físico: usa la IP de tu PC (ipconfig), ej. http://192.168.1.70:8080/api/
 *    y arranca el backend con:  php spark serve --host 0.0.0.0
 *  ¡Siempre debe terminar en "/"!
 */
public class ApiCliente {

    private static final String BASE_URL = "http://10.0.2.2:8080/";

    private static Retrofit retrofit;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
