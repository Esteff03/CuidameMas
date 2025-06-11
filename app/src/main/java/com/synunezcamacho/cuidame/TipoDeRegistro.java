package com.synunezcamacho.cuidame;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import jp.wasabeef.glide.transformations.BlurTransformation;
import android.view.MotionEvent;
import android.view.View;


public class TipoDeRegistro extends AppCompatActivity {

    private Button botonCuidador, botonBuscoCuidador, botonRegistro;
    private String perfilSeleccionado = "";
    private ImageView imgAtras;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tipo_de_registro);

        ImageView fondo = findViewById(R.id.fondoImagen);

        botonCuidador = findViewById(R.id.botonCuidador);
        botonBuscoCuidador = findViewById(R.id.botonBuscoCuidador);
        botonRegistro = findViewById(R.id.botonRegistro);

        //Uso del Glide para aplicar el desenfoque
        Glide.with(this)
                .load(R.drawable.fondo)
                .transform(new BlurTransformation(25))
                .into(fondo);


        // Deselecciona si ya está seleccionado
        botonCuidador.setOnClickListener(view -> {
            if ("soyCuidador".equals(perfilSeleccionado)) {
                perfilSeleccionado = "";
            } else {
                perfilSeleccionado = "soyCuidador";
            }

            actualizarColoresBotones();
        });
        botonBuscoCuidador.setOnClickListener(view -> {
            if ("buscarCuidador".equals(perfilSeleccionado)) {
                perfilSeleccionado = "";
            } else {
                perfilSeleccionado = "buscarCuidador";
            }
            actualizarColoresBotones();
        });

        imgAtras = findViewById(R.id.btnAtras);

        //boton para atras
        imgAtras.setOnClickListener(v -> {
            Intent intent = new Intent(TipoDeRegistro.this, Welcome.class);
            startActivity(intent);
            finish();
        });




        botonCuidador = findViewById(R.id.botonCuidador);
        botonBuscoCuidador = findViewById(R.id.botonBuscoCuidador);
         botonRegistro = findViewById(R.id.botonRegistro);

// 👇 Aplicar animación táctil
        botonCuidador.setOnTouchListener(touchEffect);
        botonBuscoCuidador.setOnTouchListener(touchEffect);
        botonRegistro.setOnTouchListener(touchEffect);



        botonRegistro.setOnClickListener(view -> {
            if (!perfilSeleccionado.isEmpty()) {
                Intent intent = new Intent(TipoDeRegistro.this, Registro.class);
                intent.putExtra("perfil", perfilSeleccionado);
                startActivity(intent);

            } else {
                // Mostrar mensaje o advertencia si no se ha seleccionado un perfil
                Toast.makeText(this, "No has Seleccionado un Perfil", Toast.LENGTH_SHORT).show();
            }

        });
        actualizarColoresBotones();
    }
    private void actualizarColoresBotones() {
        int colorPrincipal = ContextCompat.getColor(this, R.color.principal);
        int colorCrema = ContextCompat.getColor(this, R.color.crema);

        if ("soyCuidador".equals(perfilSeleccionado)) {
                botonCuidador.setBackgroundColor(colorPrincipal);
                botonBuscoCuidador.setBackgroundColor(colorCrema);
        } else if ("buscarCuidador".equals(perfilSeleccionado)) {
                botonBuscoCuidador.setBackgroundColor(colorPrincipal);
                botonCuidador.setBackgroundColor(colorCrema);
        } else {
                botonCuidador.setBackgroundColor(colorCrema);
                botonBuscoCuidador.setBackgroundColor(colorCrema);
        }


    }

    View.OnTouchListener touchEffect = (v, event) -> {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                break;
        }
        return false; // importante: deja que el click se siga ejecutando
    };

}