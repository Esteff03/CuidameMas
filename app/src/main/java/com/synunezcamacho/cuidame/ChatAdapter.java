package com.synunezcamacho.cuidame;


import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

            // Extrae la hora usando getter getEnviadoEn()
            String enviadoEn = mensaje.getEnviadoEn();
            String hora = enviadoEn.length() >= 16 ? enviadoEn.substring(11, 16) : "";
            textoHora.setText(hora);

            // Ajusta la burbuja según el remitente, usando getter getRemitenteId()
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

