package com.example.ingles;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.example.ingles.api.ApiRequest;
import com.example.ingles.api.ApiCliente;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Pantalla 2 del Diagrama de Interacción: Registro (F. Reg).
 * Campos: Nom, Correo, Mat, Carrera (Spinner), Contraseña.
 *
 * Flujo del diagrama:
 *   SI  -> "Registro Exitoso"   -> va a Inicio de Sesión (pantalla 3)
 *   NO  -> "Usuario ya registrado"
 *
 * La contraseña se manda tal cual; se encripta del lado de la BD / CodeIgniter.
 */
public class RegistroActivity extends AppCompatActivity {

    private TextInputLayout tilNombre, tilCorreo, tilMatricula, tilContrasena;
    private TextInputEditText txtNombre, txtCorreo, txtMatricula, txtContrasena;
    private Spinner spCarrera;
    private TextView tvErrorCarrera;
    private Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // 1. Vincular componentes del XML
        tilNombre = findViewById(R.id.tilNombre);
        tilCorreo = findViewById(R.id.tilCorreo);
        tilMatricula = findViewById(R.id.tilMatricula);
        tilContrasena = findViewById(R.id.tilContrasena);
        txtNombre = findViewById(R.id.txtNombre);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtMatricula = findViewById(R.id.txtMatricula);
        txtContrasena = findViewById(R.id.txtContrasena);
        spCarrera = findViewById(R.id.spCarrera);
        tvErrorCarrera = findViewById(R.id.tvErrorCarrera);
        btnEnviar = findViewById(R.id.btnEnviar);

        // 2. Llenar el Spinner de carreras (res/values/strings.xml -> carreras)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.carreras, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCarrera.setAdapter(adapter);

        spCarrera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) tvErrorCarrera.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // 3. Botón Enviar
        btnEnviar.setOnClickListener(v -> registrar());
    }

    private void registrar() {
        if (!validarCampos()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombre = texto(txtNombre);
        String correo = texto(txtCorreo);
        String matricula = texto(txtMatricula);
        String carrera = spCarrera.getSelectedItem().toString();
        String contrasena = texto(txtContrasena);

        // Se envían los datos a la API de CodeIgniter:
        //   POST /api/alumnos/registrar
        //   La contraseña se encripta en el servidor (password_hash).
        //   Si la matrícula ya existe el servidor responde 409 -> "Usuario ya registrado".
        btnEnviar.setEnabled(false);
        ApiRequest alumno = new ApiRequest(nombre, matricula, correo, carrera, contrasena);

        ApiCliente.getApi().registrarAlumno(alumno).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                btnEnviar.setEnabled(true);
                if (response.isSuccessful()) {
                    mostrarRegistroExitoso(nombre);
                } else if (response.code() == 409) {
                    mostrarUsuarioYaRegistrado();
                } else {
                    Toast.makeText(RegistroActivity.this,
                            "Error del servidor (" + response.code() + ")", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                btnEnviar.setEnabled(true);
                Toast.makeText(RegistroActivity.this,
                        "No se pudo conectar con el servidor: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /** Valida que ningún campo esté vacío (y formato básico de correo/matrícula). */
    private boolean validarCampos() {
        boolean ok = true;

        tilNombre.setError(null);
        tilCorreo.setError(null);
        tilMatricula.setError(null);
        tilContrasena.setError(null);
        tvErrorCarrera.setVisibility(View.GONE);

        if (TextUtils.isEmpty(texto(txtNombre))) {
            tilNombre.setError(getString(R.string.campo_obligatorio));
            ok = false;
        }

        String correo = texto(txtCorreo);
        if (TextUtils.isEmpty(correo)) {
            tilCorreo.setError(getString(R.string.campo_obligatorio));
            ok = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tilCorreo.setError(getString(R.string.correo_invalido));
            ok = false;
        }

        String matricula = texto(txtMatricula);
        if (TextUtils.isEmpty(matricula)) {
            tilMatricula.setError(getString(R.string.campo_obligatorio));
            ok = false;
        } else if (!TextUtils.isDigitsOnly(matricula)) {
            tilMatricula.setError(getString(R.string.matricula_invalida));
            ok = false;
        }

        if (spCarrera.getSelectedItemPosition() == 0) {
            tvErrorCarrera.setVisibility(View.VISIBLE);
            ok = false;
        }

        if (TextUtils.isEmpty(texto(txtContrasena))) {
            tilContrasena.setError(getString(R.string.campo_obligatorio));
            ok = false;
        }

        return ok;
    }

    /** SI -> Registro Exitoso -> Inicio de Sesión */
    private void mostrarRegistroExitoso(String nombre) {
        new AlertDialog.Builder(this)
                .setTitle("Registro exitoso")
                .setMessage("¡Listo, " + nombre + "! Ya puedes iniciar sesión con tu matrícula.")
                .setCancelable(false)
                .setPositiveButton("Iniciar sesión", (dialog, which) -> {
                    Intent intent = new Intent(this, InicioSesionActivity.class);
                    intent.putExtra("matricula", texto(txtMatricula));
                    startActivity(intent);
                    finish();
                })
                .show();
    }

    /** NO -> Usuario ya registrado */
    private void mostrarUsuarioYaRegistrado() {
        new AlertDialog.Builder(this)
                .setTitle("Usuario ya registrado")
                .setMessage("Esa matrícula o correo ya existe. Inicia sesión.")
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private String texto(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString().trim();
    }
}
