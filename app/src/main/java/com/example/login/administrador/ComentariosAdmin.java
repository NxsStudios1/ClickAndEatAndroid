// com/example/login/administrador/ComentariosAdmin.java
package com.example.login.administrador;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;
import com.example.login.cliente.ComentariosClienteAdapter;
import com.example.login.network.ApiClient;
import com.example.login.network.SpringApiService;
import com.example.login.network.model.ComentarioConRespuestaUi;
import com.example.login.network.model.ComentarioDto;
import com.example.login.network.model.RespuestaComentarioDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComentariosAdmin extends Fragment {

    private RecyclerView recyclerView;
    private ComentariosClienteAdapter adapter;
    private final List<ComentarioConRespuestaUi> lista = new ArrayList<>();

    private SpringApiService apiService;

    private int idAdmin;
    private String nombreAdmin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_comentarios_admin,
                container, false);

        recyclerView = view.findViewById(R.id.recyclerComentariosAdmin);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ComentariosClienteAdapter(
                lista,
                this::mostrarDialogOpcionesComentario
        );
        recyclerView.setAdapter(adapter);

        apiService = ApiClient.getInstance().create(SpringApiService.class);

        SharedPreferences prefs = requireContext()
                .getSharedPreferences("sesion", MODE_PRIVATE);
        idAdmin = prefs.getInt("idUsuario", 0);
        nombreAdmin = prefs.getString("nombreUsuario", "");

        if (idAdmin == 0) {
            Toast.makeText(requireContext(),
                    "No se encontró el ID del administrador en sesión",
                    Toast.LENGTH_LONG).show();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarComentariosYRespuestas();
    }

    private void cargarComentariosYRespuestas() {
        apiService.getComentarios().enqueue(new Callback<List<ComentarioDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<ComentarioDto>> call,
                                   @NonNull Response<List<ComentarioDto>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(),
                            "Error al obtener comentarios",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                List<ComentarioDto> comentarios = response.body();

                apiService.getRespuestasComentario()
                        .enqueue(new Callback<List<RespuestaComentarioDto>>() {
                            @Override
                            public void onResponse(
                                    @NonNull Call<List<RespuestaComentarioDto>> call2,
                                    @NonNull Response<List<RespuestaComentarioDto>> response2) {

                                List<RespuestaComentarioDto> respuestas =
                                        (response2.isSuccessful() && response2.body() != null)
                                                ? response2.body()
                                                : new ArrayList<>();

                                llenarListaUi(comentarios, respuestas);
                            }

                            @Override
                            public void onFailure(
                                    @NonNull Call<List<RespuestaComentarioDto>> call2,
                                    @NonNull Throwable t) {
                                Toast.makeText(getContext(),
                                        "Error de conexión (respuestas)",
                                        Toast.LENGTH_SHORT).show();
                                llenarListaUi(comentarios, new ArrayList<>());
                            }
                        });
            }

            @Override
            public void onFailure(@NonNull Call<List<ComentarioDto>> call,
                                  @NonNull Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(),
                            "Error de conexión (comentarios)",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void llenarListaUi(List<ComentarioDto> comentarios,
                               List<RespuestaComentarioDto> respuestas) {

        lista.clear();

        for (ComentarioDto c : comentarios) {

            ComentarioConRespuestaUi ui = new ComentarioConRespuestaUi();
            ui.idComentario    = c.getId();
            ui.nombreCliente   = c.getNombreCliente();
            ui.fechaComentario = c.getFechaComentario();
            ui.categoria       = c.getCategoria();
            ui.calificacion    = c.getCalificacion();
            ui.asunto          = c.getAsunto();
            ui.contenido       = c.getContenido();

            // Buscar si ese comentario tiene respuesta
            for (RespuestaComentarioDto r : respuestas) {
                if (r.getIdComentario() == c.getId()) {
                    ui.contenidoRespuesta = r.getContenido();
                    ui.fechaRespuesta     = r.getFechaRespuesta();
                    ui.nombreAdmin        = r.getNombreAdministrador();
                    break;
                }
            }

            lista.add(ui);
        }

        Collections.sort(
                lista,
                Comparator.comparing((ComentarioConRespuestaUi u) -> u.fechaComentario).reversed()
        );

        adapter.notifyDataSetChanged();
    }


    // ================== Diálogos ==================

    private void mostrarDialogOpcionesComentario(ComentarioConRespuestaUi comentario) {
        String[] opciones = {"Responder", "Eliminar"};

        new AlertDialog.Builder(requireContext())
                .setTitle("Acciones")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        mostrarDialogResponder(comentario);
                    } else if (which == 1) {
                        confirmarEliminar(comentario);
                    }
                })
                .show();
    }

    // >>> DIÁLOGO BONITO AQUÍ <<<
    private void mostrarDialogResponder(ComentarioConRespuestaUi comentario) {
        if (idAdmin == 0) {
            Toast.makeText(requireContext(),
                    "No se encontró el ID del administrador en sesión",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_responder_comentario, null);
        builder.setView(dialogView);

        TextView tvTitulo = dialogView.findViewById(R.id.tvTituloDialog);
        TextView tvNombreCliente = dialogView.findViewById(R.id.tvNombreClienteDialog);
        TextView tvComentarioOriginal = dialogView.findViewById(R.id.tvComentarioOriginalDialog);
        EditText etRespuesta = dialogView.findViewById(R.id.etRespuestaDialog);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelarDialog);
        Button btnEnviar = dialogView.findViewById(R.id.btnEnviarDialog);

        tvTitulo.setText("Responder comentario");
        tvNombreCliente.setText("Cliente: " + comentario.nombreCliente);
        tvComentarioOriginal.setText(comentario.contenido);

        AlertDialog dialog = builder.create();

        // Para respetar el fondo redondeado
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnEnviar.setOnClickListener(v -> {
            String texto = etRespuesta.getText().toString().trim();
            if (texto.isEmpty()) {
                etRespuesta.setError("La respuesta no puede estar vacía");
                return;
            }

            enviarRespuesta(comentario.idComentario, texto);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void confirmarEliminar(ComentarioConRespuestaUi comentario) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar comentario")
                .setMessage("¿Seguro que quieres eliminar este comentario?")
                .setPositiveButton("Eliminar", (dialog, which) ->
                        eliminarComentario(comentario.idComentario))
                .setNegativeButton("Cancelar",
                        (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void enviarRespuesta(int idComentario, String texto) {
        if (idAdmin == 0) {
            Toast.makeText(requireContext(),
                    "No se encontró el ID del administrador en sesión",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        RespuestaComentarioDto dto = new RespuestaComentarioDto();
        dto.setContenido(texto);
        dto.setIdComentario(idComentario);
        dto.setIdAdministrador(idAdmin);

        apiService.crearRespuestaComentario(dto)
                .enqueue(new Callback<RespuestaComentarioDto>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<RespuestaComentarioDto> call,
                            @NonNull Response<RespuestaComentarioDto> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(requireContext(),
                                    "Error al enviar respuesta",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Toast.makeText(requireContext(),
                                "Respuesta enviada",
                                Toast.LENGTH_SHORT).show();
                        cargarComentariosYRespuestas();
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<RespuestaComentarioDto> call,
                            @NonNull Throwable t) {
                        Toast.makeText(requireContext(),
                                "Error de conexión al responder",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void eliminarComentario(int idComentario) {
        apiService.eliminarComentario(idComentario)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<Void> call,
                            @NonNull Response<Void> response) {

                        if (!response.isSuccessful()) {
                            Toast.makeText(requireContext(),
                                    "No se pudo eliminar",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Toast.makeText(requireContext(),
                                "Comentario eliminado",
                                Toast.LENGTH_SHORT).show();
                        cargarComentariosYRespuestas();
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Void> call,
                            @NonNull Throwable t) {
                        Toast.makeText(requireContext(),
                                "Error de conexión al eliminar",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
