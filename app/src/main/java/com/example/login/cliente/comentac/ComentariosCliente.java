package com.example.login.cliente.comentac;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;
import com.example.login.network.ApiClient;
import com.example.login.network.SpringApiService;
import com.example.login.network.model.ComentarioConRespuestaUi;
import com.example.login.network.model.ComentarioDto;
import com.example.login.network.model.RespuestaComentarioDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComentariosCliente extends Fragment {

    private RecyclerView recyclerView;
    private ComentariosClienteAdapter adapter;
    private final List<ComentarioConRespuestaUi> lista = new ArrayList<>();

    private SpringApiService apiService;
    private FloatingActionButton fabAgregar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_comentarios_cliente,
                container,
                false
        );

        recyclerView = view.findViewById(R.id.recyclerComentariosCliente);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ComentariosClienteAdapter(lista, null);
        recyclerView.setAdapter(adapter);

        fabAgregar = view.findViewById(R.id.fabAgregarComentario);
        apiService = ApiClient.getInstance().create(SpringApiService.class);

        fabAgregar.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.nuevoComentarioCliente)
        );

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
                                        response2.body() != null
                                                ? response2.body()
                                                : new ArrayList<>();

                                llenarListaUi(comentarios, respuestas);
                            }

                            @Override
                            public void onFailure(
                                    @NonNull Call<List<RespuestaComentarioDto>> call2,
                                    @NonNull Throwable t) {

                                llenarListaUi(comentarios, new ArrayList<>());
                            }
                        });
            }

            @Override
            public void onFailure(@NonNull Call<List<ComentarioDto>> call,
                                  @NonNull Throwable t) {

                Toast.makeText(getContext(),
                        "Error de conexión",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void llenarListaUi(List<ComentarioDto> comentarios,
                               List<RespuestaComentarioDto> respuestas) {

        lista.clear();

        for (ComentarioDto c : comentarios) {
            ComentarioConRespuestaUi ui = new ComentarioConRespuestaUi();

            ui.idComentario = c.getId();
            ui.nombreCliente = c.getNombreCliente();
            ui.fechaComentario = c.getFechaComentario();
            ui.categoria = c.getCategoria();
            ui.calificacion = c.getCalificacion();
            ui.asunto = c.getAsunto();
            ui.contenido = c.getContenido();

            for (RespuestaComentarioDto r : respuestas) {
                if (r.getIdComentario() == c.getId()) {
                    ui.contenidoRespuesta = r.getContenido();
                    ui.fechaRespuesta = r.getFechaRespuesta();
                    ui.nombreAdmin = r.getNombreAdministrador();
                    break;
                }
            }

            lista.add(ui);
        }

        lista.sort((a, b) ->
                Integer.compare(b.idComentario, a.idComentario)
        );

        adapter.notifyDataSetChanged();
    }
}
