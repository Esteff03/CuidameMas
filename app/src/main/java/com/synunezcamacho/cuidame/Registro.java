package com.synunezcamacho.cuidame;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class Registro extends AppCompatActivity {
    EditText edtNombre, edtApellido, edtEmail, edtPassword, edtDireccion, edtTelefono;
    Button btnContinuar;

    // Supabase Auth
    private static final String SUPABASE_URL = "https://ieymwafslrvnvbneybgc.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlleW13YWZzbHJ2bnZibmV5YmdjIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NDcxNjgwMSwiZXhwIjoyMDYwMjkyODAxfQ.6O4seaPmMGH2hWm-ICUes5lVfNsKF8mWV0XVwY-9SYo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);

        ImageView fondo = findViewById(R.id.fondoImagen);

        edtNombre = findViewById(R.id.nombre);
        edtApellido = findViewById(R.id.apellido);
        edtEmail = findViewById(R.id.email);
        edtPassword = findViewById(R.id.password);
        edtDireccion = findViewById(R.id.direccion);
        edtTelefono = findViewById(R.id.telefono);
        btnContinuar = findViewById(R.id.botonContinuar);

        // Fondo desenfocado
        Glide.with(this)
                .load(R.drawable.fondo)
                .transform(new BlurTransformation(25))
                .into(fondo);

        String perfilSeleccionado = getIntent().getStringExtra("perfil");

        btnContinuar.setOnClickListener(view -> {
            Usuario getActualUser = registrarUsuario();
            if (getActualUser != null) {
                getActualUser.setCuidado("soyCuidador".equals(perfilSeleccionado));
                registrarEnSupabase(getActualUser, perfilSeleccionado);
            }
        });
    }

    public Usuario registrarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNombre(edtNombre.getText().toString());
        usuario.setApellido(edtApellido.getText().toString());
        usuario.setEmail(edtEmail.getText().toString());
        usuario.setPassword(edtPassword.getText().toString());
        usuario.setDireccion(edtDireccion.getText().toString());
        usuario.setTelefono(edtTelefono.getText().toString());

        if (validaPerfil(usuario)) {
            if (!validaCorreo(usuario.getEmail())) {
                verificarCampo(edtEmail, findViewById(R.id.asteriscoEmail), findViewById(R.id.invisibleCorreo));
                Toast.makeText(this, "Correo no válido", Toast.LENGTH_SHORT).show();
                return null;
            }
            if (!validaTelefono(usuario.getTelefono())) {
                verificarCampo(edtTelefono, findViewById(R.id.asteriscoTelefono), findViewById(R.id.invisibleTelefono));
                Toast.makeText(this, "Número de teléfono no válido", Toast.LENGTH_SHORT).show();
                return null;
            }

            return usuario;
        } else {
            verificarCampo(edtNombre, findViewById(R.id.asteriscoNombre), findViewById(R.id.invisibleNombre));
            verificarCampo(edtApellido, findViewById(R.id.asteriscoApellido), findViewById(R.id.invisibleApellido));
            verificarCampo(edtEmail, findViewById(R.id.asteriscoEmail), findViewById(R.id.invisibleCorreo));
            verificarCampo(edtDireccion, findViewById(R.id.asteriscoDireccion), findViewById(R.id.invisibleDireccion));
            verificarCampo(edtTelefono, findViewById(R.id.asteriscoTelefono), findViewById(R.id.invisibleTelefono));
            verificarCampo(edtPassword, findViewById(R.id.asteriscoPassword), findViewById(R.id.invisiblePassword));
        }
        return null;
    }

    private void registrarEnSupabase(Usuario usuario, String perfilSeleccionado) {
        new Thread(() -> {
            try {
                URL url = new URL(SUPABASE_URL + "/auth/v1/signup");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("apikey", SUPABASE_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_KEY);
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("email", usuario.getEmail());
                json.put("password", usuario.getPassword());

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes());
                os.close();

                int responseCode = conn.getResponseCode();
                StringBuilder response = new StringBuilder();
                Scanner scanner = (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED)
                        ? new Scanner(conn.getInputStream())
                        : new Scanner(conn.getErrorStream());

                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                String responseStr = response.toString();

                runOnUiThread(() -> {
                    if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                        Toast.makeText(this, "¡Cuenta creada exitosamente!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Registro.this, Perfil.class);
                        intent.putExtra("perfil", usuario);
                        startActivity(intent);
                    } else {
                        String msg = parseErrorMessage(responseStr);
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error de red: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private String parseErrorMessage(String responseStr) {
        try {
            JSONObject errorJson = new JSONObject(responseStr);
            String msg = errorJson.optString("msg", errorJson.optString("message", errorJson.optString("error_description", "")));
            String errorCode = errorJson.optString("error_code", "");

            switch (errorCode) {
                case "email_address_invalid":
                    return "El correo electrónico no es válido.";
                case "email_already_registered":
                    return "Correo ya registrado. Intenta iniciar sesión.";
                case "invalid_password":
                    return "Contraseña no válida.";
                default:
                    return !msg.isEmpty() ? msg : "Error al crear cuenta.";
            }
        } catch (Exception e) {
            return "Error inesperado al crear cuenta.";
        }
    }

    private boolean validaPerfil(Usuario usuario) {
        return !usuario.getNombre().isEmpty() &&
                !usuario.getApellido().isEmpty() &&
                !usuario.getEmail().isEmpty() &&
                !usuario.getDireccion().isEmpty() &&
                !usuario.getPassword().isEmpty() &&
                !usuario.getTelefono().isEmpty();
    }

    private boolean validaCorreo(String mail) {
        mail = mail.trim().toLowerCase();
        String[] dominiosPermitidos = {"@gmail.com", "@hotmail.com", "@outlook.com", "@icloud.com", "@educa.madrid.org"};
        for (String dominio : dominiosPermitidos) {
            if (mail.endsWith(dominio)) {
                return true;
            }
        }
        return false;
    }

    private boolean validaTelefono(String telefono) {
        return telefono.matches("^[6-9]\\d{8}$");
    }

    private void verificarCampo(EditText editText, TextView asterisco, TextView label) {
        String texto = editText.getText().toString().trim();
        if (texto.isEmpty()) {
            asterisco.setVisibility(View.VISIBLE);
            label.setVisibility(View.GONE);
        } else {
            asterisco.setVisibility(View.GONE);
            label.setVisibility(View.VISIBLE);
        }
    }
}
