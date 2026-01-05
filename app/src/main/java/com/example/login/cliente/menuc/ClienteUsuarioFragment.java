package com.example.login.cliente.menuc;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;
import com.example.login.cliente.pedidoc.PedidoClienteAdapter;
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

public class ClienteUsuarioFragment extends Fragment {

    private ImageView ivAvatarCliente;
    private TextView tvNombreClienteUsuario;
    private TextView tvTelefonoClienteUsuario;
    private TextView tvFraseBienvenidaCliente;

    private Button btnVerPedidosCliente;
    private TextView tvSinPedidosCliente;
    private ProgressBar progressPedidosCliente;
    private RecyclerView recyclerPedidosCliente;

    private Button btnCerrarSesionCliente;

    private SpringApiService apiService;

    private final List<PedidoClienteAdapter.PedidoConDetalles> listaPedidos = new ArrayList<>();
    private PedidoClienteAdapter adapter;

    private final Map<Integer, String> nombresProductos = new HashMap<>();
    private final Map<Integer, String> nombresPromos = new HashMap<>();

    private int idClienteSesion = 0;

    public ClienteUsuarioFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cliente_usuario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivAvatarCliente = view.findViewById(R.id.ivAvatarCliente);
        tvNombreClienteUsuario = view.findViewById(R.id.tvNombreClienteUsuario);
        tvTelefonoClienteUsuario = view.findViewById(R.id.tvTelefonoClienteUsuario);
        tvFraseBienvenidaCliente = view.findViewById(R.id.tvFraseBienvenidaCliente);

        btnVerPedidosCliente = view.findViewById(R.id.btnVerPedidosCliente);
        tvSinPedidosCliente = view.findViewById(R.id.tvSinPedidosCliente);
        progressPedidosCliente = view.findViewById(R.id.progressPedidosCliente);
        recyclerPedidosCliente = view.findViewById(R.id.recyclerPedidosCliente);

        btnCerrarSesionCliente = view.findViewById(R.id.btnCerrarSesionCliente);

        apiService = ApiClient.getInstance().create(SpringApiService.class);

        SharedPreferences prefs = requireContext()
                .getSharedPreferences("sesion", Context.MODE_PRIVATE);

        idClienteSesion = prefs.getInt("idUsuario", 0);
        String nombre = prefs.getString("nombreUsuario", "Invitado");
        String telefono = prefs.getString("telefonoUsuario", "");

        tvNombreClienteUsuario.setText(nombre);

        if (telefono != null && !telefono.trim().isEmpty()) {
            tvTelefonoClienteUsuario.setText("Teléfono: " + telefono);
        } else {
            tvTelefonoClienteUsuario.setText("Teléfono: Sin teléfono");
        }

        tvFraseBienvenidaCliente.setText("¡Qué bueno verte por aquí, " + nombre + "!");

        recyclerPedidosCliente.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PedidoClienteAdapter(listaPedidos);
        recyclerPedidosCliente.setAdapter(adapter);

        cargarCatalogosProductosYPromos();

        btnVerPedidosCliente.setOnClickListener(v -> cargarPedidosCliente());

        NavController navController = Navigation.findNavController(view);
        btnCerrarSesionCliente.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(getContext(),
                    "Sesión cerrada correctamente",
                    Toast.LENGTH_SHORT).show();

            navController.navigate(R.id.loginfragment);
        });
    }

    private void cargarCatalogosProductosYPromos() {
        // Productos
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

    private void cargarPedidosCliente() {
        if (idClienteSesion == 0) {
            Toast.makeText(getContext(),
                    "No hay un usuario en sesión.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mostrarCargando(true);
        tvSinPedidosCliente.setVisibility(View.GONE);
        recyclerPedidosCliente.setVisibility(View.GONE);

        apiService.getPedidos().enqueue(new Callback<List<PedidoDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<PedidoDto>> call,
                                   @NonNull Response<List<PedidoDto>> response) {
                if (!isAdded()) return;
                mostrarCargando(false);

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(),
                            "Error al obtener pedidos",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                listaPedidos.clear();

                for (PedidoDto p : response.body()) {
                    if (p.getIdCliente() == idClienteSesion) {
                        listaPedidos.add(new PedidoClienteAdapter.PedidoConDetalles(
                                p,
                                new ArrayList<>()
                        ));
                        cargarDetallesDePedido(p.getId());
                    }
                }

                if (listaPedidos.isEmpty()) {
                    tvSinPedidosCliente.setVisibility(View.VISIBLE);
                    recyclerPedidosCliente.setVisibility(View.GONE);
                    Toast.makeText(getContext(),
                            "Aún no has realizado pedidos.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    tvSinPedidosCliente.setVisibility(View.GONE);
                    recyclerPedidosCliente.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<PedidoDto>> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                mostrarCargando(false);
                Toast.makeText(getContext(),
                        "Error de conexión al obtener pedidos",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarDetallesDePedido(int idPedido) {
        apiService.getDetallesPorPedido(idPedido)
                .enqueue(new Callback<List<DetallePedidoDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<DetallePedidoDto>> call,
                                           @NonNull Response<List<DetallePedidoDto>> response) {
                        if (!isAdded()
                                || !response.isSuccessful()
                                || response.body() == null) return;

                        for (PedidoClienteAdapter.PedidoConDetalles pc : listaPedidos) {
                            if (pc.pedido.getId() == idPedido) {
                                pc.detalles.clear();
                                pc.detalles.addAll(response.body());
                                break;
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<DetallePedidoDto>> call,
                                          @NonNull Throwable t) { }
                });
    }

    private void mostrarCargando(boolean cargando) {
        progressPedidosCliente.setVisibility(cargando ? View.VISIBLE : View.GONE);
    }
}
