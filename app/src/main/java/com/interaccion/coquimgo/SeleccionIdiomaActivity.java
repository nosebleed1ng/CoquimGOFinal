package com.interaccion.coquimgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SeleccionIdiomaActivity extends AppCompatActivity {

    private Button btnEspanol, btnIngles, btnPortugues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar modo oscuro guardado
        SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);
        boolean modoOscuro = prefs.getBoolean("modo_oscuro", false);
        AppCompatDelegate.setDefaultNightMode(
                modoOscuro ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_idioma);

        btnEspanol = findViewById(R.id.btnEspanol);
        btnIngles = findViewById(R.id.btnIngles);
        btnPortugues = findViewById(R.id.btnPortugues);

        // Guardar idioma seleccionado y volver al Splash
        btnEspanol.setOnClickListener(v -> cambiarIdioma("es"));
        btnIngles.setOnClickListener(v -> cambiarIdioma("en"));
        btnPortugues.setOnClickListener(v -> cambiarIdioma("pt"));
    }

    private void cambiarIdioma(String codigo) {
        // Guardar idioma
        SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);
        prefs.edit().putString("idioma", codigo).apply();

        // Animación de desvanecimiento
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(300);
        findViewById(android.R.id.content).startAnimation(fadeOut);

        // Reiniciar app hacia el Splash con animación
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                LocaleHelper.setLocale(SeleccionIdiomaActivity.this, codigo);
                Intent intent = new Intent(SeleccionIdiomaActivity.this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
    }
}
