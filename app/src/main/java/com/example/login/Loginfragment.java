package com.example.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.login.network.ApiClient;
import com.example.login.network.SpringApiService;
import com.example.login.network.model.UsuarioDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Loginfragment extends Fragment {

    private EditText etUser, etPass;
    private Button btnLogin, btnRegister;
    private SpringApiService apiService;

    public Loginfragment() {
        // Constructor vacío
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loginfragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etUser = view.findViewById(R.id.etname);
        etPass = view.findViewById(R.id.etpass);
        btnLogin = view.findViewById(R.id.button);
        btnRegister = view.findViewById(R.id.btnRegister);

        apiService = ApiClient.getInstance().create(SpringApiService.class);
        NavController navController = Navigation.findNavController(view);

        btnRegister.setOnClickListener(v ->
                navController.navigate(R.id.action_loginfragment_to_registroFragment)
        );

        btnLogin.setOnClickListener(v -> {
            String usuario = etUser.getText().toString().trim();
            String contrasena = etPass.getText().toString().trim();

            if (TextUtils.isEmpty(usuario) || TextUtils.isEmpty(contrasena)) {
                Toast.makeText(requireContext(),
                        "Usuario y contraseña son obligatorios",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            realizarLogin(usuario, contrasena, navController);
        });
    }

    private void realizarLogin(String telefono, String contrasena, NavController navController) {
        Call<List<UsuarioDto>> call = apiService.getUsuarios();
        call.enqueue(new Callback<List<UsuarioDto>>() {
            @Override
            public void onResponse(Call<List<UsuarioDto>> call,
                                   Response<List<UsuarioDto>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<UsuarioDto> usuarios = response.body();

                    UsuarioDto encontrado = null;
                    for (UsuarioDto u : usuarios) {
                        if (telefono.equals(u.getTelefono()) &&
                                contrasena.equals(u.getContrasena())) {
                            encontrado = u;
                            break;
                        }
                    }

                    if (encontrado == null) {
                        Toast.makeText(requireContext(),
                                "Credenciales incorrectas",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int rol = encontrado.getRol();

                    // En lugar de ir directo al menú, vamos al fragment de éxito
                    Bundle args = new Bundle();
                    args.putInt("rol", rol);
                    navController.navigate(R.id.action_loginfragment_to_loginSuccessFragment, args);

                } else {
                    Toast.makeText(requireContext(),
                            "Error al obtener usuarios (código " + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UsuarioDto>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(),
                        "Error de conexión: " + t.getLocalizedMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
