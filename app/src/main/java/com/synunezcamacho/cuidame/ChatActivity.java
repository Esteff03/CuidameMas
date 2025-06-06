package com.synunezcamacho.cuidame;

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

    private String usuarioActualId;      // ← Pasa tu UUID real
    private String usuarioOtroId; // ← Pasa el UUID del receptor
    private String chatId = "";

    private RealtimeClient realtimeClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecycler = findViewById(R.id.chat_recycler);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        chatAdapter = new ChatAdapter(mensajes, usuarioActualId);
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatRecycler.setAdapter(chatAdapter);

        usuarioActualId = getIntent().getStringExtra("UUID_ACTUAL");
        usuarioOtroId = getIntent().getStringExtra("UUID_DESTINATARIO");

        if (usuarioActualId == null || usuarioOtroId == null) {
            Toast.makeText(this, "Error al obtener los datos del chat", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        SupabaseService.obtenerOcrearChat(SUPABASE_URL, SUPABASE_API_KEY, usuarioActualId, usuarioOtroId, new SupabaseService.Callback() {
            @Override
            public void onSuccess(String chatUuid) {
                chatId = chatUuid;
                cargarMensajes();

                try {
                    URI uri = new URI("wss://ieymwafslrvnvbneybgc.supabase.co/realtime/v1/websocket?apikey=" + SUPABASE_API_KEY + "&vsn=1.0.0");

                    realtimeClient = new RealtimeClient(uri, SUPABASE_API_KEY, chatId, new RealtimeClient.RealtimeCallback() {
                        @Override
                        public void onNewMessage(JSONObject json) {
                            runOnUiThread(() -> {
                                try {
                                    Mensaje nuevo = new Mensaje(
                                            json.getString("id"),
                                            json.getString("contenido"),
                                            json.getString("remitente_id"),
                                            json.getString("enviado_en"),
                                            json.getString("hora")
                                    );
                                    mensajes.add(nuevo);
                                    chatAdapter.notifyItemInserted(mensajes.size() - 1);
                                    chatRecycler.scrollToPosition(mensajes.size() - 1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    });

                    realtimeClient.connect();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ChatActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        sendButton.setOnClickListener(v -> {
            String contenido = messageInput.getText().toString().trim();
            if (!contenido.isEmpty()) {
                SupabaseService.enviarMensaje(SUPABASE_URL, SUPABASE_API_KEY, chatId, usuarioActualId, contenido, new SupabaseService.Callback() {
                    @Override
                    public void onSuccess(String response) {
                        messageInput.setText("");
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(ChatActivity.this, "No se pudo enviar", Toast.LENGTH_SHORT).show();
                        Log.e("ChatActivity", "Error al enviar mensaje: " + error);
                    }
                });
            }
        });
    }

    private void cargarMensajes() {
        SupabaseService.obtenerMensajes(SUPABASE_URL, SUPABASE_API_KEY, chatId, new SupabaseService.Callback() {
            @Override
            public void onSuccess(String json) {
                try {
                    mensajes.clear();
                    JSONArray array = new JSONArray(json);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        Mensaje m = new Mensaje(
                                obj.getString("id"),
                                obj.getString("contenido"),
                                obj.getString("remitente_id"),
                                obj.getString("enviado_en"),
                                obj.getString("hora")
                        );
                        mensajes.add(m);
                    }
                    chatAdapter.notifyDataSetChanged();
                    chatRecycler.scrollToPosition(mensajes.size() - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ChatActivity.this, "Error al cargar mensajes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realtimeClient != null) {
            realtimeClient.close();
        }
    }
}
