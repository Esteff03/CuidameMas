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
import java.net.URLEncoder;
import java.util.Scanner;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class Registro extends AppCompatActivity {
    EditText edtNombre, edtApellido, edtEmail, edtPassword, edtDireccion, edtTelefono;
    Button btnContinuar;


    // Supabase Auth
    private static final String SUPABASE_URL = "https://ieymwafslrvnvbneybgc.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlleW13YWZzbHJ2bnZibmV5YmdjIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NDcxNjgwMSwiZXhwIjoyMDYwMjkyODAxfQ.6O4seaPmMGH2hWm-ICUes5lVfNsKF8mWV0XVwY-9SYo";
    private static final String GOOGLE_GEOCODING_API_KEY = "AIzaSyDDzubmOggWoNZguBTSnkug-U5y3AWicOE";

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
            if (!validaNombreApellido(usuario.getNombre())) {
                Toast.makeText(this, "Nombre no válido", Toast.LENGTH_SHORT).show();
                return null;
            }

            if (!validaNombreApellido(usuario.getApellido())) {
                Toast.makeText(this, "Apellido no válido", Toast.LENGTH_SHORT).show();
                return null;
            }
            if (!validaCorreo(usuario.getEmail())) {
                verificarCampo(edtEmail, findViewById(R.id.asteriscoEmail), findViewById(R.id.invisibleCorreo));
                Toast.makeText(this, "Correo no válido", Toast.LENGTH_SHORT).show();
                return null;
            }
            if (!validaTelefono(usuario.getTelefono())){
                Toast.makeText(this, "Telefono no válido", Toast.LENGTH_SHORT).show();
                return null;
            }
            if (!validaDireccion(usuario.getDireccion())) {
                Toast.makeText(this, "Dirección no válida", Toast.LENGTH_SHORT).show();
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

                try {
                    JSONObject jsonResponse = new JSONObject(responseStr);
                    JSONObject user = jsonResponse.optJSONObject("user");
                    if (user != null) {
                        String userId = user.getString("id");

                        // Guardar en SharedPreferences
                        getSharedPreferences("session", MODE_PRIVATE)
                                .edit()
                                .putString("user_id", userId)
                                .apply();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                runOnUiThread(() -> {
                    if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                        Toast.makeText(this, "¡Cuenta creada exitosamente!", Toast.LENGTH_SHORT).show();
                        guardarDatosUsuarioEnTabla(usuario, perfilSeleccionado); // <--- Agregado aquí
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
                case "signup_rate_limit_exceeded":
                    return "Has alcanzado el límite de registros. Intenta más tarde.";
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
        String[] dominiosPermitidos = {"@gmail.com", "@hotmail.com", "@outlook.com", "@icloud.com", "@educa.madrid.org"};
        for (String dominio : dominiosPermitidos) {
            if (mail.endsWith(dominio)) {
                return true;
            }
        }
        return false;
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
    private boolean validaTelefono(String telefono){
        telefono = edtTelefono.getText().toString().trim();
        String patronMovilEspaña = "^[67]\\d{8}$";

        if (telefono.matches(patronMovilEspaña)) {
            return true;
        }
        return false;
    }
    // validacion
    private boolean validaNombreApellido(String texto) {
        return texto.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s]{2,30}$");
    }
    private boolean validaDireccion(String direccion) {
        return direccion.matches("^[\\wÁÉÍÓÚáéíóúÑñ0-9\\s,\\.\\-#]{5,60}$");
    }

    private void guardarDatosUsuarioEnTabla(Usuario usuario, String perfilSeleccionado) {
        new Thread(() -> {
            try {
                double[] coordenadas = obtenerCoordenadasGoogle(usuario.getDireccion());

                String userId = getSharedPreferences("session", MODE_PRIVATE)
                        .getString("user_id", null);

                JSONObject json = new JSONObject(); // <-- Mueve esto antes de usar json.put()

                if (userId != null) {
                    json.put("id", userId); // Asegúrate que tu tabla lo acepta como PK o FK
                }

                json.put("Nombre", usuario.getNombre());
                json.put("Telefono", usuario.getTelefono());
                json.put("Rol", "soyCuidador".equals(perfilSeleccionado) ? "Cuidador" : "Paciente");
                json.put("Direccion", usuario.getDireccion());
                json.put("Barrio", ""); // agregar si tienes
                if (coordenadas != null) {
                    json.put("Latitud", coordenadas[0]);
                    json.put("Longitud", coordenadas[1]);
                }

                URL url = new URL(SUPABASE_URL + "/rest/v1/Users");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("apikey", SUPABASE_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_KEY);
                conn.setRequestProperty("Prefer", "return=minimal");
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes());
                os.close();

                int responseCode = conn.getResponseCode();

                runOnUiThread(() -> {
                    if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                        Toast.makeText(this, "Datos y coordenadas guardados correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error al guardar datos en Users", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error al guardar coordenadas: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private double[] obtenerCoordenadasGoogle(String direccion) {
        try {
            String query = URLEncoder.encode(direccion, "UTF-8");
            String urlStr = "https://maps.googleapis.com/maps/api/geocode/json?address=" + query + "&key=" + GOOGLE_GEOCODING_API_KEY;
            URL url = new URL(urlStr);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            JSONObject json = new JSONObject(response.toString());
            if ("OK".equals(json.getString("status"))) {
                JSONObject location = json.getJSONArray("results")
                        .getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONObject("location");
                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");
                return new double[]{lat, lng};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // si falla
    }




}

