package com.example.login.administrador.menua;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login.MainActivity;
import com.example.login.R;
import com.example.login.administrador.recursosa.ThemeUtils;

public class AdminUsuarioFragment extends Fragment {

    private TextView tvNombre, tvTelefono, tvRol, tvMensaje;
    private Spinner spColorMenu, spLogo;
    private View viewPreviewColor;
    private ImageView ivPreviewLogo;
    private Button btnGuardarConfig, btnCerrarSesion;

    private final String[] colores = {
            "Primavera - Verde", "Primavera - Rosa", "Primavera - Amarillo",
            "Verano - Naranja", "Verano - Azul", "Verano - Verde",
            "Otoño - Rojo", "Otoño - Naranja", "Otoño - Café",
            "Invierno - Azul", "Invierno - Gris", "Invierno - Blanco",
            "Halloween - Naranja", "Halloween - Gris", "Halloween - Morado",
            "Navidad - Rojo", "Navidad - Verde", "Navidad - Dorado"
    };

    private final String[] logos = {
            "Clásico", "Halloween", "Navidad", "San Valentín", "Vacaciones"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_usuario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvNombre = view.findViewById(R.id.tvNombreAdminUsuario);
        tvTelefono = view.findViewById(R.id.tvTelefonoAdminUsuario);
        tvRol = view.findViewById(R.id.tvRolAdminUsuario);
        tvMensaje = view.findViewById(R.id.tvMensajeAdminUsuario);

        spColorMenu = view.findViewById(R.id.spColorMenuAdmin);
        spLogo = view.findViewById(R.id.spLogoAdmin);
        viewPreviewColor = view.findViewById(R.id.viewPreviewColorMenuAdmin);
        ivPreviewLogo = view.findViewById(R.id.ivPreviewLogoAdmin);

        btnGuardarConfig = view.findViewById(R.id.btnGuardarConfigVisualAdmin);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesionAdmin);

        cargarDatosSesion();
        configurarSpinners();

        btnGuardarConfig.setOnClickListener(v -> guardarConfiguracion());
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    private void cargarDatosSesion() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("sesion", Context.MODE_PRIVATE);

        String nombre = prefs.getString("nombreUsuario", "Administrador");
        String telefono = prefs.getString("telefonoUsuario", "Sin teléfono");
        int rol = prefs.getInt("rolUsuario", 1);

        tvNombre.setText(nombre);
        tvTelefono.setText("Teléfono: " + telefono);
        tvRol.setText("Rol: " + (rol == 1 ? "Administrador" : "Usuario"));
        tvMensaje.setText("¡Hoy es un gran día para vender, " + nombre + "!");
    }

    private void configurarSpinners() {
        spColorMenu.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                colores
        ));
        spLogo.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                logos
        ));

        spColorMenu.setOnItemSelectedListener(
                new SimpleItemSelectedListener(this::actualizarPreviewColor));
        spLogo.setOnItemSelectedListener(
                new SimpleItemSelectedListener(this::actualizarPreviewLogo));
    }

    private void actualizarPreviewColor() {
        viewPreviewColor.setBackgroundColor(
                ThemeUtils.getColorInt(
                        requireContext(),
                        mapColor(colores[spColorMenu.getSelectedItemPosition()])
                )
        );
    }

    private void actualizarPreviewLogo() {
        ivPreviewLogo.setImageResource(
                ThemeUtils.getLogoResId(
                        mapLogo(logos[spLogo.getSelectedItemPosition()])
                )
        );
    }

    private void guardarConfiguracion() {
        ThemeUtils.guardarConfigVisual(
                requireContext(),
                mapColor(colores[spColorMenu.getSelectedItemPosition()]),
                mapLogo(logos[spLogo.getSelectedItemPosition()])
        );
        ThemeUtils.aplicarConfigBarra(requireActivity());

        Toast.makeText(requireContext(),
                "Configuración guardada", Toast.LENGTH_SHORT).show();
    }

    private String mapColor(String label) {
        switch (label) {
            case "Primavera - Verde": return ThemeUtils.SPRING_GREEN;
            case "Primavera - Rosa": return ThemeUtils.SPRING_PINK;
            case "Primavera - Amarillo": return ThemeUtils.SPRING_YELLOW;
            case "Verano - Naranja": return ThemeUtils.SUMMER_ORANGE;
            case "Verano - Azul": return ThemeUtils.SUMMER_BLUE;
            case "Verano - Verde": return ThemeUtils.SUMMER_GREEN;
            case "Otoño - Rojo": return ThemeUtils.AUTUMN_RED;
            case "Otoño - Naranja": return ThemeUtils.AUTUMN_ORANGE;
            case "Otoño - Café": return ThemeUtils.AUTUMN_BROWN;
            case "Invierno - Azul": return ThemeUtils.WINTER_BLUE;
            case "Invierno - Gris": return ThemeUtils.WINTER_GRAY;
            case "Invierno - Blanco": return ThemeUtils.WINTER_WHITE;
            case "Halloween - Naranja": return ThemeUtils.HALLOWEEN_ORANGE;
            case "Halloween - Gris": return ThemeUtils.HALLOWEEN_GRAY;
            case "Halloween - Morado": return ThemeUtils.HALLOWEEN_PURPLE;
            case "Navidad - Rojo": return ThemeUtils.CHRISTMAS_RED;
            case "Navidad - Verde": return ThemeUtils.CHRISTMAS_GREEN;
            case "Navidad - Dorado": return ThemeUtils.CHRISTMAS_GOLD;
            default: return ThemeUtils.COLOR_NARANJA;
        }
    }

    private String mapLogo(String label) {
        switch (label) {
            case "Halloween": return ThemeUtils.LOGO_HALLOWEEN;
            case "Navidad": return ThemeUtils.LOGO_NAVIDAD;
            case "San Valentín": return ThemeUtils.LOGO_SAN_VALENTIN;
            case "Vacaciones": return ThemeUtils.LOGO_VACACIONES;
            default: return ThemeUtils.LOGO_CLASICO;
        }
    }

    private void cerrarSesion() {
        requireContext()
                .getSharedPreferences("sesion", Context.MODE_PRIVATE)
                .edit().clear().apply();

        startActivity(new Intent(requireContext(), MainActivity.class));
        requireActivity().finish();
    }

    private static class SimpleItemSelectedListener
            implements android.widget.AdapterView.OnItemSelectedListener {

        private final Runnable action;
        private boolean first = true;

        SimpleItemSelectedListener(Runnable action) {
            this.action = action;
        }

        @Override
        public void onItemSelected(android.widget.AdapterView<?> parent,
                                   View view, int position, long id) {
            if (first) { first = false; return; }
            action.run();
        }

        @Override
        public void onNothingSelected(android.widget.AdapterView<?> parent) {}
    }
}
