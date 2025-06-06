package com.synunezcamacho.cuidame;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.android.material.textfield.TextInputEditText;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class Login extends AppCompatActivity {
    private TextInputEditText email, password;
    private Button login, recuperarPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Referencia al ImageView del fondo
        ImageView fondo = findViewById(R.id.fondoImagen);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.button1);
        recuperarPassword = findViewById(R.id.button2);

        Glide.with(this)
                .load(R.drawable.fondo) // Tu imagen de fondo
                .transform(new CenterCrop(), new BlurTransformation(10, 1)) // <-- Aquí aplicamos el desenfoque
                .into(fondo);

        //inicar sesion
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarCredencial();
            }
        });

        //recuperar contraseña
    }

    private void verificarCredencial(){
        String correo = email.getText().toString().trim();
        String contraseña = password.getText().toString().trim();

        if (correo.isEmpty() || contraseña.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simulación: credenciales correctas
        if (correo.equals("usuario@ejemplo.com") && contraseña.equals("1234")) {
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
            // Redirigir a la siguiente pantalla
            // startActivity(new Intent(this, Inicio.class));
        } else {
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
        }
    }
}
