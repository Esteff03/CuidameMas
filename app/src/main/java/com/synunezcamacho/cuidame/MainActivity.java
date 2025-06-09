package com.synunezcamacho.cuidame;
import com.google.firebase.FirebaseApp;
import com.synunezcamacho.cuidame.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        FirebaseApp.initializeApp(this);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Log.d("MainActivity", "Item seleccionado ID: " + id); // Log general

                if (id == R.id.page_mapa) {
                    Log.d("MainActivity", "Opción: Mapa seleccionada");
                    Intent intent = new Intent(MainActivity.this, Mapa.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.page_chat) {
                    Log.d("MainActivity", "Opción: Chat seleccionada");
                    Intent intent = new Intent(MainActivity.this, Contacto.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.page_perfil) {
                    Log.d("MainActivity", "Opción: Perfil seleccionada");
                    Intent intent = new Intent(MainActivity.this, PerfilPublico.class);
                    startActivity(intent);
                    return true;
                }

                Log.d("MainActivity", "Opción no reconocida");
                return false;
            }
        });
    }
}
