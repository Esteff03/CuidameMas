package com.synunezcamacho.cuidame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Contacto extends AppCompatActivity {

    private RecyclerView recyclerMensaje;
    private ContactoAdapter adapter;
    private List<ContactoPreview> listaContactos;

    // Supabase
    private static final String SUPABASE_URL = "https://ieymwafslrvnvbneybgc.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlleW13YWZzbHJ2bnZibmV5YmdjIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NDcxNjgwMSwiZXhwIjoyMDYwMjkyODAxfQ.6O4seaPmMGH2hWm-ICUes5lVfNsKF8mWV0XVwY-9SYo";
    private ImageView nmensajes;

    private BottomNavigationView botonNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contacto);
        nmensajes = findViewById(R.id.nmensajes);

        recyclerMensaje = findViewById(R.id.recyclerViewChats);
        recyclerMensaje.setLayoutManager(new LinearLayoutManager(this));

        listaContactos = new ArrayList<>();
        adapter = new ContactoAdapter(this, listaContactos);
        recyclerMensaje.setAdapter(adapter);

        // Obtener user_id de SharedPreferences
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId != null) {
            // Cargar mensajes para ese usuario
            cargarMensajesDeUsuario(userId);
        } else {
            Toast.makeText(this, "No se encontró sesión activa", Toast.LENGTH_SHORT).show();
        }

        //actividad de nav_menu
        botonNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.page_mapa) {
                startActivity(new Intent(Contacto.this, Mapa.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.page_perfil) {
                startActivity(new Intent(Contacto.this, PerfilPublico.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.page_chat) {

                return true;
            }
            return false;
        });
    }

    private void cargarMensajesDeUsuario(String userId) {
        Log.d("Contacto", "User ID para consulta: " + userId);
        new Thread(() -> {
            try {
                // Endpoint para obtener mensajes filtrados por contact_id = userId
                // Aquí se asume que el nombre de la tabla en supabase es "messages"
                // y que quieres los últimos mensajes ordenados por insert_at desc

                String urlStr = SUPABASE_URL + "/rest/v1/mensajes?remitente_id=eq." + userId + "&select=*&order=enviado_en.desc";
                URL url = new URL(urlStr);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("apikey", SUPABASE_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + SUPABASE_KEY);
                conn.setRequestProperty("Accept", "application/json");

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    Scanner scanner = new Scanner(is);
                    StringBuilder response = new StringBuilder();
                    while (scanner.hasNext()) {
                        response.append(scanner.nextLine());
                    }
                    scanner.close();

                    JSONArray jsonArray = new JSONArray(response.toString());

                    listaContactos.clear();

                    // Para evitar mostrar mensajes duplicados del mismo contacto,
                    // podemos usar un Map o HashSet para filtrar contactos únicos,
                    // pero por simplicidad agregamos todos aquí

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject mensaje = jsonArray.getJSONObject(i);
                        String idContacto = mensaje.getString("contact_id");
                        String nombreUsuario = mensaje.getString("username");
                        String ultimoMensaje = mensaje.getString("content");
                        String hora = mensaje.getString("insert_at"); // aquí podrías formatear la fecha/hora

                        // Como no tienes imagen real, usa tu drawable por defecto
                        int imagenPerfil = R.drawable.camara;

                        ContactoPreview contacto = new ContactoPreview(idContacto, nombreUsuario, ultimoMensaje, hora, imagenPerfil);
                        listaContactos.add(contacto);
                    }

                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        if (listaContactos.isEmpty()) {
                            findViewById(R.id.nmensajes).setVisibility(View.VISIBLE);
                            recyclerMensaje.setVisibility(View.GONE);
                        } else {
                            findViewById(R.id.nmensajes).setVisibility(View.GONE);
                            recyclerMensaje.setVisibility(View.VISIBLE);
                        }
                    });

                } else {
                    Scanner scanner = new Scanner(conn.getErrorStream());
                    StringBuilder errorResponse = new StringBuilder();
                    while (scanner.hasNext()) {
                        errorResponse.append(scanner.nextLine());
                    }
                    scanner.close();
                    Log.e("Supabase", "Error HTTP: " + responseCode);
                    Log.e("Supabase", "Error response: " + errorResponse.toString());
                    runOnUiThread(() -> Toast.makeText(this, "Error al cargar mensajes", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();

    }
}
