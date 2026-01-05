package com.example.login.cliente.menuc;

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

public class ClienteHomeFragment extends Fragment {

    private TextView tvSaludoCliente;
    private TextView tvNombreCliente;
    private TextView tvFraseCliente;
    private TextView tvHintCliente;

    public ClienteHomeFragment() {
        // Constructor vacío requerido
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cliente_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvSaludoCliente = view.findViewById(R.id.tvSaludoCliente);
        tvNombreCliente = view.findViewById(R.id.tvNombreCliente);
        tvFraseCliente  = view.findViewById(R.id.tvFraseCliente);
        tvHintCliente   = view.findViewById(R.id.tvHintCliente);

        SharedPreferences prefs = requireContext()
                .getSharedPreferences("sesion", Context.MODE_PRIVATE);

        String nombre = prefs.getString("nombreUsuario", "Cliente");

        tvSaludoCliente.setText("¡Bienvenido(a)!");
        tvNombreCliente.setText(nombre);
        tvFraseCliente.setText("Hoy es un gran día para disfrutar una Andy Burger 😋🍔");
        tvHintCliente.setText("Explora el menú, arma tu pedido y cuéntanos qué te pareció.");
    }
}
