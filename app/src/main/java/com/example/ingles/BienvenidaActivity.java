package com.example.ingles;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Pantalla 1 del Diagrama de Interacción: Bienvenida.
 * Botón Reg  -> RegistroActivity      (pantalla 2)
 * Botón I.Ses -> InicioSesionActivity (pantalla 3)
 */
public class BienvenidaActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnReg;
    private Button btnISes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bienvenida);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Vincular componentes del XML
        btnReg = findViewById(R.id.btnReg);
        btnISes = findViewById(R.id.btnISes);

        btnReg.setOnClickListener(this);
        btnISes.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnReg) {
            IrRegistro(v);
        }
        if (v.getId() == R.id.btnISes) {
            IrInicioSesion(v);
        }
    }

    public void IrRegistro(View v) {
        Intent intent = new Intent(this, RegistroActivity.class);
        startActivity(intent);
    }

    public void IrInicioSesion(View v) {
        Intent intent = new Intent(this, InicioSesionActivity.class);
        startActivity(intent);
    }
}
