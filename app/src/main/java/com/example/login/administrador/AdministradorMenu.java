package com.example.login.administrador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdministradorMenu extends Fragment {

    public AdministradorMenu() {
        // Constructor vacío obligatorio
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_administrador_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNav = view.findViewById(R.id.bottomNavigationAdmin);

        // Cargar pestaña HOME por defecto (usaré EditarMenuAdmin como home)
        if (savedInstanceState == null) {
            loadFragment(new EditarMenuAdmin());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_admin_home) {
                selectedFragment = new EditarMenuAdmin();
            } else if (id == R.id.nav_admin_respuestas) {
                selectedFragment = new ComentariosAdmin();
            } else if (id == R.id.nav_admin_pedidos) {
                // Pedidos admin
                selectedFragment = new PedidosAdmin();
            } else if (id == R.id.nav_admin_inventario) {
                // Inventario admin
                selectedFragment = new InventarioAdminFragment();
            } else if (id == R.id.nav_admin_usuario) {
                selectedFragment = new AdminUsuarioFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(@NonNull Fragment fragment) {
        // Como estamos dentro de un Fragment, usamos childFragmentManager
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.admin_fragment_container, fragment)
                .commit();
    }
}
