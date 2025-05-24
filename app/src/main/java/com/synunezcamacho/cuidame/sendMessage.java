package com.synunezcamacho.cuidame;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class sendMessage {

    public void sendMessage(String content, String userId, String username) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        JSONObject json = new JSONObject();
        try {
            json.put("user_id", userId);
            json.put("username", username);
            json.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(), JSON);

        Request request = new Request.Builder()
                .url("https://https://ieymwafslrvnvbneybgc.supabase.co/rest/v1/messages")
                .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlleW13YWZzbHJ2bnZibmV5YmdjIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NDcxNjgwMSwiZXhwIjoyMDYwMjkyODAxfQ.6O4seaPmMGH2hWm-ICUes5lVfNsKF8mWV0XVwY-9SYo")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlleW13YWZzbHJ2bnZibmV5YmdjIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NDcxNjgwMSwiZXhwIjoyMDYwMjkyODAxfQ.6O4seaPmMGH2hWm-ICUes5lVfNsKF8mWV0XVwY-9SYo")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SUPABASE", "Failed to send message: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.i("SUPABASE", "Message sent successfully!");
                } else {
                    Log.e("SUPABASE", "Error: " + response.code() + " " + response.message());
                }
            }
        });
    }
}
