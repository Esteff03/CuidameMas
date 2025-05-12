package com.synunezcamacho.cuidame;


import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Chat extends AppCompatActivity {

    private EditText editTextMensaje;
    private TextView txtnombreContacto;
    private ImageView btnEnviar, btnAtras, usuarioImagen;
    private RecyclerView recyclerMensajes;
    private List<Mensaje> listaMensajes;
    private MensajeAdapter mensajeAdapter;
    private String nombreContacto;
    private int imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        editTextMensaje = findViewById(R.id.editTextMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnAtras = findViewById(R.id.imgAtras);

        recyclerMensajes = findViewById(R.id.recyclerMensajes);

        nombreContacto = getIntent().getStringExtra("nombreUsuario");
        imagen = getIntent().getIntExtra("imagenPerfil", R.drawable.camara);

        listaMensajes = new ArrayList<>();
        mensajeAdapter = new MensajeAdapter(listaMensajes);
        recyclerMensajes.setLayoutManager(new LinearLayoutManager(this));
        recyclerMensajes.setAdapter(mensajeAdapter);

        // Obtener la referencia al TextView
        txtnombreContacto = findViewById(R.id.usuarioNombre);
        usuarioImagen = findViewById(R.id.usuarioImagen);

        if (nombreContacto != null) {
            txtnombreContacto.setText(nombreContacto);
            usuarioImagen.setImageResource(imagen);
        }
        //boton para atras
                btnAtras.setOnClickListener(v -> {
                    finish();
                });
        // Evento botón enviar
                btnEnviar.setOnClickListener(view -> {
                    String texto = editTextMensaje.getText().toString().trim();
                    if (!texto.isEmpty()) {
                        String hora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                        Mensaje nuevo = new Mensaje("Yo", nombreContacto, texto, hora);
                        listaMensajes.add(nuevo);
                        mensajeAdapter.notifyItemInserted(listaMensajes.size() - 1);
                        recyclerMensajes.scrollToPosition(listaMensajes.size() - 1);
                        editTextMensaje.setText("");
                    }
                });
            }
}