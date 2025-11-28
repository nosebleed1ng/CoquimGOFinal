package com.interaccion.coquimgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;

public class ConfiguracionActivity extends AppCompatActivity {

    private Switch switchModoOscuro;
    private Button btnSeleccionIdioma, btnVolverInicio;

    // Vistas para animaciones
    private TextView txtTitulo;
    private CardView cardModoOscuro, cardIdioma, cardVolver, cardAcerca;
    private View layoutRoot;
    private View imgLogoCoquimgo;

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

        // Referencias
        switchModoOscuro = findViewById(R.id.switchModoOscuro);
        btnSeleccionIdioma = findViewById(R.id.btnSeleccionIdioma);
        btnVolverInicio = findViewById(R.id.btnVolverInicio);


        txtTitulo = findViewById(R.id.txtTitulo);
        cardModoOscuro = findViewById(R.id.cardModoOscuro);
        cardIdioma = findViewById(R.id.cardIdioma);
        cardVolver = findViewById(R.id.cardVolverInicio);
        cardAcerca = findViewById(R.id.cardAcerca);
        imgLogoCoquimgo = findViewById(R.id.imgLogoCoquimgo);
        layoutRoot = findViewById(R.id.layoutConfigRoot);

        switchModoOscuro.setChecked(modoOscuro);

        // Microanimación en botones
        setupButtonPressAnimation(btnSeleccionIdioma);
        setupButtonPressAnimation(btnVolverInicio);

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

        //  Activar/Desactivar modo oscuro con animación de desvanecimiento
        switchModoOscuro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("modo_oscuro", isChecked).apply();

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

        // Animaciones
        prepararAnimacionesIniciales();
        animarEntradaConfiguracion();
    }

    // Animaciones de entrada


    private void prepararAnimacionesIniciales() {
        if (layoutRoot != null) {
            layoutRoot.setAlpha(1f);
        }

        // Título
        if (txtTitulo != null) {
            txtTitulo.setAlpha(0f);
            txtTitulo.setTranslationY(-120f);
        }

        // Cards
        if (cardModoOscuro != null) {
            cardModoOscuro.setAlpha(0f);
            cardModoOscuro.setTranslationX(-220f);
            cardModoOscuro.setScaleX(0.9f);
            cardModoOscuro.setScaleY(0.9f);
        }

        if (cardIdioma != null) {
            cardIdioma.setAlpha(0f);
            cardIdioma.setTranslationX(220f);
            cardIdioma.setScaleX(0.9f);
            cardIdioma.setScaleY(0.9f);
        }

        if (cardVolver != null) {
            cardVolver.setAlpha(0f);
            cardVolver.setTranslationX(-220f);
            cardVolver.setScaleX(0.9f);
            cardVolver.setScaleY(0.9f);
        }

        if (cardAcerca != null) {
            cardAcerca.setAlpha(0f);
            cardAcerca.setTranslationY(200f);
            cardAcerca.setScaleX(0.9f);
            cardAcerca.setScaleY(0.9f);
        }

        // Logo CoquimGO
        if (imgLogoCoquimgo != null) {
            imgLogoCoquimgo.setAlpha(0f);
            imgLogoCoquimgo.setTranslationY(260f);
            imgLogoCoquimgo.setScaleX(0.6f);
            imgLogoCoquimgo.setScaleY(0.6f);
        }
    }

    private void animarEntradaConfiguracion() {
        DecelerateInterpolator desacelerar = new DecelerateInterpolator();
        OvershootInterpolator rebote = new OvershootInterpolator(1.1f);

        if (txtTitulo != null) {
            txtTitulo.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(80)
                    .setInterpolator(desacelerar)
                    .start();
        }

        if (cardModoOscuro != null) {
            cardModoOscuro.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(650)
                    .setStartDelay(200)
                    .setInterpolator(rebote)
                    .start();
        }

        if (cardIdioma != null) {
            cardIdioma.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(650)
                    .setStartDelay(280)
                    .setInterpolator(rebote)
                    .start();
        }

        if (cardVolver != null) {
            cardVolver.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(650)
                    .setStartDelay(360)
                    .setInterpolator(rebote)
                    .start();
        }

        if (cardAcerca != null) {
            cardAcerca.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(700)
                    .setStartDelay(440)
                    .setInterpolator(desacelerar)
                    .start();
        }

        // Logo con zoom y rebote
        if (imgLogoCoquimgo != null) {
            imgLogoCoquimgo.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(750)
                    .setStartDelay(550)
                    .setInterpolator(rebote)
                    .start();
        }
    }


    // microanimacion de botones
    private void setupButtonPressAnimation(Button button) {
        if (button == null) return;

        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    animateScale(v, 0.96f);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    animateScale(v, 1f);
                    break;
            }
            return false;
        });
    }

    private void animateScale(View v, float scale) {
        ViewPropertyAnimatorCompat animator = ViewCompat.animate(v);
        animator.scaleX(scale).scaleY(scale).setDuration(120).start();
    }
}
