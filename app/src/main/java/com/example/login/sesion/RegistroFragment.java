package com.example.login.sesion;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.login.R;
import com.example.login.network.ApiClient;
import com.example.login.network.SpringApiService;
import com.example.login.network.model.UsuarioDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroFragment extends Fragment implements View.OnClickListener {

    private Button btnCrear, btnRegresar;
    private EditText editNombre, editTelefono, editContrasena;
    private SpringApiService apiService;
    private NavController navController;

    public RegistroFragment() {
        // Constructor vacío
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConstraintLayout root = view.findViewById(R.id.main);
        if (root != null && root.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) root.getBackground();
            animationDrawable.setEnterFadeDuration(2500);
            animationDrawable.setExitFadeDuration(5000);
            animationDrawable.start();
        }

        ScrollView scrollView = view.findViewById(R.id.registro);

        btnCrear = view.findViewById(R.id.btnRegistrar);
        btnRegresar = view.findViewById(R.id.btnRegresar);

        editNombre = view.findViewById(R.id.editNombre);
        editTelefono = view.findViewById(R.id.editTelefono);
        editContrasena = view.findViewById(R.id.editContraseña);

        btnCrear.setOnClickListener(this);
        btnRegresar.setOnClickListener(this);

        apiService = ApiClient.getInstance().create(SpringApiService.class);
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnRegistrar) {
            String nombreTxt = editNombre.getText().toString().trim();
            String telefonoTxt = editTelefono.getText().toString().trim();
            String contraTxt = editContrasena.getText().toString().trim();

            if (TextUtils.isEmpty(nombreTxt) ||
                    TextUtils.isEmpty(telefonoTxt) ||
                    TextUtils.isEmpty(contraTxt)) {
                Toast.makeText(requireContext(),
                        "Todos los campos son obligatorios",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (contraTxt.length() < 6) {
                Toast.makeText(requireContext(),
                        "La contraseña debe tener mínimo 6 caracteres",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            registrarUsuario(nombreTxt, telefonoTxt, contraTxt);
        } else if (v.getId() == R.id.btnRegresar) {
            navController.navigate(R.id.loginfragment);
        }
    }

    private void registrarUsuario(String nombreTxt, String telefonoTxt, String contraTxt) {
        // rol=2 -> CLIENTE
        UsuarioDto nuevoUsuario = new UsuarioDto(
                0,
                nombreTxt,
                telefonoTxt,
                contraTxt,
                2
        );

        Call<UsuarioDto> call = apiService.crearUsuario(nuevoUsuario);
        call.enqueue(new Callback<UsuarioDto>() {
            @Override
            public void onResponse(Call<UsuarioDto> call, Response<UsuarioDto> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    // En vez de ir directo al login, vamos al fragment con la animación
                    navController.navigate(R.id.action_registroFragment_to_registroExitosoFragment);
                } else {
                    Toast.makeText(requireContext(),
                            "Error al crear la cuenta",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioDto> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(),
                        "Error de conexión: " + t.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
