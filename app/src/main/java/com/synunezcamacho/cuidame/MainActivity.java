package com.synunezcamacho.cuidame;
import com.synunezcamacho.cuidame.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.page_home) {
                    Intent intent = new Intent(MainActivity.this, Mapa.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.page_fav) {
                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.page_search) {
                    Intent intent = new Intent(MainActivity.this, PerfilPublico.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }

        });
    }
}
