package com.interaccion.coquimgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.interaccion.coquimgo.databinding.ActivitySplashBinding;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pantalla completa
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // Aplicar idioma guardado globalmente antes de mostrar la pantalla
        SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);
        String idiomaGuardado = prefs.getString("idioma", "es"); // por defecto español

        Locale locale = new Locale(idiomaGuardado);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Inflar la vista
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View bg = binding.bgImage;
        View overlay = binding.dimOverlay;
        View bienvenida = binding.txtBienvenida;
        View logo = binding.imgLogo;
        View slogan = binding.txtSlogan;
        View highlights = binding.txtHighlights;
        View bottomPanel = binding.bottomPanel;
        Button btnIniciar = binding.btnIniciar;
        View footer = binding.txtFooter;

        // Fondo: leve zoom-out
        bg.setAlpha(0f);
        bg.setScaleX(1.12f);
        bg.setScaleY(1.12f);

        // Capa oscura
        overlay.setAlpha(0f);

        // Bienvenida
        bienvenida.setAlpha(0f);
        bienvenida.setTranslationY(-30f);

        // Logo
        logo.setAlpha(0f);
        logo.setScaleX(0.7f);
        logo.setScaleY(0.7f);
        logo.setTranslationY(-60f);

        // Slogan
        slogan.setAlpha(0f);
        slogan.setTranslationY(-30f);

        // Highlights
        highlights.setAlpha(0f);
        highlights.setTranslationY(20f);

        // Panel inferior
        bottomPanel.setAlpha(0f);
        bottomPanel.setTranslationY(40f);

        // Botón iniciar
        btnIniciar.setAlpha(0f);
        btnIniciar.setScaleX(0.85f);
        btnIniciar.setScaleY(0.85f);

        // Footer
        footer.setAlpha(0f);
        footer.setTranslationY(20f);

        // Fondo
        bg.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(900)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Capa oscura
        overlay.animate()
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(150)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // "Te damos la bienvenida a" (según idioma)
        bienvenida.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(700)
                .setStartDelay(250)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Logo
        logo.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(900)
                .setStartDelay(400)
                .setInterpolator(new OvershootInterpolator(1.15f))
                .start();

        // Slogan (usa @string/textoPrincipal)
        slogan.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(750)
                .setStartDelay(650)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Highlights
        highlights.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(650)
                .setStartDelay(800)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Panel inferior
        bottomPanel.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(650)
                .setStartDelay(900)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // Botón Iniciar
        btnIniciar.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(650)
                .setStartDelay(1000)
                .setInterpolator(new OvershootInterpolator(1.3f))
                .start();

        // Footer
        footer.animate()
                .alpha(0.9f)
                .translationY(0f)
                .setDuration(700)
                .setStartDelay(1150)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        binding.btnIniciar.setOnClickListener(v -> {
            // Evitar doble click
            binding.btnIniciar.setEnabled(false);

            // Pequeño feedback al pulsar
            binding.btnIniciar.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .alpha(0.9f)
                    .setDuration(150)
                    .withEndAction(() -> binding.btnIniciar.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .alpha(1f)
                            .setDuration(150)
                            .start())
                    .start();

            binding.progressBar.setVisibility(View.VISIBLE);

            Intent intent = new Intent(SplashActivity.this, LugaresTuristicosActivity.class);
            startActivity(intent);
            finish(); // Evita volver al splash con el botón atrás
        });
    }
}

