package com.example.login.cliente;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;
import com.example.login.network.ApiClient;
import com.example.login.network.SpringApiService;
import com.example.login.network.model.CategoriaProductoDto;
import com.example.login.network.model.DetallePedidoDto;
import com.example.login.network.model.PedidoDto;
import com.example.login.network.model.ProductoDto;
import com.example.login.network.model.PromocionDto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuCliente extends Fragment
        implements ProductoClienteAdapter.OnProductoClienteListener,
        PromocionClienteAdapter.OnPromocionClienteListener,
        CarritoClienteAdapter.OnCarritoListener {

    private static final int MAX_ARTICULOS = 15;

    private RecyclerView rvPromos, rvProductos, rvCarrito;
    private EditText etBuscar;
    private Spinner spCategorias;
    private TextView tvTotal;
    private Button btnObservaciones, btnConfirmar;

    private SpringApiService apiService;

    // listas catálogo
    private final List<PromocionDto> promosOriginal = new ArrayList<>();
    private final List<PromocionDto> promosFiltradas = new ArrayList<>();
    private final List<ProductoDto> productosOriginal = new ArrayList<>();
    private final List<ProductoDto> productosFiltrados = new ArrayList<>();
    private final List<CategoriaProductoDto> categorias = new ArrayList<>();

    private PromocionClienteAdapter promoAdapter;
    private ProductoClienteAdapter prodAdapter;

    // carrito
    private final List<CarritoClienteAdapter.ItemCarrito> carrito = new ArrayList<>();
    private CarritoClienteAdapter carritoAdapter;

    private String observaciones = "";

    public MenuCliente() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu_cliente, container, false);

        rvPromos = view.findViewById(R.id.recyclerPromosCliente);
        rvProductos = view.findViewById(R.id.recyclerProductosCliente);
        rvCarrito = view.findViewById(R.id.recyclerCarritoCliente);
        etBuscar = view.findViewById(R.id.etBuscarCliente);
        spCategorias = view.findViewById(R.id.spCategoriaCliente);
        tvTotal = view.findViewById(R.id.tvTotalCarritoCliente);
        btnObservaciones = view.findViewById(R.id.btnObservacionesCliente);
        btnConfirmar = view.findViewById(R.id.btnConfirmarPedidoCliente);

        apiService = ApiClient.getInstance().create(SpringApiService.class);

        rvPromos.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        rvProductos.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        rvCarrito.setLayoutManager(new LinearLayoutManager(getContext()));

        promoAdapter = new PromocionClienteAdapter(promosFiltradas, this);
        prodAdapter = new ProductoClienteAdapter(productosFiltrados, this);
        carritoAdapter = new CarritoClienteAdapter(carrito, this);

        rvPromos.setAdapter(promoAdapter);
        rvProductos.setAdapter(prodAdapter);
        rvCarrito.setAdapter(carritoAdapter);

        configurarBusqueda();
        configurarSpinnerCategorias();
        configurarBotones();

        cargarCategorias();
        cargarPromociones();
        cargarProductos();

        actualizarTotal();

        return view;
    }

    // --------- búsqueda y filtros ---------

    private void configurarBusqueda() {
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                aplicarFiltros();
            }
        });
    }

    private void configurarSpinnerCategorias() {
        // de inicio solo "Todas"
        List<String> nombres = new ArrayList<>();
        nombres.add("Todas las categorías");
        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                nombres
        );
        adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategorias.setAdapter(adapterCat);

        spCategorias.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent,
                                       View view, int position, long id) {
                aplicarFiltros();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });
    }

    private void configurarBotones() {
        btnObservaciones.setOnClickListener(v -> mostrarDialogObservaciones());
        btnConfirmar.setOnClickListener(v -> confirmarPedido());
    }

    private void aplicarFiltros() {
        String texto = etBuscar.getText().toString().trim().toLowerCase(Locale.getDefault());
        int posCat = spCategorias.getSelectedItemPosition();
        Integer idCategoriaFiltro = null;

        if (posCat > 0 && posCat - 1 < categorias.size()) {
            idCategoriaFiltro = categorias.get(posCat - 1).getId();
        }

        // FILTRO PROMOS (solo texto)
        promosFiltradas.clear();
        for (PromocionDto p : promosOriginal) {
            if (!p.isActivo()) continue;
            String nombre = p.getNombre() != null ? p.getNombre().toLowerCase(Locale.getDefault()) : "";
            String desc = p.getDescripcion() != null ? p.getDescripcion().toLowerCase(Locale.getDefault()) : "";
            if (texto.isEmpty() || nombre.contains(texto) || desc.contains(texto)) {
                promosFiltradas.add(p);
            }
        }
        promoAdapter.notifyDataSetChanged();

        // FILTRO PRODUCTOS (texto + categoría + disponible)
        productosFiltrados.clear();
        for (ProductoDto p : productosOriginal) {
            if (!p.isDisponible()) continue;

            if (idCategoriaFiltro != null && p.getIdCategoria() != idCategoriaFiltro) {
                continue;
            }

            String nombre = p.getNombre() != null ? p.getNombre().toLowerCase(Locale.getDefault()) : "";
            String desc = p.getDescripcion() != null ? p.getDescripcion().toLowerCase(Locale.getDefault()) : "";

            if (texto.isEmpty() || nombre.contains(texto) || desc.contains(texto)) {
                productosFiltrados.add(p);
            }
        }
        prodAdapter.notifyDataSetChanged();
    }

    // --------- carga de datos ---------

    private void cargarCategorias() {
        apiService.getCategoriasProducto().enqueue(new Callback<List<CategoriaProductoDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoriaProductoDto>> call,
                                   @NonNull Response<List<CategoriaProductoDto>> response) {
                if (!isAdded()) return;
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(),
                            "Error al obtener categorías",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                categorias.clear();
                categorias.addAll(response.body());

                List<String> nombres = new ArrayList<>();
                nombres.add("Todas las categorías");
                for (CategoriaProductoDto c : categorias) {
                    nombres.add(c.getNombre());
                }

                ArrayAdapter<String> adapterCat = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        nombres
                );
                adapterCat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCategorias.setAdapter(adapterCat);
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoriaProductoDto>> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(),
                        "Error de conexión (categorías)",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarPromociones() {
        apiService.getPromociones().enqueue(new Callback<List<PromocionDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<PromocionDto>> call,
                                   @NonNull Response<List<PromocionDto>> response) {
                if (!isAdded()) return;
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(),
                            "Error al obtener promociones",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                promosOriginal.clear();
                promosOriginal.addAll(response.body());
                aplicarFiltros();
            }

            @Override
            public void onFailure(@NonNull Call<List<PromocionDto>> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(),
                        "Error de conexión (promociones)",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarProductos() {
        apiService.getProductos().enqueue(new Callback<List<ProductoDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductoDto>> call,
                                   @NonNull Response<List<ProductoDto>> response) {
                if (!isAdded()) return;
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(),
                            "Error al obtener productos",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                productosOriginal.clear();
                productosOriginal.addAll(response.body());
                aplicarFiltros();
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductoDto>> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(),
                        "Error de conexión (productos)",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --------- manejo de carrito ---------

    private int getTotalCantidadCarrito() {
        int total = 0;
        for (CarritoClienteAdapter.ItemCarrito item : carrito) {
            total += item.cantidad;
        }
        return total;
    }

    private void agregarAlCarritoProducto(@NonNull ProductoDto producto) {
        if (getTotalCantidadCarrito() >= MAX_ARTICULOS) {
            Toast.makeText(getContext(),
                    "Máximo " + MAX_ARTICULOS + " artículos por pedido",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // buscar si ya existe
        for (CarritoClienteAdapter.ItemCarrito item : carrito) {
            if (item.tipo == CarritoClienteAdapter.ItemCarrito.Tipo.PRODUCTO
                    && item.idReferencia == producto.getId()) {
                item.cantidad++;
                carritoAdapter.notifyDataSetChanged();
                actualizarTotal();
                return;
            }
        }

        CarritoClienteAdapter.ItemCarrito nuevo = new CarritoClienteAdapter.ItemCarrito();
        nuevo.tipo = CarritoClienteAdapter.ItemCarrito.Tipo.PRODUCTO;
        nuevo.idReferencia = producto.getId();
        nuevo.nombre = producto.getNombre();
        nuevo.precioUnitario = producto.getPrecio();
        nuevo.cantidad = 1;
        carrito.add(nuevo);
        carritoAdapter.notifyDataSetChanged();
        actualizarTotal();
    }

    private void agregarAlCarritoPromocion(@NonNull PromocionDto promo) {
        if (getTotalCantidadCarrito() >= MAX_ARTICULOS) {
            Toast.makeText(getContext(),
                    "Máximo " + MAX_ARTICULOS + " artículos por pedido",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        for (CarritoClienteAdapter.ItemCarrito item : carrito) {
            if (item.tipo == CarritoClienteAdapter.ItemCarrito.Tipo.PROMOCION
                    && item.idReferencia == promo.getId()) {
                item.cantidad++;
                carritoAdapter.notifyDataSetChanged();
                actualizarTotal();
                return;
            }
        }

        CarritoClienteAdapter.ItemCarrito nuevo = new CarritoClienteAdapter.ItemCarrito();
        nuevo.tipo = CarritoClienteAdapter.ItemCarrito.Tipo.PROMOCION;
        nuevo.idReferencia = promo.getId();
        nuevo.nombre = promo.getNombre();
        nuevo.precioUnitario = promo.getPrecioTotalConDescuento();
        nuevo.cantidad = 1;
        carrito.add(nuevo);
        carritoAdapter.notifyDataSetChanged();
        actualizarTotal();
    }

    private void actualizarTotal() {
        double total = 0;
        for (CarritoClienteAdapter.ItemCarrito item : carrito) {
            total += item.precioUnitario * item.cantidad;
        }
        tvTotal.setText("Total: $ " + total);
    }

    // --------- Observaciones ---------

    private void mostrarDialogObservaciones() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_observaciones_cliente, null);

        EditText etObs = dialogView.findViewById(R.id.etObservacionesCliente);
        etObs.setText(observaciones);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btnGuardarObservaciones)
                .setOnClickListener(v -> {
                    observaciones = etObs.getText().toString().trim();
                    Toast.makeText(getContext(),
                            "Observaciones guardadas",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });

        dialogView.findViewById(R.id.btnCancelarObservaciones)
                .setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // --------- Confirmar pedido ---------

    private void confirmarPedido() {
        if (carrito.isEmpty()) {
            Toast.makeText(getContext(),
                    "Agrega al menos un producto o promoción",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        double total = 0;
        for (CarritoClienteAdapter.ItemCarrito item : carrito) {
            total += item.precioUnitario * item.cantidad;
        }

        PedidoDto pedido = new PedidoDto();
        pedido.setNumeroTicket(generarNumeroTicket());
        pedido.setEstado(1);
        pedido.setTotal(total);
        pedido.setObservaciones(observaciones);

        // TODO: obtén el idCliente real de tu sesión / SharedPreferences
        int idCliente = obtenerIdClienteActual();
        pedido.setIdCliente(idCliente);

        List<DetallePedidoDto> detalles = new ArrayList<>();
        for (CarritoClienteAdapter.ItemCarrito item : carrito) {
            DetallePedidoDto d = new DetallePedidoDto();
            d.setCantidad(item.cantidad);
            d.setPrecioUnitario(item.precioUnitario);
            if (item.tipo == CarritoClienteAdapter.ItemCarrito.Tipo.PRODUCTO) {
                d.setTipo("PRODUCTO");
                d.setIdProducto(item.idReferencia);
                d.setIdPromocion(null);
            } else {
                d.setTipo("PROMOCION");
                d.setIdPromocion(item.idReferencia);
                d.setIdProducto(null);
            }
            detalles.add(d);
        }
        pedido.setDetalles(detalles);

        apiService.crearPedido(pedido).enqueue(new Callback<PedidoDto>() {
            @Override
            public void onResponse(@NonNull Call<PedidoDto> call,
                                   @NonNull Response<PedidoDto> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    PedidoDto creado = response.body();
                    mostrarTicket(creado, carrito);
                    carrito.clear();
                    carritoAdapter.notifyDataSetChanged();
                    observaciones = "";
                    actualizarTotal();
                } else {
                    Toast.makeText(getContext(),
                            "Error al crear pedido",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PedidoDto> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(),
                        "Error de conexión al crear pedido",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String generarNumeroTicket() {
        // 6 caracteres alfanuméricos
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int idx = r.nextInt(chars.length());
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }

    private int obtenerIdClienteActual() {
        // AQUÍ pones tu lógica real (SharedPreferences, singleton de sesión, etc.)
        // Por ahora dejo un valor de ejemplo:
        return 1;
    }

    private void mostrarTicket(@NonNull PedidoDto pedido,
                               @NonNull List<CarritoClienteAdapter.ItemCarrito> items) {

        StringBuilder sb = new StringBuilder();
        sb.append("Ticket: ").append(pedido.getNumeroTicket()).append("\n");
        sb.append("Cliente ID: ").append(pedido.getIdCliente()).append("\n");

        String fecha;
        if (pedido.getFechaPedido() != null) {
            fecha = pedido.getFechaPedido();
        } else {
            SimpleDateFormat sdf =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            fecha = sdf.format(Calendar.getInstance().getTime());
        }
        sb.append("Fecha: ").append(fecha).append("\n\n");
        sb.append("DETALLE\n");
        sb.append("--------------------------------\n");

        for (CarritoClienteAdapter.ItemCarrito item : items) {
            String etiqueta = item.tipo == CarritoClienteAdapter.ItemCarrito.Tipo.PROMOCION
                    ? "(PROMO)"
                    : "";
            double subtotal = item.precioUnitario * item.cantidad;
            sb.append(item.nombre).append(" ").append(etiqueta)
                    .append(" x").append(item.cantidad)
                    .append("    $").append(subtotal)
                    .append("\n");
        }

        sb.append("--------------------------------\n");
        sb.append("TOTAL = $ ").append(pedido.getTotal()).append("\n\n");
        sb.append("¡Gracias por su visita!\nVuelva pronto :)");

        new AlertDialog.Builder(getContext())
                .setTitle("Ticket")
                .setMessage(sb.toString())
                .setPositiveButton("Cerrar", null)
                .show();
    }

    // --------- callbacks de adapters ---------

    @Override
    public void onAgregarProducto(@NonNull ProductoDto producto) {
        agregarAlCarritoProducto(producto);
    }

    @Override
    public void onAgregarPromocion(@NonNull PromocionDto promocion) {
        agregarAlCarritoPromocion(promocion);
    }

    @Override
    public void onIncrementar(@NonNull CarritoClienteAdapter.ItemCarrito item) {
        if (getTotalCantidadCarrito() >= MAX_ARTICULOS) {
            Toast.makeText(getContext(),
                    "Máximo " + MAX_ARTICULOS + " artículos por pedido",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        item.cantidad++;
        carritoAdapter.notifyDataSetChanged();
        actualizarTotal();
    }

    @Override
    public void onDecrementar(@NonNull CarritoClienteAdapter.ItemCarrito item) {
        if (item.cantidad > 1) {
            item.cantidad--;
        } else {
            carrito.remove(item);
        }
        carritoAdapter.notifyDataSetChanged();
        actualizarTotal();
    }

    @Override
    public void onEliminar(@NonNull CarritoClienteAdapter.ItemCarrito item) {
        carrito.remove(item);
        carritoAdapter.notifyDataSetChanged();
        actualizarTotal();
    }
}
