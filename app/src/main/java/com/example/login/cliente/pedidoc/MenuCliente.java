package com.example.login.cliente.pedidoc;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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

    private enum TipoVista { PRODUCTOS, PROMOCIONES }

    private RecyclerView rvLista;
    private EditText etBuscar;
    private Spinner spTipoLista;
    private TextView tvTituloLista;

    private LinearLayout barraCarrito;
    private ImageView imgIconoCarrito;
    private TextView tvResumenCarrito;
    private Button btnObservaciones;
    private Button btnVerCarrito;

    private TextView tvTotalDialogCarrito;
    private CarritoClienteAdapter carritoAdapterDialog;

    private SpringApiService apiService;

    private final List<ProductoDto> productosOriginal = new ArrayList<>();
    private final List<ProductoDto> productosFiltrados = new ArrayList<>();

    private final List<PromocionDto> promosOriginal = new ArrayList<>();
    private final List<PromocionDto> promosFiltradas = new ArrayList<>();

    private final List<CategoriaProductoDto> categorias = new ArrayList<>();

    private ProductoClienteAdapter productosAdapter;
    private PromocionClienteAdapter promosAdapter;

    private TipoVista tipoActual = TipoVista.PRODUCTOS;

    private final List<CarritoClienteAdapter.ItemCarrito> carrito = new ArrayList<>();
    private String observaciones = "";

    public MenuCliente() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu_cliente, container, false);

        rvLista          = view.findViewById(R.id.recyclerListaCliente);
        etBuscar         = view.findViewById(R.id.etBuscarCliente);
        spTipoLista      = view.findViewById(R.id.spTipoListaCliente);
        tvTituloLista    = view.findViewById(R.id.tvTituloListaCliente);

        barraCarrito     = view.findViewById(R.id.barraCarritoCliente);
        imgIconoCarrito  = view.findViewById(R.id.imgIconoCarritoCliente);
        tvResumenCarrito = view.findViewById(R.id.tvResumenCarritoCliente);
        btnObservaciones = view.findViewById(R.id.btnObservacionesCliente);
        btnVerCarrito    = view.findViewById(R.id.btnVerCarritoCliente);

        apiService = ApiClient.getInstance().create(SpringApiService.class);

        // grid de 2 columnas
        rvLista.setLayoutManager(new GridLayoutManager(getContext(), 2));

        productosAdapter = new ProductoClienteAdapter(productosFiltrados, this);
        promosAdapter    = new PromocionClienteAdapter(promosFiltradas, this);

        configurarSpinnerTipo();
        configurarBusqueda();
        configurarBarraCarrito();

        cargarCategorias();
        cargarProductos();
        cargarPromociones();

        actualizarBarraCarrito();

        return view;
    }

    private void configurarSpinnerTipo() {
        List<String> tipos = new ArrayList<>();
        tipos.add("Productos");
        tipos.add("Promociones");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                tipos
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoLista.setAdapter(adapter);

        spTipoLista.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent,
                                       View view, int position, long id) {
                tipoActual = (position == 0) ? TipoVista.PRODUCTOS : TipoVista.PROMOCIONES;
                aplicarFiltros();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        spTipoLista.setSelection(0);
        tvTituloLista.setText("Productos disponibles");
    }

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

    private void configurarBarraCarrito() {
        btnObservaciones.setOnClickListener(v -> mostrarDialogObservaciones());
        btnVerCarrito.setOnClickListener(v -> mostrarDialogCarrito());
    }

    private void cargarCategorias() {
        apiService.getCategoriasProducto().enqueue(new Callback<List<CategoriaProductoDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoriaProductoDto>> call,
                                   @NonNull Response<List<CategoriaProductoDto>> response) {
                if (!isAdded()) return;
                if (!response.isSuccessful() || response.body() == null) return;

                categorias.clear();
                categorias.addAll(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoriaProductoDto>> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(),
                        "Error de conexión (categorías)", Toast.LENGTH_SHORT).show();
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
                            "Error al obtener productos", Toast.LENGTH_SHORT).show();
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
                        "Error de conexión (productos)", Toast.LENGTH_SHORT).show();
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
                            "Error al obtener promociones", Toast.LENGTH_SHORT).show();
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
                        "Error de conexión (promociones)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void aplicarFiltros() {
        String texto = etBuscar.getText().toString()
                .trim().toLowerCase(Locale.getDefault());

        if (tipoActual == TipoVista.PRODUCTOS) {
            tvTituloLista.setText("Productos disponibles");

            productosFiltrados.clear();
            for (ProductoDto p : productosOriginal) {
                if (!p.isDisponible()) continue;
                String nombre = p.getNombre() != null
                        ? p.getNombre().toLowerCase(Locale.getDefault()) : "";
                String desc = p.getDescripcion() != null
                        ? p.getDescripcion().toLowerCase(Locale.getDefault()) : "";
                if (texto.isEmpty() || nombre.contains(texto) || desc.contains(texto)) {
                    productosFiltrados.add(p);
                }
            }
            rvLista.setAdapter(productosAdapter);
            productosAdapter.notifyDataSetChanged();
        } else {
            tvTituloLista.setText("Promociones disponibles");

            promosFiltradas.clear();
            for (PromocionDto promo : promosOriginal) {
                if (!promo.isActivo()) continue;
                String nombre = promo.getNombre() != null
                        ? promo.getNombre().toLowerCase(Locale.getDefault()) : "";
                String desc = promo.getDescripcion() != null
                        ? promo.getDescripcion().toLowerCase(Locale.getDefault()) : "";
                if (texto.isEmpty() || nombre.contains(texto) || desc.contains(texto)) {
                    promosFiltradas.add(promo);
                }
            }
            rvLista.setAdapter(promosAdapter);
            promosAdapter.notifyDataSetChanged();
        }
    }

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

        for (CarritoClienteAdapter.ItemCarrito item : carrito) {
            if (item.tipo == CarritoClienteAdapter.ItemCarrito.Tipo.PRODUCTO
                    && item.idReferencia == producto.getId()) {
                item.cantidad++;
                actualizarBarraCarrito();
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
        actualizarBarraCarrito();
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
                actualizarBarraCarrito();
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
        actualizarBarraCarrito();
    }

    private void actualizarBarraCarrito() {
        int cantidad = getTotalCantidadCarrito();
        double total = 0;
        for (CarritoClienteAdapter.ItemCarrito item : carrito) {
            total += item.precioUnitario * item.cantidad;
        }

        if (cantidad == 0) {
            barraCarrito.setVisibility(View.GONE);
            tvResumenCarrito.setText("0 artículos · Total: $0.0");
        } else {
            barraCarrito.setVisibility(View.VISIBLE);
            tvResumenCarrito.setText(
                    cantidad + " artículo" + (cantidad == 1 ? "" : "s") +
                            " · Total: $" + total
            );
        }

        actualizarTotalDialog();
        if (carritoAdapterDialog != null) {
            carritoAdapterDialog.notifyDataSetChanged();
        }
    }

    private void mostrarDialogCarrito() {
        if (carrito.isEmpty()) {
            Toast.makeText(getContext(),
                    "Tu carrito está vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_carrito_cliente, null);

        RecyclerView rv = dialogView.findViewById(R.id.recyclerCarritoCliente);
        tvTotalDialogCarrito = dialogView.findViewById(R.id.tvTotalCarritoCliente);
        Button btnCerrar = dialogView.findViewById(R.id.btnCerrarCarrito);
        Button btnConfirmar = dialogView.findViewById(R.id.btnConfirmarPedidoCliente);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        carritoAdapterDialog = new CarritoClienteAdapter(carrito, this);
        rv.setAdapter(carritoAdapterDialog);

        actualizarTotalDialog();

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT)
            );
        }

        btnCerrar.setOnClickListener(v -> {
            dialog.dismiss();
            carritoAdapterDialog = null;
            tvTotalDialogCarrito = null;
        });

        btnConfirmar.setOnClickListener(v -> {
            confirmarPedido();
            dialog.dismiss();
            carritoAdapterDialog = null;
            tvTotalDialogCarrito = null;
        });

        dialog.show();
    }

    private void actualizarTotalDialog() {
        if (tvTotalDialogCarrito == null) return;
        double total = 0;
        for (CarritoClienteAdapter.ItemCarrito item : carrito) {
            total += item.precioUnitario * item.cantidad;
        }
        tvTotalDialogCarrito.setText("Total: $ " + total);
    }

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
                            "Observaciones guardadas", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });

        dialogView.findViewById(R.id.btnCancelarObservaciones)
                .setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

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
        pedido.setEstado(1); // PENDIENTE
        pedido.setTotal(total);
        pedido.setObservaciones(observaciones);
        pedido.setIdCliente(obtenerIdClienteActual());

        List<DetallePedidoDto> detalles = new ArrayList<>();
        for (CarritoClienteAdapter.ItemCarrito item : carrito) {
            DetallePedidoDto d = new DetallePedidoDto();
            d.setCantidad(item.cantidad);
            d.setPrecioUnitario(item.precioUnitario);

            if (item.tipo == CarritoClienteAdapter.ItemCarrito.Tipo.PRODUCTO) {
                d.setTipoItem(1);  // PRODUCTO
                d.setIdProducto(item.idReferencia);
                d.setIdPromocion(null);
            } else {
                d.setTipoItem(2);  // PROMOCION
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
                    mostrarTicket(creado);
                    carrito.clear();
                    observaciones = "";
                    actualizarBarraCarrito();
                } else {
                    String msg = "Error al crear pedido";

                    if (response.code() == 409) {
                        try {
                            String backendMsg =
                                    response.errorBody() != null
                                            ? response.errorBody().string()
                                            : null;
                            if (backendMsg != null && !backendMsg.isEmpty()) {
                                msg = backendMsg;
                            } else {
                                msg = "Por el momento el producto/promocion no se encuntra disponible.";
                            }
                        } catch (Exception e) {
                            msg = "Por el momento el producto/promocion no se encuntra disponible.";
                        }
                    }

                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
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
        if (getContext() == null) return 0;
        android.content.SharedPreferences prefs =
                getContext().getSharedPreferences("sesion", android.content.Context.MODE_PRIVATE);
        return prefs.getInt("idUsuario", 0);
    }


    private void mostrarTicket(@NonNull PedidoDto pedido) {
        StringBuilder sb = new StringBuilder();

        int width = 32;
        java.util.function.Function<String, String> center = text -> {
            int pad = (width - text.length()) / 2;
            pad = Math.max(pad, 0);
            return " ".repeat(pad) + text;
        };

        sb.append(center.apply("ANDY BURGER")).append("\n");
        sb.append(center.apply("------------------------------")).append("\n\n");

        sb.append("Ticket: ").append(pedido.getNumeroTicket()).append("\n");
        sb.append("Cliente: ").append(pedido.getIdCliente()).append("\n");

        String fecha = pedido.getFechaPedido() != null
                ? pedido.getFechaPedido()
                : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Calendar.getInstance().getTime());

        sb.append("Fecha: ").append(fecha).append("\n\n");

        sb.append(center.apply("DETALLE")).append("\n");
        sb.append(center.apply("------------------------------")).append("\n\n");

        for (CarritoClienteAdapter.ItemCarrito item : carrito) {
            String etiqueta = item.tipo == CarritoClienteAdapter.ItemCarrito.Tipo.PROMOCION
                    ? "(PROMO)"
                    : "(PROD)";

            double subtotal = item.precioUnitario * item.cantidad;

            sb.append(center.apply(etiqueta + " " + item.nombre)).append("\n");
            sb.append(String.format(
                    "   x%d   $%.2f c/u   →   $%.2f\n\n",
                    item.cantidad, item.precioUnitario, subtotal
            ));
        }

        sb.append(center.apply("------------------------------")).append("\n");
        sb.append(center.apply("TOTAL: $ " + pedido.getTotal())).append("\n\n");

        sb.append(center.apply("¡Gracias por su compra!")).append("\n");
        sb.append(center.apply("Vuelva pronto :)")).append("\n");

        // Crear TextView con fuente monoespaciada
        TextView tv = new TextView(getContext());
        tv.setText(sb.toString());
        tv.setTypeface(Typeface.MONOSPACE);
        tv.setPadding(40, 20, 40, 20);
        tv.setTextSize(15);

        new AlertDialog.Builder(getContext())
                .setTitle("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nTicket")
                .setView(tv)
                .setPositiveButton("Cerrar", null)
                .show();
    }

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
        actualizarBarraCarrito();
    }

    @Override
    public void onDecrementar(@NonNull CarritoClienteAdapter.ItemCarrito item) {
        if (item.cantidad > 1) {
            item.cantidad--;
        } else {
            carrito.remove(item);
        }
        actualizarBarraCarrito();
    }

    @Override
    public void onEliminar(@NonNull CarritoClienteAdapter.ItemCarrito item) {
        carrito.remove(item);
        actualizarBarraCarrito();
    }
}
