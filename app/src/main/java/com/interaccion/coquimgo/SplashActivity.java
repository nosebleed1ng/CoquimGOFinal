package com.interaccion.coquimgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.interaccion.coquimgo.databinding.ActivitySplashBinding;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pantalla completa (oculta barra de estado en el splash)
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

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


        // Animación de entrada
        View root = binding.getRoot();
        root.setAlpha(0f);
        root.setScaleX(0.97f);
        root.setScaleY(0.97f);
        root.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(700)
                .start();

        // Animación para el botón Iniciar (aparece desde abajo)
        Button btnIniciar = binding.btnIniciar;
        btnIniciar.setAlpha(0f);
        btnIniciar.setTranslationY(40f);
        btnIniciar.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(400)
                .setDuration(600)
                .start();

        // Botón Iniciar, abre LugaresTuristicosActivity
        binding.btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Evitar doble click
                binding.btnIniciar.setEnabled(false);

                Intent intent = new Intent(SplashActivity.this, LugaresTuristicosActivity.class);
                startActivity(intent);
                finish(); // Evita volver al splash con el botón atrás
            }
        });

    }
}
