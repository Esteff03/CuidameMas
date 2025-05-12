package com.synunezcamacho.cuidame;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder> {
    private List<Mensaje> mensajes;

    public MensajeAdapter(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje, parent, false);
        return new MensajeViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get(position);
        holder.textoMensaje.setText(mensaje.getContenido());
        holder.horaMensaje.setText(mensaje.getHora());
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public static class MensajeViewHolder extends RecyclerView.ViewHolder {
        TextView textoMensaje, horaMensaje;

        public MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            textoMensaje = itemView.findViewById(R.id.textoMensaje);
            horaMensaje = itemView.findViewById(R.id.horaMensaje);
        }
    }
}