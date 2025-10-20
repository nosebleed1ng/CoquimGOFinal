package com.interaccion.coquimgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class ConfiguracionActivity extends AppCompatActivity {

    private Switch switchModoOscuro;
    private Button btnSeleccionIdioma, btnVolverInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //  Aplicar modo oscuro guardado
        SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);
        boolean modoOscuro = prefs.getBoolean("modo_oscuro", false);
        AppCompatDelegate.setDefaultNightMode(
                modoOscuro ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        switchModoOscuro = findViewById(R.id.switchModoOscuro);
        btnSeleccionIdioma = findViewById(R.id.btnSeleccionIdioma);
        btnVolverInicio = findViewById(R.id.btnVolverInicio);

        switchModoOscuro.setChecked(modoOscuro);

        //  Botón para cambiar idioma
        btnSeleccionIdioma.setOnClickListener(v -> {
            Intent intent = new Intent(ConfiguracionActivity.this, SeleccionIdiomaActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        //  Botón para volver al inicio
        btnVolverInicio.setOnClickListener(v -> {
            Intent intent = new Intent(ConfiguracionActivity.this, SplashActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        //  Activar/Desactivar modo oscuro con animación
        switchModoOscuro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("modo_oscuro", isChecked).apply();

            // Animación de desvanecimiento
            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setDuration(300);
            findViewById(android.R.id.content).startAnimation(fadeOut);

            new Handler().postDelayed(() -> {
                AppCompatDelegate.setDefaultNightMode(
                        isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
                );

                Intent refresh = new Intent(this, ConfiguracionActivity.class);
                startActivity(refresh);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }, 300);
        });
    }
}
