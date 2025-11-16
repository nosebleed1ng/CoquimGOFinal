package com.interaccion.coquimgo;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer (menú lateral)
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Toggle (ícono del menú)
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Seleccionar item por defecto
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_lugares_turisticos);
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_lugares_turisticos) {

        } else if (id == R.id.nav_lugares_visitados) {
            startActivity(new Intent(this, LugaresVisitadosActivity.class));

        } else if (id == R.id.nav_lugares_favoritos) {
            startActivity(new Intent(this, LugaresFavoritosActivity.class));

        } else if (id == R.id.nav_configuracion) {
            startActivity(new Intent(this, ConfiguracionActivity.class));

        } else if (id == R.id.nav_mapa) {
            startActivity(new Intent(this, MapaActivity.class));
        }

        // Cerrar el Drawer después de la selección
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}

