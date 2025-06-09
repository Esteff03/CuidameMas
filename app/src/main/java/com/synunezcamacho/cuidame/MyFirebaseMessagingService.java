package com.synunezcamacho.cuidame;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "chat_channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        crearCanalNotificacion();

        String title = remoteMessage.getNotification() != null ?
                remoteMessage.getNotification().getTitle() : "Nuevo mensaje";

        String body = remoteMessage.getNotification() != null ?
                remoteMessage.getNotification().getBody() : "Tienes un nuevo mensaje";

        String uuidActual = remoteMessage.getData().get("uuid_actual");
        String uuidDestinatario = remoteMessage.getData().get("uuid_destinatario");
        String usernameActual = remoteMessage.getData().get("username_actual");

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("UUID_ACTUAL", uuidDestinatario);
        intent.putExtra("UUID_DESTINATARIO", uuidActual);
        intent.putExtra("USERNAME_ACTUAL", usernameActual);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_chat)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // No tienes permiso, sal sin notificar
                return;
            }
        }
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(1001, builder.build());

    }

    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Mensajes";
            String description = "Notificaciones de chat";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        // TODO: Envía el token a Supabase si aún no lo haces
    }
}
