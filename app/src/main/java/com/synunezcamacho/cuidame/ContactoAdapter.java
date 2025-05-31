package com.synunezcamacho.cuidame;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.synunezcamacho.cuidame.Constants;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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
            Log.d("ContactoAdapter", "Click en contacto: " + contacto.getNombreUsuario() + " (ID: " + contacto.getIdContacto() + ")");

            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra(Constants.EXTRA_ID_CONTACTO, contacto.getIdContacto());
            intent.putExtra(Constants.EXTRA_NOMBRE_CONTACTO, contacto.getNombreUsuario());


            // Solo usa FLAG_ACTIVITY_NEW_TASK si el contexto NO es una Activity
            if (!(context instanceof android.app.Activity)) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

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
