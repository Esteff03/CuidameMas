package com.synunezcamacho.cuidame;


import android.content.Context;
import android.content.SharedPreferences;

public class UUIDManager {
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_UUID = "uuid";

    // Guardar UUID
    public static void guardarUUID(Context context, String uuid) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_UUID, uuid);
        editor.apply(); // O commit() si quieres que sea inmediato
    }

    // Obtener UUID
    public static String getUUID(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_UUID, null); // Devuelve null si no existe
    }
}
