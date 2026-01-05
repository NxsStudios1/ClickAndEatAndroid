package com.example.login.administrador.menua;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.login.R;

public class EditarMenuAdmin extends Fragment {

    private TextView tvBienvenidaTitulo;
    private TextView tvNombreAdmin;
    private TextView tvFraseDia;

    public EditarMenuAdmin() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editar_menu_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvBienvenidaTitulo = view.findViewById(R.id.tvBienvenidaTituloAdmin);
        tvNombreAdmin      = view.findViewById(R.id.tvNombreAdmin);
        tvFraseDia         = view.findViewById(R.id.tvFraseDiaAdmin);

        SharedPreferences prefs = requireContext()
                .getSharedPreferences("sesion", Context.MODE_PRIVATE);

        String nombre = prefs.getString("nombreUsuario", "Administrador");

        tvBienvenidaTitulo.setText("¡Bienvenido(a)!");
        tvNombreAdmin.setText(nombre);
        tvFraseDia.setText("Hoy es un gran día para vender ✨🍔");

    }
}
