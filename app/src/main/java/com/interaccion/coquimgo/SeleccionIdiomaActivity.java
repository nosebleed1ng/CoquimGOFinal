package com.interaccion.coquimgo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;

public class SeleccionIdiomaActivity extends AppCompatActivity {

    private Button btnEspanol, btnIngles, btnPortugues;

    // vistas para animación
    private TextView txtTituloIdioma;
    private CardView cardIdiomaEs, cardIdiomaEn, cardIdiomaPt;
    private View layoutRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 1) Tema (usa modo_oscuro de SharedPreferences)
        ThemeHelper.applyTheme(this);

        // 2) Idioma actual para mostrar los textos de esta pantalla
        SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);
        String idioma = prefs.getString("idioma", "es");
        LocaleHelper.setLocale(this, idioma);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_idioma);

        // Referencias
        btnEspanol   = findViewById(R.id.btnEspanol);
        btnIngles    = findViewById(R.id.btnIngles);
        btnPortugues = findViewById(R.id.btnPortugues);

        txtTituloIdioma = findViewById(R.id.txtTituloIdioma);
        cardIdiomaEs = findViewById(R.id.cardIdiomaEs);
        cardIdiomaEn = findViewById(R.id.cardIdiomaEn);
        cardIdiomaPt = findViewById(R.id.cardIdiomaPt);
        layoutRoot   = findViewById(R.id.layoutSeleccionIdiomaRoot);

        // micro animación al presionar
        setupButtonPressAnimation(btnEspanol);
        setupButtonPressAnimation(btnIngles);
        setupButtonPressAnimation(btnPortugues);

        // Guardar idioma seleccionado y volver al Splash
        btnEspanol.setOnClickListener(v -> cambiarIdioma("es"));
        btnIngles.setOnClickListener(v -> cambiarIdioma("en"));
        btnPortugues.setOnClickListener(v -> cambiarIdioma("pt"));

        // Animaciones de entrada
        prepararAnimacionesIniciales();
        animarEntradaSeleccionIdioma();
    }

    // ANIMACIONES DE ENTRADA

    private void prepararAnimacionesIniciales() {
        if (layoutRoot != null) {
            layoutRoot.setAlpha(1f);
        }

        if (txtTituloIdioma != null) {
            txtTituloIdioma.setAlpha(0f);
            txtTituloIdioma.setTranslationY(-120f);
        }

        if (cardIdiomaEs != null) {
            cardIdiomaEs.setAlpha(0f);
            cardIdiomaEs.setTranslationX(-220f);
            cardIdiomaEs.setScaleX(0.9f);
            cardIdiomaEs.setScaleY(0.9f);
        }

        if (cardIdiomaEn != null) {
            cardIdiomaEn.setAlpha(0f);
            cardIdiomaEn.setTranslationX(220f);
            cardIdiomaEn.setScaleX(0.9f);
            cardIdiomaEn.setScaleY(0.9f);
        }

        if (cardIdiomaPt != null) {
            cardIdiomaPt.setAlpha(0f);
            cardIdiomaPt.setTranslationY(220f);
            cardIdiomaPt.setScaleX(0.9f);
            cardIdiomaPt.setScaleY(0.9f);
        }
    }

    private void animarEntradaSeleccionIdioma() {
        DecelerateInterpolator desacelerar = new DecelerateInterpolator();
        OvershootInterpolator rebote = new OvershootInterpolator(1.1f);

        if (txtTituloIdioma != null) {
            txtTituloIdioma.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(80)
                    .setInterpolator(desacelerar)
                    .start();
        }

        if (cardIdiomaEs != null) {
            cardIdiomaEs.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(650)
                    .setStartDelay(200)
                    .setInterpolator(rebote)
                    .start();
        }

        if (cardIdiomaEn != null) {
            cardIdiomaEn.animate()
                    .alpha(1f)
                    .translationX(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(650)
                    .setStartDelay(280)
                    .setInterpolator(rebote)
                    .start();
        }

        if (cardIdiomaPt != null) {
            cardIdiomaPt.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(650)
                    .setStartDelay(360)
                    .setInterpolator(rebote)
                    .start();
        }
    }

    // ANIMACIÓN DE BOTONES (presión)

    private void setupButtonPressAnimation(Button button) {
        if (button == null) return;

        button.setOnTouchListener((View v, MotionEvent event) -> {
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

    // CAMBIO DE IDIOMA + FADE OUT

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
