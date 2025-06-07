package com.synunezcamacho.cuidame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String SUPABASE_URL = "https://ieymwafslrvnvbneybgc.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlleW13YWZzbHJ2bnZibmV5YmdjIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NDcxNjgwMSwiZXhwIjoyMDYwMjkyODAxfQ.6O4seaPmMGH2hWm-ICUes5lVfNsKF8mWV0XVwY-9SYo";

    private RecyclerView chatRecycler;
    private EditText messageInput;
    private Button sendButton;

    private ChatAdapter chatAdapter;
    private final List<Mensaje> mensajes = new ArrayList<>();

    private String usuarioActualId;
    private String usuarioOtroId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecycler = findViewById(R.id.chat_recycler);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        usuarioActualId = getIntent().getStringExtra("UUID_ACTUAL");
        usuarioOtroId = getIntent().getStringExtra("UUID_DESTINATARIO");

        if (usuarioActualId == null || usuarioOtroId == null) {
            Toast.makeText(this, "Error al obtener los datos del chat", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        chatAdapter = new ChatAdapter(mensajes, usuarioActualId);
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatRecycler.setAdapter(chatAdapter);

        cargarMensajes();

        sendButton.setOnClickListener(v -> {
            String contenido = messageInput.getText().toString().trim();
            if (!contenido.isEmpty()) {
                SupabaseService.enviarMensajeEnChat(
                        SUPABASE_URL, SUPABASE_API_KEY,
                        usuarioActualId, usuarioOtroId, contenido,
                        new SupabaseService.Callback() {
                            @Override
                            public void onSuccess(String response) {
                                messageInput.setText("");
                                cargarMensajes();
                            }
                            @Override
                            public void onError(String error) {
                                Toast.makeText(ChatActivity.this, "No se pudo enviar", Toast.LENGTH_SHORT).show();
                                Log.e("ChatActivity", "Error al enviar mensaje: " + error);
                            }
                        });
            } else {
                Toast.makeText(this, "Escribe un mensaje antes de enviar.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarMensajes() {
        SupabaseService.obtenerMensajesChat(
                SUPABASE_URL, SUPABASE_API_KEY,
                usuarioActualId, usuarioOtroId,
                new SupabaseService.Callback() {
                    @Override
                    public void onSuccess(String json) {
                        try {
                            mensajes.clear();
                            JSONArray array = new JSONArray(json);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                Mensaje m = new Mensaje(
                                        obj.getString("id"),
                                        obj.getString("content"),
                                        obj.getString("user_id"),
                                        obj.getString("inserted_at"),
                                        "" // campo hora vacío o extraer de inserted_at si quieres
                                );
                                mensajes.add(m);
                            }
                            runOnUiThread(() -> {
                                chatAdapter.notifyDataSetChanged();
                                chatRecycler.scrollToPosition(mensajes.size() - 1);
                            });
                        } catch (Exception e) {
                            Log.e("ChatActivity", "Error parseando JSON mensajes", e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            Toast.makeText(ChatActivity.this, "Error al cargar mensajes: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }
}