package com.interaccion.coquimgo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // Header views
    private ImageView imgLogoDrawer;
    private TextView txtDrawerTitle, txtDrawerSubtitle;

    // Contenido principal
    private View contentRootMain;

    private boolean drawerAnimatedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer y NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Root del contenido
        contentRootMain = findViewById(R.id.contentRootMain);

        // Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Header del NavigationView
        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            imgLogoDrawer   = headerView.findViewById(R.id.headerLogo);
            txtDrawerTitle  = headerView.findViewById(R.id.headerTitle);
            txtDrawerSubtitle = headerView.findViewById(R.id.headerSubtitle);
        }

        // Preparar animaciones iniciales
        prepararAnimacionesDrawer();
        setupDrawerAnimationListener();

        // Item por defecto
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_lugares_turisticos);
        }

        // Back
        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                        } else {
                            setEnabled(false);
                            getOnBackPressedDispatcher().onBackPressed();
                        }
                    }
                });
    }

    // Animaciones
    private void prepararAnimacionesDrawer() {
        // Header
        if (txtDrawerTitle != null) {
            txtDrawerTitle.setAlpha(0f);
            txtDrawerTitle.setTranslationY(-40f);
        }
        if (txtDrawerSubtitle != null) {
            txtDrawerSubtitle.setAlpha(0f);
            txtDrawerSubtitle.setTranslationY(-20f);
        }
        if (imgLogoDrawer != null) {
            imgLogoDrawer.setAlpha(0f);
            imgLogoDrawer.setScaleX(0.6f);
            imgLogoDrawer.setScaleY(0.6f);
        }

        // Items del menú
        ViewGroup menuView = (ViewGroup) navigationView.getChildAt(0);
        if (menuView != null) {
            for (int i = 0; i < menuView.getChildCount(); i++) {
                View child = menuView.getChildAt(i);
                if (child == navigationView.getHeaderView(0)) continue; // saltar header

                child.setAlpha(0f);
                child.setTranslationX(40f);
            }
        }
    }

    private void setupDrawerAnimationListener() {
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // Efecto de scale del contenido principal
                if (contentRootMain != null) {
                    float scale = 1f - (slideOffset * 0.06f);
                    contentRootMain.setScaleX(scale);
                    contentRootMain.setScaleY(scale);
                    contentRootMain.setTranslationX(drawerView.getWidth() * slideOffset * 0.15f);
                }
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                animarContenidoDrawer();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // Reset scale al cerrar
                if (contentRootMain != null) {
                    contentRootMain.setScaleX(1f);
                    contentRootMain.setScaleY(1f);
                    contentRootMain.setTranslationX(0f);
                }
            }
        });
    }

    private void animarContenidoDrawer() {
        if (drawerAnimatedOnce) return;
        drawerAnimatedOnce = true;

        DecelerateInterpolator desacelerar = new DecelerateInterpolator();
        OvershootInterpolator rebote = new OvershootInterpolator(1.1f);

        if (txtDrawerTitle != null) {
            txtDrawerTitle.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(450)
                    .setStartDelay(50)
                    .setInterpolator(desacelerar)
                    .start();
        }

        if (txtDrawerSubtitle != null) {
            txtDrawerSubtitle.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(450)
                    .setStartDelay(120)
                    .setInterpolator(desacelerar)
                    .start();
        }

        if (imgLogoDrawer != null) {
            imgLogoDrawer.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(600)
                    .setStartDelay(200)
                    .setInterpolator(rebote)
                    .start();
        }

        // Items del menú en cascada
        ViewGroup menuView = (ViewGroup) navigationView.getChildAt(0);
        if (menuView != null) {
            long baseDelay = 260;
            for (int i = 0; i < menuView.getChildCount(); i++) {
                View child = menuView.getChildAt(i);
                if (child == navigationView.getHeaderView(0)) continue;

                long delay = baseDelay + i * 70L;

                child.animate()
                        .alpha(1f)
                        .translationX(0f)
                        .setDuration(350)
                        .setStartDelay(delay)
                        .setInterpolator(desacelerar)
                        .start();
            }
        }
    }

    // Navegación
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_lugares_turisticos) {
            startActivity(new Intent(this, LugaresTuristicosActivity.class));

        } else if (id == R.id.nav_lugares_visitados) {
            startActivity(new Intent(this, LugaresVisitadosActivity.class));

        } else if (id == R.id.nav_lugares_favoritos) {
            startActivity(new Intent(this, LugaresFavoritosActivity.class));

        } else if (id == R.id.nav_configuracion) {
            startActivity(new Intent(this, ConfiguracionActivity.class));

        } else if (id == R.id.nav_mapa) {
            startActivity(new Intent(this, MapaActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
