package com.example.login.cliente.menuc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login.R;
import com.example.login.administrador.recursosa.ThemeUtils;
import com.example.login.cliente.comentac.ComentariosCliente;
import com.example.login.cliente.pedidoc.MenuCliente;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ClienteMenu extends Fragment {

    public ClienteMenu() {
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

        ThemeUtils.aplicarConfigBarra(requireActivity());

        BottomNavigationView bottomNav = view.findViewById(R.id.bottomNavigationCliente);

        if (savedInstanceState == null) {
            loadChildFragment(new ClienteHomeFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;

            int id = item.getItemId();
            if (id == R.id.nav_cliente_home) {
                selected = new ClienteHomeFragment();
            } else if (id == R.id.nav_cliente_comentarios) {
                selected = new ComentariosCliente();
            } else if (id == R.id.nav_cliente_menu_comida) {
                selected = new MenuCliente();
            } else if (id == R.id.nav_cliente_usuario) {
                selected = new ClienteUsuarioFragment();
            }

            if (selected != null) {
                loadChildFragment(selected);
                return true;
            }
            return false;
        });
    }

    private void loadChildFragment(@NonNull Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.cliente_fragment_container, fragment)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        ThemeUtils.aplicarConfigBarra(requireActivity());
    }
}
