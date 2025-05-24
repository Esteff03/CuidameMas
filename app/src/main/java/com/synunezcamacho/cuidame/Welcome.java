package com.synunezcamacho.cuidame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);


        ImageView fondo = findViewById(R.id.fondo);

        Glide.with(this)
                .load(R.drawable.fondo) // Tu imagen de fondo
                .transform(new CenterCrop(), new BlurTransformation(10, 1)) // <-- Aquí aplicamos el desenfoque
                .into(fondo);

        Button registrarse = findViewById(R.id.btnRegistrarse);
        Button IniciarSesion = findViewById(R.id.btnIniciarSesion);

        registrarse.setOnClickListener(v -> {
            Intent intent = new Intent(Welcome.this, TipoDeRegistro.class); // Cambia RegistroActivity por la clase destino
            startActivity(intent);
        });

        IniciarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(Welcome.this, Login.class); // Cambia LoginActivity por la clase destino
            startActivity(intent);
        });
    }


}