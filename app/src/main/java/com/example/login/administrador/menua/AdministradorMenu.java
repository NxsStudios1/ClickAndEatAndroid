package com.example.login.administrador.menua;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login.R;
import com.example.login.administrador.pedidosa.PedidosAdmin;
import com.example.login.administrador.comentarioa.ComentariosAdmin;
import com.example.login.administrador.inventarioa.InventarioAdminFragment;
import com.example.login.administrador.recursosa.ThemeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdministradorMenu extends Fragment {

    public AdministradorMenu() {
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

        ThemeUtils.aplicarConfigBarra(requireActivity());

        BottomNavigationView bottomNav = view.findViewById(R.id.bottomNavigationAdmin);

        if (savedInstanceState == null) {
            loadChildFragment(new EditarMenuAdmin());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_admin_home) {
                selectedFragment = new EditarMenuAdmin();
            } else if (id == R.id.nav_admin_respuestas) {
                selectedFragment = new ComentariosAdmin();
            } else if (id == R.id.nav_admin_pedidos) {
                selectedFragment = new PedidosAdmin();
            } else if (id == R.id.nav_admin_inventario) {
                selectedFragment = new InventarioAdminFragment();
            } else if (id == R.id.nav_admin_usuario) {
                selectedFragment = new AdminUsuarioFragment();
            }

            if (selectedFragment != null) {
                loadChildFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    private void loadChildFragment(@NonNull Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.admin_fragment_container, fragment)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        ThemeUtils.aplicarConfigBarra(requireActivity());
    }
}
