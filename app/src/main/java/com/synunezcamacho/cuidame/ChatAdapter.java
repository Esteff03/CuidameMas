package com.synunezcamacho.cuidame;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Mensaje> mensajeList;
    private String usuarioActual;

    public ChatAdapter(List<Mensaje> mensajeList, String usuarioActual) {
        this.mensajeList = mensajeList;
        this.usuarioActual = usuarioActual;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mensaje, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Mensaje mensaje = mensajeList.get(position);
        holder.bind(mensaje, usuarioActual);

        // Aplica animación solo al último mensaje
        if (position == mensajeList.size() - 1) {
            AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(300);
            holder.itemView.startAnimation(anim);
        }
    }

    @Override
    public int getItemCount() {
        return mensajeList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textoMensaje, textoHora;
        LinearLayout burbujaLayout;
        LinearLayout containerMensaje;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textoMensaje = itemView.findViewById(R.id.textoMensaje);
            textoHora = itemView.findViewById(R.id.textoHora);
            burbujaLayout = itemView.findViewById(R.id.burbujaLayout);
            containerMensaje = itemView.findViewById(R.id.containerMensaje);
        }

        public void bind(Mensaje mensaje, String usuarioActual) {
            textoMensaje.setText(mensaje.getContenido());

            // Extrae la hora (por ejemplo, HH:mm desde "2025-06-09T18:34:12Z")
            String enviadoEn = mensaje.getEnviadoEn();
            String hora = enviadoEn.length() >= 16 ? enviadoEn.substring(11, 16) : "";
            textoHora.setText(hora);

            // Alinea el mensaje dependiendo del usuario
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) burbujaLayout.getLayoutParams();

            if (mensaje.getRemitenteId().equals(usuarioActual)) {
                burbujaLayout.setBackgroundResource(R.drawable.bg_burbuja_derecha);
                params.gravity = Gravity.END;
                containerMensaje.setGravity(Gravity.END);
            } else {
                burbujaLayout.setBackgroundResource(R.drawable.bg_burbuja_izquierda);
                params.gravity = Gravity.START;
                containerMensaje.setGravity(Gravity.START);
            }

            burbujaLayout.setLayoutParams(params);
        }
    }
}
