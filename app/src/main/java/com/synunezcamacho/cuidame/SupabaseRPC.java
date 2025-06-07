package com.synunezcamacho.cuidame;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupabaseRPC {

    private static final String TAG = "SupabaseRPC";
    private static final String BASE_URL = "https://ieymwafslrvnvbneybgc.supabase.co";
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImlleW13YWZzbHJ2bnZibmV5YmdjIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NDcxNjgwMSwiZXhwIjoyMDYwMjkyODAxfQ.6O4seaPmMGH2hWm-ICUes5lVfNsKF8mWV0XVwY-9SYo"; // clave pública o servicio

    public interface CallbackRPC {
        void onSuccess(String chatId);
        void onError(String error);
    }

    public static void obtenerOCrearChat(String usuarioA, String usuarioB, CallbackRPC callback) {
        OkHttpClient client = new OkHttpClient();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("usuario_a", usuarioA);
            jsonBody.put("usuario_b", usuarioB);
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError("Error armando JSON");
            return;
        }

        RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .addHeader("apikey", API_KEY)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error en llamada RPC", e);
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resp = response.body().string();
                    // La respuesta viene en formato JSON, ejemplo: {"obtener_o_crear_chat":"uuid-chat"}

                    try {
                        JSONObject jsonResponse = new JSONObject(resp);
                        String chatId = jsonResponse.getString("obtener_o_crear_chat");
                        callback.onSuccess(chatId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.onError("Error parseando respuesta");
                    }

                } else {
                    callback.onError("Error: " + response.message());
                }
            }
        });
    }
}
