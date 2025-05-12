package com.synunezcamacho.cuidame;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class Contacto extends AppCompatActivity {
    private RecyclerView recyclerMensaje;
    private ContactoAdapter adapter;
    private List<ContactoPreview> listaContactos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contacto);

        recyclerMensaje = findViewById(R.id.recyclerViewChats);
        recyclerMensaje.setLayoutManager(new LinearLayoutManager(this));

        listaContactos = new ArrayList<>();

        // Simulación de contactos
        listaContactos.add(new ContactoPreview("Joe", "Gracias :)", "12:45", R.drawable.camara));
        listaContactos.add(new ContactoPreview("Lucía", "Nos vemos mañana", "10:30", R.drawable.camara));
        listaContactos.add(new ContactoPreview("Carlos", "¡Hola!", "09:15", R.drawable.camara));

        adapter = new ContactoAdapter(this, listaContactos);
        recyclerMensaje.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}