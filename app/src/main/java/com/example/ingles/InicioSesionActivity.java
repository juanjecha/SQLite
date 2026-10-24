package com.example.ingles;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.example.ingles.api.ApiCliente;
import com.example.ingles.api.LoginRequest;
import com.example.ingles.api.LoginResponse;
import com.example.ingles.api.Sesion;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Pantalla 3 del Diagrama de Interacción: Inicio de Sesión.
 * Campos: Matr. (matrícula) y Contraseña.
 *
 * Flujo del diagrama:
 *   1ª vez que entra -> Configuraciones (pantalla 5)
 *   2ª vez en adelante -> Inicio (pantalla 6)
 */
public class InicioSesionActivity extends AppCompatActivity {

    private TextInputLayout tilMatricula, tilContrasena;
    private TextInputEditText txtMatricula, txtContrasena;
    private Button btnEnviar;
    private TextView tvOlvidaste, tvIrRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio_sesion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // Vincular componentes del XML
        tilMatricula = findViewById(R.id.tilMatricula);
        tilContrasena = findViewById(R.id.tilContrasena);
        txtMatricula = findViewById(R.id.txtMatricula);
        txtContrasena = findViewById(R.id.txtContrasena);
        btnEnviar = findViewById(R.id.btnEnviar);
        tvOlvidaste = findViewById(R.id.tvOlvidaste);
        tvIrRegistro = findViewById(R.id.tvIrRegistro);

        // Si viene del Registro, se rellena la matrícula
        String matricula = getIntent().getStringExtra("matricula");
        if (matricula != null) {
            txtMatricula.setText(matricula);
            txtContrasena.requestFocus();
        }

        btnEnviar.setOnClickListener(v -> iniciarSesion());

        // Pantalla 4 (Recuperar contraseña) se hará en la siguiente etapa
        tvOlvidaste.setOnClickListener(v ->
                Toast.makeText(this, "Recuperar contraseña: próximamente", Toast.LENGTH_SHORT).show());

        tvIrRegistro.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistroActivity.class));
            finish();
        });
    }

    private void iniciarSesion() {
        String matricula = texto(txtMatricula);
        String contrasena = texto(txtContrasena);

        // Validación de campos vacíos
        boolean ok = true;
        tilMatricula.setError(null);
        tilContrasena.setError(null);
        if (TextUtils.isEmpty(matricula)) {
            tilMatricula.setError(getString(R.string.campo_obligatorio));
            ok = false;
        }
        if (TextUtils.isEmpty(contrasena)) {
            tilContrasena.setError(getString(R.string.campo_obligatorio));
            ok = false;
        }
        if (!ok) return;

        // Se consulta la BD a través de la API de CodeIgniter:
        //   POST /api/login/alumno   { matricula, contrasena }
        //   El servidor compara la contraseña encriptada con password_verify
        //   y responde el token JWT + si es la PRIMERA vez que entra.
        btnEnviar.setEnabled(false);

        ApiCliente.getApi().loginAlumno(new LoginRequest(matricula, contrasena))
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        btnEnviar.setEnabled(true);

                        if (!response.isSuccessful() || response.body() == null) {
                            // 401 -> credenciales incorrectas
                            Toast.makeText(InicioSesionActivity.this,
                                    "Matrícula o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        LoginResponse r = response.body();
                        String nombre = r.alumno != null ? r.alumno.nombre : "";
                        Sesion.guardar(InicioSesionActivity.this, r.token, matricula, nombre);
                        irSiguientePantalla(r.primeraVez, matricula, nombre);
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        btnEnviar.setEnabled(true);
                        Toast.makeText(InicioSesionActivity.this,
                                "No se pudo conectar con el servidor: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void irSiguientePantalla(boolean esPrimeraVez, String matricula, String nombre) {
        if (esPrimeraVez) {
            // 1ª vez -> Configuraciones (pantalla 5)
            // Intent intent = new Intent(this, ConfiguracionesActivity.class);
            // intent.putExtra("matricula", matricula);
            // startActivity(intent);
            Toast.makeText(this, "¡Bienvenido " + nombre + "! (1ª vez: aquí irá a Configuraciones)", Toast.LENGTH_LONG).show();
        } else {
            // 2ª vez en adelante -> Inicio (pantalla 6)
            // Intent intent = new Intent(this, InicioActivity.class);
            // intent.putExtra("matricula", matricula);
            // startActivity(intent);
            Toast.makeText(this, "¡Hola de nuevo " + nombre + "! (aquí irá a Inicio)", Toast.LENGTH_LONG).show();
        }
    }

    private String texto(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString().trim();
    }
}
