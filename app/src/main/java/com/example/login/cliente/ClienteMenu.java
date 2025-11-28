package com.example.login.cliente;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login.R;
import com.example.login.cliente.ClienteHomeFragment;
import com.example.login.cliente.ClienteUsuarioFragment;
import com.example.login.cliente.ComentariosCliente;
import com.example.login.cliente.MenuCliente;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ClienteMenu extends Fragment {

    public ClienteMenu() {
        // Constructor vacío obligatorio
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cliente_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNav = view.findViewById(R.id.bottomNavigationCliente);

        // Pantalla por defecto: Home del cliente
        if (savedInstanceState == null) {
            loadFragment(new ClienteHomeFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;

            int id = item.getItemId();
            if (id == R.id.nav_cliente_home) {
                // Home
                selected = new ClienteHomeFragment();
            } else if (id == R.id.nav_cliente_comentarios) {
                // Comentarios del cliente
                selected = new ComentariosCliente();
            } else if (id == R.id.nav_cliente_menu_comida) {
                // Menú de comida (ya tienes fragment_menu_cliente)
                selected = new MenuCliente();
            } else if (id == R.id.nav_cliente_usuario) {
                // Datos del usuario cliente
                selected = new ClienteUsuarioFragment();
            }

            if (selected != null) {
                loadFragment(selected);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(@NonNull Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.cliente_fragment_container, fragment)
                .commit();
    }
}
