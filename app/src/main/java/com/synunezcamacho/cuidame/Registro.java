package com.synunezcamacho.cuidame;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import jp.wasabeef.glide.transformations.BlurTransformation;


public class Registro extends AppCompatActivity {


    EditText edtNombre, edtApellido,edtEmail, edtPassword, edtDireccion, edtTelefono;
    Button btnContinuar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        ImageView fondo = findViewById(R.id.fondoImagen);

        edtNombre = findViewById(R.id.nombre);
        edtApellido = findViewById(R.id.apellido);
        edtEmail = findViewById(R.id.email);
        edtPassword = findViewById(R.id.password);
        edtDireccion = findViewById(R.id.direccion);
        edtTelefono = findViewById(R.id.telefono);
        btnContinuar = findViewById(R.id.botonContinuar);

        //Obtener el perfil desde el Intent
        String perfilSeleccionado = getIntent().getStringExtra("perfil");

        //Uso del Glide para aplicar el desenfoque
        Glide.with(this)
                .load(R.drawable.fondo)
                .transform(new BlurTransformation(25))
                .into(fondo);
/*
        // Aquí  personalizar el formulario dependiendo del tipo de perfil
        if ("soyCuidador".equals(perfilSeleccionado)) {


        } else if ("buscarCuidador".equals(perfilSeleccionado)) {

        }

 */
    }
    public void registrarUsuario(View view) {

        // OBTENGO LOS VALORES
        String nombre = edtNombre.getText().toString();
        String apellido = edtApellido.getText().toString();
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        String direccion = edtDireccion.getText().toString();
        String telefono = edtTelefono.getText().toString();

        boolean todosDatos = !nombre.isEmpty() && !apellido.isEmpty() && !email.isEmpty() && !password.isEmpty() &&
                !direccion.isEmpty() && !telefono.isEmpty();

        // COMPRUEBO SI NO ESTAN VACIOS
        if (todosDatos) {

            if (!valida(email)) {
                verificarCampo(edtEmail, findViewById(R.id.asteriscoEmail), findViewById(R.id.invisibleCorreo));
                Toast.makeText(this, "Correo no válido", Toast.LENGTH_SHORT).show();

            }

        } else {
            // Comprobamos los campos vacíos
            verificarCampo(edtNombre, findViewById(R.id.asteriscoNombre), findViewById(R.id.invisibleNombre));
            verificarCampo(edtApellido, findViewById(R.id.asteriscoApellido), findViewById(R.id.invisibleApellido));
            verificarCampo(edtEmail, findViewById(R.id.asteriscoEmail), findViewById(R.id.invisibleCorreo));
            verificarCampo(edtDireccion, findViewById(R.id.asteriscoDireccion), findViewById(R.id.invisibleDireccion));
            verificarCampo(edtTelefono, findViewById(R.id.asteriscoTelefono), findViewById(R.id.invisibleTelefono));
            verificarCampo(edtPassword, findViewById(R.id.asteriscoPassword), findViewById(R.id.invisiblePassword));

        }

    }

    private boolean valida(String mail) {
        String[] dominiosPermitidos = {"@gmail.com", "@hotmail.com", "@outlook.com", "@icloud.com", "@educa.madrid.org"};

        for (String dominio : dominiosPermitidos) {
            if (mail.endsWith(dominio)) {
                return true;
            }
        }
        return false;
    }
    // Metodo para gestionar la visibilidad del asterisco
    private void verificarCampo(EditText editText, TextView asterisco, TextView label) {
        String texto =editText.getText().toString().trim();

        if (texto.isEmpty()) {
            asterisco.setVisibility(View.VISIBLE);
            label.setVisibility(View.GONE);

        } else {
            asterisco.setVisibility(View.GONE);
            label.setVisibility(View.VISIBLE);
        }
    }

}