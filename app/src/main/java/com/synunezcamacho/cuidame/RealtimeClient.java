package com.synunezcamacho.cuidame;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

public class RealtimeClient extends WebSocketClient {

    private final String apiKey;
    private final String chatId;
    private final RealtimeCallback callback;

    public RealtimeClient(URI serverUri, String apiKey, String chatId, RealtimeCallback callback) {
        super(serverUri);
        this.apiKey = apiKey;
        this.chatId = chatId;
        this.callback = callback;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("🔌 WebSocket conectado");

        try {
            // Paso 1: enviar token de acceso
            JSONObject accessPayload = new JSONObject();
            accessPayload.put("access_token", apiKey);

            JSONObject accessMsg = new JSONObject();
            accessMsg.put("topic", "realtime:public:mensajes");
            accessMsg.put("event", "access_token");
            accessMsg.put("payload", accessPayload);
            accessMsg.put("ref", "1");

            send(accessMsg.toString());

            // Paso 2: suscribirse a inserciones
            subscribeToInserts();

        } catch (JSONException e) {
            e.printStackTrace();
            System.err.println("❌ Error creando el mensaje JSON en onOpen: " + e.getMessage());
        }
    }

    @Override
    public void onMessage(String message) {
        try {
            JSONObject json = new JSONObject(message);
            String event = json.optString("event");

            if ("postgres_changes".equals(event)) {
                JSONObject payload = json.getJSONObject("payload");
                JSONObject newMessage = payload.getJSONObject("new");

                if (newMessage.optString("chat_id").equals(chatId)) {
                    callback.onNewMessage(newMessage);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error procesando mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("🔌 WebSocket cerrado: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("❌ WebSocket error: " + ex.getMessage());
        ex.printStackTrace();
    }

    public void subscribeToInserts() {
        try {
            JSONObject change = new JSONObject()
                    .put("event", "INSERT")
                    .put("schema", "public")
                    .put("table", "mensajes");

            JSONArray changes = new JSONArray().put(change);

            JSONObject config = new JSONObject()
                    .put("broadcast", new JSONObject().put("ack", true))
                    .put("postgres_changes", changes);

            JSONObject payload = new JSONObject().put("config", config);

            JSONObject subscribeMsg = new JSONObject();
            subscribeMsg.put("topic", "realtime:public:mensajes");
            subscribeMsg.put("event", "phx_join");
            subscribeMsg.put("payload", payload);
            subscribeMsg.put("ref", "2");

            send(subscribeMsg.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            System.err.println("❌ Error en subscribeToInserts(): " + e.getMessage());
        }
    }

    public interface RealtimeCallback {
        void onNewMessage(JSONObject message);
    }
}
