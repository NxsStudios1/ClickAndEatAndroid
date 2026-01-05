package com.example.login.sesion;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

    private static final String[] PREFIJOS_VALIDOS = {
            "55", "56",
            "427", "588","720",
            "591", "592", "593", "594", "595", "596", "597", "599",
            "711", "712", "713", "714", "716", "717", "718", "719",
            "721", "722", "723", "724", "725", "726", "728", "729",
            "743", "751", "761", "767"
    };

    private static final String PREFIJO_MEXICO = "52";
    private boolean editandoTelefono = false;

    public RegistroFragment() {}

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

        configurarAutoPrefijoTelefono();
    }

    private void configurarAutoPrefijoTelefono() {
        editTelefono.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (editandoTelefono) return;
                editandoTelefono = true;

                if (!s.toString().startsWith(PREFIJO_MEXICO)) {
                    editTelefono.setText(PREFIJO_MEXICO);
                    editTelefono.setSelection(editTelefono.getText().length());
                }

                editandoTelefono = false;
            }
        });

        editTelefono.setText(PREFIJO_MEXICO);
        editTelefono.setSelection(editTelefono.getText().length());
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

            if (!esTelefonoValidoMexico(telefonoTxt)) {
                Toast.makeText(requireContext(),
                        "Número inválido. Usa un celular real de México",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (!esContrasenaValida(contraTxt)) {
                Toast.makeText(requireContext(),
                        "La contraseña debe tener mínimo 10 caracteres y al menos un número",
                        Toast.LENGTH_LONG).show();
                return;
            }

            registrarUsuario(nombreTxt, telefonoTxt, contraTxt);

        } else if (v.getId() == R.id.btnRegresar) {
            navController.navigate(R.id.loginfragment);
        }
    }


    private boolean esTelefonoValidoMexico(String telefono) {

        if (!telefono.matches("^52\\d{10}$")) {
            return false;
        }

        String numero = telefono.substring(2);

        for (String prefijo : PREFIJOS_VALIDOS) {
            if (numero.startsWith(prefijo)) {
                return true;
            }
        }
        return false;
    }

    private boolean esContrasenaValida(String password) {
        return password.length() >= 10 && password.matches(".*\\d.*");
    }

    private void registrarUsuario(String nombreTxt,
                                  String telefonoTxt,
                                  String contraTxt) {

        UsuarioDto nuevoUsuario = new UsuarioDto(
                0,
                nombreTxt,
                telefonoTxt,
                contraTxt,
                2 // CLIENTE
        );

        apiService.crearUsuario(nuevoUsuario)
                .enqueue(new Callback<UsuarioDto>() {
                    @Override
                    public void onResponse(Call<UsuarioDto> call,
                                           Response<UsuarioDto> response) {

                        if (!isAdded()) return;

                        if (response.isSuccessful() && response.body() != null) {

                            navController.navigate(
                                    R.id.action_registroFragment_to_registroExitosoFragment
                            );

                        } else if (response.code() == 409) {

                            Toast.makeText(requireContext(),
                                    "Este número ya está registrado",
                                    Toast.LENGTH_LONG).show();

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
