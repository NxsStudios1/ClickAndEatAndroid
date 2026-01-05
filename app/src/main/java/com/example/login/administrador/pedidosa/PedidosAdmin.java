package com.example.login.administrador.pedidosa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;
import com.example.login.network.ApiClient;
import com.example.login.network.SpringApiService;
import com.example.login.network.model.DetallePedidoDto;
import com.example.login.network.model.PedidoDto;
import com.example.login.network.model.ProductoDto;
import com.example.login.network.model.PromocionDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidosAdmin extends Fragment
        implements PedidoAdminAdapter.OnEstadoChangeListener {

    private RecyclerView rvPedidos;
    private Spinner spFiltroEstado;

    private SpringApiService apiService;

    private final List<PedidoAdminAdapter.PedidoConDetalles> listaOriginal = new ArrayList<>();
    private final List<PedidoAdminAdapter.PedidoConDetalles> listaFiltrada = new ArrayList<>();

    private PedidoAdminAdapter adapter;

    private final Map<Integer, String> nombresProductos = new HashMap<>();
    private final Map<Integer, String> nombresPromos = new HashMap<>();

    private final String[] estadosFiltro = {
            "Todos", "Pendiente", "En proceso", "Terminado", "Pagado", "Cancelado"
    };

    public PedidosAdmin() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_pedidos_admin, container, false);

        rvPedidos = v.findViewById(R.id.recyclerPedidosAdmin);
        spFiltroEstado = v.findViewById(R.id.spFiltroEstadoPedidos);

        apiService = ApiClient.getInstance().create(SpringApiService.class);

        rvPedidos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PedidoAdminAdapter(listaFiltrada, this);
        rvPedidos.setAdapter(adapter);

        configurarSpinnerFiltro();
        cargarCatalogosProductosYPromos();
        cargarPedidos();

        return v;
    }

    private void configurarSpinnerFiltro() {
        ArrayAdapter<String> adapterFiltro = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                estadosFiltro
        );
        adapterFiltro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFiltroEstado.setAdapter(adapterFiltro);

        spFiltroEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarFiltro();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spFiltroEstado.setSelection(0);
    }

    private void cargarCatalogosProductosYPromos() {
        apiService.getProductos().enqueue(new Callback<List<ProductoDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductoDto>> call,
                                   @NonNull Response<List<ProductoDto>> response) {
                if (!isAdded() || !response.isSuccessful() || response.body() == null) return;

                nombresProductos.clear();
                for (ProductoDto p : response.body()) {
                    nombresProductos.put(p.getId(), p.getNombre());
                }
                adapter.setNombresProductos(nombresProductos);
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductoDto>> call,
                                  @NonNull Throwable t) { }
        });

        apiService.getPromociones().enqueue(new Callback<List<PromocionDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<PromocionDto>> call,
                                   @NonNull Response<List<PromocionDto>> response) {
                if (!isAdded() || !response.isSuccessful() || response.body() == null) return;

                nombresPromos.clear();
                for (PromocionDto p : response.body()) {
                    nombresPromos.put(p.getId(), p.getNombre());
                }
                adapter.setNombresPromos(nombresPromos);
            }

            @Override
            public void onFailure(@NonNull Call<List<PromocionDto>> call,
                                  @NonNull Throwable t) { }
        });
    }

    private void cargarPedidos() {
        apiService.getPedidos().enqueue(new Callback<List<PedidoDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<PedidoDto>> call,
                                   @NonNull Response<List<PedidoDto>> response) {
                if (!isAdded()) return;

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(),
                            "Error al obtener pedidos",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                listaOriginal.clear();

                for (PedidoDto p : response.body()) {

                    List<DetallePedidoDto> dets =
                            p.getDetalles() != null ? new ArrayList<>(p.getDetalles()) : new ArrayList<>();

                    listaOriginal.add(
                            new PedidoAdminAdapter.PedidoConDetalles(
                                    p,
                                    dets
                            )
                    );
                }

                aplicarFiltro();
            }

            @Override
            public void onFailure(@NonNull Call<List<PedidoDto>> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(),
                        "Error de conexión al obtener pedidos",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void aplicarFiltro() {
        if (!isAdded()) return;

        int idxFiltro = spFiltroEstado.getSelectedItemPosition(); // 0..5

        listaFiltrada.clear();

        for (PedidoAdminAdapter.PedidoConDetalles pc : listaOriginal) {
            if (idxFiltro == 0) {
                listaFiltrada.add(pc);
            } else {
                int estadoBuscado = idxFiltro; // 1..5
                if (pc.pedido.getEstado() == estadoBuscado) {
                    listaFiltrada.add(pc);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCambiarEstado(@NonNull PedidoDto pedido, int nuevoEstado) {
        PedidoDto body = new PedidoDto();
        body.setId(pedido.getId());
        body.setNumeroTicket(pedido.getNumeroTicket());
        body.setEstado(nuevoEstado);
        body.setTotal(pedido.getTotal());
        body.setObservaciones(pedido.getObservaciones());
        body.setIdCliente(pedido.getIdCliente());

        apiService.actualizarPedido(pedido.getId(), body)
                .enqueue(new Callback<PedidoDto>() {
                    @Override
                    public void onResponse(@NonNull Call<PedidoDto> call,
                                           @NonNull Response<PedidoDto> response) {
                        if (!isAdded()) return;

                        if (response.isSuccessful() && response.body() != null) {
                            pedido.setEstado(nuevoEstado);
                            aplicarFiltro();
                            Toast.makeText(getContext(),
                                    "Estado actualizado",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(),
                                    "Error al actualizar estado",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PedidoDto> call,
                                          @NonNull Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(),
                                "Error de conexión al actualizar estado",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
