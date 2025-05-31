// ChatActivity.java
package com.synunezcamacho.cuidame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.synunezcamacho.cuidame.Constants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.synunezcamacho.cuidame.R;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ChatActivity extends AppCompatActivity {

    private static final String SUPABASE_URL = "https://ieymwafslrvnvbneybgc.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlleW13YWZzbHJ2bnZibmV5YmdjIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NDcxNjgwMSwiZXhwIjoyMDYwMjkyODAxfQ.6O4seaPmMGH2hWm-ICUes5lVfNsKF8mWV0XVwY-9SYo";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private EditText messageInput;
    private Button sendButton;
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages = new ArrayList<>();
    private OkHttpClient client = new OkHttpClient();
    private String idContacto;
    private String nombreContacto;
    private ImageView btnAtras;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d("ChatActivity", "Intent extras: " + getIntent().getExtras());
        setContentView(R.layout.activity_chat);
        // Usa las claves constantes para obtener los extras
        idContacto = getIntent().getStringExtra(Constants.EXTRA_ID_CONTACTO);
        nombreContacto = getIntent().getStringExtra(Constants.EXTRA_NOMBRE_CONTACTO);


        Log.d("ChatActivity", "idContacto recibido: " + idContacto);
        Log.d("ChatActivity", "nombreContacto recibido: " + nombreContacto);

        if (idContacto == null || idContacto.trim().isEmpty()) {
            Log.e("ChatActivity", "idContacto es null o vacío");
            finish(); // finaliza si no hay ID válido
            return;
        }


        setTitle("Chat con " + nombreContacto);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        chatRecyclerView = findViewById(R.id.chat_recycler);
        btnAtras = findViewById(R.id.imgAtras);

        //boton para atras
        btnAtras.setOnClickListener(v -> {
            finish();
        });

        chatAdapter = new ChatAdapter(messages, "AndroidUser");

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        fetchMessages();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = messageInput.getText().toString();
                if (!content.isEmpty()) {
                    sendMessage(content);
                    messageInput.setText("");
                }
            }
        });
    }

    private void fetchUserName(Callback callback) {
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId == null) return;

        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/profiles?select=full_name&user_id=eq." + userId)
                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }


    private void sendMessage(String content) {
        fetchUserName(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SUPABASE", "No se pudo obtener el nombre", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String name = "Usuario";

                try {
                    JSONArray arr = new JSONArray(response.body().string());
                    if (arr.length() > 0) {
                        JSONObject obj = arr.getJSONObject(0);
                        name = obj.getString("full_name");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject json = new JSONObject();
                try {
                    json.put("username", name);
                    json.put("content", content);
                    json.put("contact_id", idContacto);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestBody body = RequestBody.create(json.toString(), JSON);
                Request request = new Request.Builder()
                        .url(SUPABASE_URL + "/rest/v1/messages")
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("SUPABASE", "Fallo al enviar mensaje", e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.i("SUPABASE", "Mensaje enviado: " + response.body().string());
                        fetchMessages();
                    }
                });
            }
        });
    }



    private void fetchMessages() {
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/messages?select=content,username,inserted_at&contact_id=eq." + idContacto)

                .addHeader("apikey", SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SUPABASE", "Failed to fetch messages", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    messages.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String content = obj.getString("content");
                        String username = obj.getString("username");
                        String insertedAt = obj.getString("inserted_at");
                        messages.add(new ChatMessage(username, content, insertedAt));
                    }
                    runOnUiThread(() -> chatAdapter.notifyDataSetChanged());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
