package com.synunezcamacho.cuidame;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Contacto extends AppCompatActivity {
    private RecyclerView recyclerMensaje;
    private ContactoAdapter adapter;
    private List<ContactoPreview> listaContactos;

    private OkHttpClient client = new OkHttpClient();

    // Pon aquí tu URL y API KEY correctas de Supabase
    private static final String SUPABASE_URL = "https://ieymwafslrvnvbneybgc.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlleW13YWZzbHJ2bnZibmV5YmdjIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NDcxNjgwMSwiZXhwIjoyMDYwMjkyODAxfQ.6O4seaPmMGH2hWm-ICUes5lVfNsKF8mWV0XVwY-9SYo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contacto);

        recyclerMensaje = findViewById(R.id.recyclerViewChats);
        recyclerMensaje.setLayoutManager(new LinearLayoutManager(this));

        listaContactos = new ArrayList<>();
        adapter = new ContactoAdapter(this, listaContactos);
        recyclerMensaje.setAdapter(adapter);

        fetchContactosDesdeSupabase();
    }

    private void fetchContactosDesdeSupabase() {
        // Consulta para obtener todos los mensajes ordenados por fecha ascendente (puedes cambiar a desc si quieres)
        String url = SUPABASE_URL + "/rest/v1/messages?select=contact_id,username,content,inserted_at&order=inserted_at.asc";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SUPABASE", "Error al obtener contactos", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("SUPABASE", "Respuesta no exitosa: " + response.code());
                    return;
                }

                String body = response.body().string();

                try {
                    JSONArray jsonArray = new JSONArray(body);

                    // Usamos un map para mantener solo el último mensaje por contacto
                    Map<String, ContactoPreview> mapContactos = new HashMap<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        String idContacto = obj.getString("contact_id");
                        String nombre = obj.getString("username");
                        String ultimoMensaje = obj.getString("content");
                        String hora = obj.getString("inserted_at");

                        // Para imagen, puedes usar una fija o mejorar asignando según contacto
                        int imagenPerfil = R.drawable.camara;

                        // Si el contacto no existe o el mensaje es más reciente, actualizamos
                        if (!mapContactos.containsKey(idContacto) || mapContactos.get(idContacto).getHora().compareTo(hora) < 0) {
                            mapContactos.put(idContacto, new ContactoPreview(idContacto, nombre, ultimoMensaje, hora, imagenPerfil));
                        }
                    }

                    listaContactos.clear();
                    listaContactos.addAll(mapContactos.values());

                    runOnUiThread(() -> adapter.notifyDataSetChanged());

                } catch (JSONException e) {
                    Log.e("SUPABASE", "Error parseando JSON", e);
                }
            }
        });
    }
}
