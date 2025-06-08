package com.synunezcamacho.cuidame;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupabaseService {

    // Interfaz de callback personalizada
    public interface SupabaseCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json");

    // Método para enviar un mensaje a la tabla messages
    public static void enviarMensajeEnChat(String supabaseUrl, String apiKey,
                                           String remitenteId, String destinatarioId,
                                           String contenido, String username,
                                           SupabaseCallback callback) {

        String url = supabaseUrl + "/rest/v1/messages";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("user_id", remitenteId);
            jsonBody.put("contact_id", destinatarioId);
            jsonBody.put("content", contenido);
            jsonBody.put("username", username);
        } catch (Exception e) {
            callback.onError("Error creando JSON: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Error de red: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    callback.onSuccess(responseBody);
                } else {
                    callback.onError("Error HTTP: " + response.code() + " - " + responseBody);
                }
            }
        });
    }

    public static void obtenerMensajesChat(String supabaseUrl, String apiKey,
                                           String usuario1, String usuario2,
                                           SupabaseCallback callback) {
        String url = supabaseUrl + "/rest/v1/messages?" +
                "select=*&" +
                "or=(and(user_id.eq." + usuario1 + ",contact_id.eq." + usuario2 + ")," +
                "and(user_id.eq." + usuario2 + ",contact_id.eq." + usuario1 + "))" +
                "&order=inserted_at.asc";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", apiKey)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Accept", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Fallo de red: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";
                Log.d("SupabaseService", "Respuesta del servidor: " + responseBody);

                if (response.isSuccessful()) {
                    callback.onSuccess(responseBody);
                } else {
                    callback.onError("Error HTTP " + response.code() + ": " + responseBody);
                }
            }
        });
    }
}
