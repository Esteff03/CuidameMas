package com.synunezcamacho.cuidame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class Login extends AppCompatActivity {

    private TextInputEditText email, password;
    private Button login, recuperarPassword;
    private ImageView fondo;

    private final String SUPABASE_URL = "https://ieymwafslrvnvbneybgc.supabase.co";
    private final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlleW13YWZzbHJ2bnZibmV5YmdjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDQ3MTY4MDEsImV4cCI6MjA2MDI5MjgwMX0.npx_XlGjkIhh0akIIekkIX1P69FBTNurdnsXhWo2B2o"; // Usa la API key pública (anon)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.button1);
        recuperarPassword = findViewById(R.id.button2);
        fondo = findViewById(R.id.fondoImagen);

        Glide.with(this)
                .load(R.drawable.fondo)
                .transform(new CenterCrop(), new BlurTransformation(10, 1))
                .into(fondo);

        login.setOnClickListener(view -> verificarCredencial());

        recuperarPassword.setOnClickListener(view ->
                Toast.makeText(Login.this, "En breve le contactaremos por correo.", Toast.LENGTH_LONG).show()
        );

    }

    private void verificarCredencial() {
        String correo = email.getText().toString().trim();
        String contraseña = password.getText().toString().trim();

        if (correo.isEmpty() || contraseña.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            String resultado;

            try {
                URL url = new URL(SUPABASE_URL + "/auth/v1/token?grant_type=password");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("apikey", API_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String body = "{\"email\":\"" + correo + "\",\"password\":\"" + contraseña + "\"}";
                OutputStream os = conn.getOutputStream();
                os.write(body.getBytes());
                os.flush();
                os.close();

                int code = conn.getResponseCode();

                if (code == 200) {
                    Scanner sc = new Scanner(conn.getInputStream());
                    StringBuilder sb = new StringBuilder();
                    while (sc.hasNext()) sb.append(sc.nextLine());
                    sc.close();

                    JSONObject response = new JSONObject(sb.toString());
                    String accessToken = response.getString("access_token");
                    String refreshToken = response.getString("refresh_token");
                    // Obtener el user_id del objeto "user"
                    String userId = response.getJSONObject("user").getString("id");

                    // Guardar en SharedPreferences
                    SharedPreferences.Editor editor = getSharedPreferences("session", MODE_PRIVATE).edit();
                    editor.putString("access_token", accessToken);
                    editor.putString("refresh_token", refreshToken);
                    editor.putString("user_id", userId);
                    editor.apply();
                    resultado = "success";
                } else if (code == 400) {
                    resultado = "invalid";
                } else {
                    resultado = "error";
                }

            } catch (Exception e) {
                e.printStackTrace();
                resultado = "exception";
            }

            String finalResultado = resultado;

            handler.post(() -> {
                switch (finalResultado) {
                    case "success":
                        Toast.makeText(Login.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, PerfilPublico.class));
                        finish();
                        break;
                    case "invalid":
                        Toast.makeText(Login.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(Login.this, "Error al conectar con Supabase", Toast.LENGTH_LONG).show();
                        break;
                }
            });
        });
    }
}
