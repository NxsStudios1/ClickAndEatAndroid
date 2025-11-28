// NuevoComentarioCliente.java
package com.example.login.cliente;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.login.R;
import com.example.login.network.ApiClient;
import com.example.login.network.SpringApiService;
import com.example.login.network.model.ComentarioDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NuevoComentarioCliente extends Fragment {

    private EditText etAsunto, etContenido;
    private Spinner spCategoria;
    private RatingBar ratingBar;
    private Button btnEnviar, btnCancelar;

    private SpringApiService apiService;

    // se llena desde SharedPreferences
    private int idClienteLogueado = 0;

    private final String[] categorias =
            {"COMIDA", "SERVICIO", "AMBIENTE", "TIEMPO_ESPERA", "GENERAL"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nuevo_comentario_cliente, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etAsunto = view.findViewById(R.id.etAsunto);
        etContenido = view.findViewById(R.id.etContenido);
        spCategoria = view.findViewById(R.id.spCategoria);
        ratingBar = view.findViewById(R.id.ratingBar);
        btnEnviar = view.findViewById(R.id.btnEnviarComentario);
        btnCancelar = view.findViewById(R.id.btnCancelarComentario);

        apiService = ApiClient.getInstance().create(SpringApiService.class);

        // 🔹 Cargar id del cliente logueado
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("sesion", Context.MODE_PRIVATE);
        idClienteLogueado = prefs.getInt("idUsuario", 0);

        // Spinner de categorías
        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                categorias
        );
        adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapterCat);

        btnEnviar.setOnClickListener(v -> enviarComentario());
        btnCancelar.setOnClickListener(v ->
                NavHostFragment.findNavController(NuevoComentarioCliente.this)
                        .popBackStack()
        );
    }

    private void enviarComentario() {
        if (idClienteLogueado == 0) {
            Toast.makeText(requireContext(),
                    "No se encontró el cliente en sesión",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String asunto = etAsunto.getText().toString().trim();
        String contenido = etContenido.getText().toString().trim();
        int calificacion = (int) ratingBar.getRating();
        int posCat = spCategoria.getSelectedItemPosition();
        String categoriaSeleccionada = categorias[posCat];

        if (TextUtils.isEmpty(asunto) || TextUtils.isEmpty(contenido)) {
            Toast.makeText(requireContext(),
                    "Asunto y comentario son obligatorios",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ComentarioDto nuevo = new ComentarioDto();
        nuevo.setAsunto(asunto);
        nuevo.setContenido(contenido);
        nuevo.setCalificacion(calificacion);
        nuevo.setCategoria(categoriaSeleccionada); // String
        nuevo.setIdCliente(idClienteLogueado);      // 🔹 AQUÍ VA EL CLIENTE CORRECTO

        btnEnviar.setEnabled(false);

        Call<ComentarioDto> call = apiService.crearComentario(nuevo);
        call.enqueue(new Callback<ComentarioDto>() {
            @Override
            public void onResponse(@NonNull Call<ComentarioDto> call,
                                   @NonNull Response<ComentarioDto> response) {
                btnEnviar.setEnabled(true);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(),
                            "Error al enviar comentario",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(requireContext(),
                        "Comentario enviado",
                        Toast.LENGTH_SHORT).show();

                // regresar a la lista de comentarios
                NavHostFragment.findNavController(NuevoComentarioCliente.this)
                        .popBackStack();
            }

            @Override
            public void onFailure(@NonNull Call<ComentarioDto> call,
                                  @NonNull Throwable t) {
                btnEnviar.setEnabled(true);
                Toast.makeText(requireContext(),
                        "Error de conexión al enviar comentario",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
