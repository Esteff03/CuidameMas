package com.synunezcamacho.cuidame;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class ChatViewHolder {
    TextView textoMensaje, textoHora;
    LinearLayout burbujaLayout;
    public ChatViewHolder(@NonNull View itemView) {
        super();
        textoMensaje = itemView.findViewById(R.id.textoMensaje);
        textoHora = itemView.findViewById(R.id.textoHora);
        burbujaLayout = itemView.findViewById(R.id.burbujaLayout);
    }
}
