package com.synunezcamacho.cuidame;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//es el puente entre los datos (ContactoPreview) y lo que se ve (item_contacto.xml).
public class ContactoAdapter extends RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder> {
    private List<ContactoPreview> listaContactos;
    private Context context;

    public ContactoAdapter(Context context, List<ContactoPreview> listaContactos) {
        this.context = context;
        this.listaContactos = listaContactos;
    }

    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacto, parent, false);
        return new ContactoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactoViewHolder holder, int position) {
        ContactoPreview contacto = listaContactos.get(position);

        holder.nombreUsuario.setText(contacto.getNombreUsuario());
        holder.ultimoMensaje.setText(contacto.getUltimoMensaje());
        holder.horaMensaje.setText(contacto.getHora());
        holder.imagenUsuario.setImageResource(contacto.getImagenPerfil());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Chat.class);
            intent.putExtra("nombreUsuario", contacto.getNombreUsuario());
            intent.putExtra("imagenPerfil", contacto.getImagenPerfil());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaContactos.size();
    }

    public static class ContactoViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenUsuario;
        TextView nombreUsuario, ultimoMensaje, horaMensaje;

        public ContactoViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenUsuario = itemView.findViewById(R.id.imageUsuario);
            nombreUsuario = itemView.findViewById(R.id.nombreUsuario);
            ultimoMensaje = itemView.findViewById(R.id.ultimoMensaje);
            horaMensaje = itemView.findViewById(R.id.horaMensaje);
        }
    }
}
