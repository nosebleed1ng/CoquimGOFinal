package com.interaccion.coquimgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.interaccion.coquimgo.databinding.ActivitySplashBinding;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Aplicar idioma guardado globalmente antes de mostrar la pantalla
        SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);
        String idiomaGuardado = prefs.getString("idioma", "es"); // por defecto español

        Locale locale = new Locale(idiomaGuardado);
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Inflar la vista normalmente
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Botón Iniciar, abre LugaresTuristicosActivity
        binding.btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashActivity.this, LugaresTuristicosActivity.class);
                startActivity(intent);
                finish(); // Evita volver al splash con el botón atrás
            }
        });

    }
}
