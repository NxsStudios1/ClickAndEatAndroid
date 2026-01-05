package com.example.login.administrador.inventarioa;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.example.login.network.model.CategoriaProductoDto;
import com.example.login.network.model.IngredienteDto;
import com.example.login.network.model.ProductoDto;
import com.example.login.network.model.ProductoIngredienteDto;
import com.example.login.network.model.PromocionDto;
import com.example.login.network.model.PromocionProductoDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException; // <-- IMPORT NUEVO
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventarioAdminFragment extends Fragment
        implements IngredientesAdminAdapter.OnIngredienteClickListener,
        CategoriaProductoAdminAdapter.OnCategoriaClickListener,
        ProductosAdminAdapter.OnProductoClickListener,
        PromocionesAdminAdapter.OnPromocionClickListener {

    private enum Modo {
        INGREDIENTES,
        CATEGORIAS,
        PRODUCTOS,
        PROMOCIONES
    }

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private EditText etBuscar;
    private TabLayout tabLayout;


    private final List<IngredienteDto> listaIngOriginal = new ArrayList<>();
    private final List<IngredienteDto> listaIngFiltrada = new ArrayList<>();
    private IngredientesAdminAdapter ingredientesAdapter;

    private final List<CategoriaProductoDto> listaCatOriginal = new ArrayList<>();
    private final List<CategoriaProductoDto> listaCatFiltrada = new ArrayList<>();
    private CategoriaProductoAdminAdapter categoriasAdapter;

    private final List<ProductoDto> listaProdOriginal = new ArrayList<>();
    private final List<ProductoDto> listaProdFiltrada = new ArrayList<>();
    private ProductosAdminAdapter productosAdapter;

    private final List<PromocionDto> listaPromoOriginal = new ArrayList<>();
    private final List<PromocionDto> listaPromoFiltrada = new ArrayList<>();
    private PromocionesAdminAdapter promocionesAdapter;

    private Modo modoActual = Modo.INGREDIENTES;

    private SpringApiService apiService;

    public InventarioAdminFragment() { }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inventario_admin, container, false);

        recyclerView = view.findViewById(R.id.recyclerIngredientes);
        fab = view.findViewById(R.id.fabAgregarIngrediente);
        etBuscar = view.findViewById(R.id.etBuscarIngrediente);
        tabLayout = view.findViewById(R.id.tabLayoutInventario);

        apiService = ApiClient.getInstance().create(SpringApiService.class);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Adapters
        ingredientesAdapter = new IngredientesAdminAdapter(listaIngFiltrada, this);
        categoriasAdapter = new CategoriaProductoAdminAdapter(listaCatFiltrada, this);
        productosAdapter = new ProductosAdminAdapter(listaProdFiltrada, this);
        promocionesAdapter = new PromocionesAdminAdapter(listaPromoFiltrada, this);

        recyclerView.setAdapter(ingredientesAdapter);

        configurarTabs();
        configurarBusqueda();
        cargarIngredientes();

        fab.setOnClickListener(v -> {
            switch (modoActual) {
                case INGREDIENTES:
                    mostrarDialogoAgregarIngrediente();
                    break;
                case CATEGORIAS:
                    mostrarDialogoCategoriaProducto(null);
                    break;
                case PRODUCTOS:
                    mostrarDialogoProducto(null);
                    break;
                case PROMOCIONES:
                    mostrarDialogoPromocion(null);
                    break;
            }
        });

        return view;
    }


    private void configurarTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Ingredientes"));
        tabLayout.addTab(tabLayout.newTab().setText("Cat. producto"));
        tabLayout.addTab(tabLayout.newTab().setText("Productos"));
        tabLayout.addTab(tabLayout.newTab().setText("Promociones"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                int pos = tab.getPosition();
                switch (pos) {
                    case 0:
                        modoActual = Modo.INGREDIENTES;
                        etBuscar.setHint("Buscar ingrediente...");
                        recyclerView.setAdapter(ingredientesAdapter);
                        fab.show();
                        cargarIngredientes();
                        break;
                    case 1:
                        modoActual = Modo.CATEGORIAS;
                        etBuscar.setHint("Buscar categoría...");
                        recyclerView.setAdapter(categoriasAdapter);
                        fab.show();
                        cargarCategorias();
                        break;
                    case 2:
                        modoActual = Modo.PRODUCTOS;
                        etBuscar.setHint("Buscar producto...");
                        recyclerView.setAdapter(productosAdapter);
                        fab.show();
                        cargarProductos();
                        break;
                    case 3:
                        modoActual = Modo.PROMOCIONES;
                        etBuscar.setHint("Buscar promoción...");
                        recyclerView.setAdapter(promocionesAdapter);
                        fab.show();
                        cargarPromociones();
                        break;
                }
            }

            @Override public void onTabUnselected(@NonNull TabLayout.Tab tab) { }
            @Override public void onTabReselected(@NonNull TabLayout.Tab tab) { }
        });

        TabLayout.Tab primera = tabLayout.getTabAt(0);
        if (primera != null) primera.select();
    }


    private void configurarBusqueda() {
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String texto = s.toString();
                switch (modoActual) {
                    case INGREDIENTES:
                        filtrarIngredientes(texto);
                        break;
                    case CATEGORIAS:
                        filtrarCategorias(texto);
                        break;
                    case PRODUCTOS:
                        filtrarProductos(texto);
                        break;
                    case PROMOCIONES:
                        filtrarPromociones(texto);
                        break;
                }
            }
        });
    }

    private void filtrarIngredientes(String texto) {
        listaIngFiltrada.clear();
        if (texto == null || texto.trim().isEmpty()) {
            listaIngFiltrada.addAll(listaIngOriginal);
        } else {
            String q = texto.trim().toLowerCase();
            for (IngredienteDto ing : listaIngOriginal) {
                String nombre = ing.getNombre() != null ? ing.getNombre().toLowerCase() : "";
                String descripcion = ing.getDescripcion() != null ? ing.getDescripcion().toLowerCase() : "";
                if (nombre.contains(q) || descripcion.contains(q)) {
                    listaIngFiltrada.add(ing);
                }
            }
        }
        ingredientesAdapter.notifyDataSetChanged();
    }

    private void filtrarCategorias(String texto) {
        listaCatFiltrada.clear();
        if (texto == null || texto.trim().isEmpty()) {
            listaCatFiltrada.addAll(listaCatOriginal);
        } else {
            String q = texto.trim().toLowerCase();
            for (CategoriaProductoDto c : listaCatOriginal) {
                String nombre = c.getNombre() != null ? c.getNombre().toLowerCase() : "";
                if (nombre.contains(q)) {
                    listaCatFiltrada.add(c);
                }
            }
        }
        categoriasAdapter.notifyDataSetChanged();
    }

    private void filtrarProductos(String texto) {
        listaProdFiltrada.clear();
        if (texto == null || texto.trim().isEmpty()) {
            listaProdFiltrada.addAll(listaProdOriginal);
        } else {
            String q = texto.trim().toLowerCase();
            for (ProductoDto p : listaProdOriginal) {
                String nombre = p.getNombre() != null ? p.getNombre().toLowerCase() : "";
                String descripcion = p.getDescripcion() != null ? p.getDescripcion().toLowerCase() : "";
                if (nombre.contains(q) || descripcion.contains(q)) {
                    listaProdFiltrada.add(p);
                }
            }
        }
        productosAdapter.notifyDataSetChanged();
    }

    private void filtrarPromociones(String texto) {
        listaPromoFiltrada.clear();
        if (texto == null || texto.trim().isEmpty()) {
            listaPromoFiltrada.addAll(listaPromoOriginal);
        } else {
            String q = texto.trim().toLowerCase();
            for (PromocionDto p : listaPromoOriginal) {
                String nombre = p.getNombre() != null ? p.getNombre().toLowerCase() : "";
                String descripcion = p.getDescripcion() != null ? p.getDescripcion().toLowerCase() : "";
                if (nombre.contains(q) || descripcion.contains(q)) {
                    listaPromoFiltrada.add(p);
                }
            }
        }
        promocionesAdapter.notifyDataSetChanged();
    }


    private void cargarIngredientes() {
        apiService.getIngredientes().enqueue(new Callback<List<IngredienteDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<IngredienteDto>> call,
                                   @NonNull Response<List<IngredienteDto>> response) {
                if (!isAdded()) return;

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(),
                            "Error al obtener ingredientes",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                listaIngOriginal.clear();
                listaIngOriginal.addAll(response.body());
                filtrarIngredientes(etBuscar.getText().toString());
            }

            @Override
            public void onFailure(@NonNull Call<List<IngredienteDto>> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(),
                        "Error de conexión (ingredientes)",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

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

                listaCatOriginal.clear();
                listaCatOriginal.addAll(response.body());
                filtrarCategorias(etBuscar.getText().toString());

                productosAdapter.setCategorias(listaCatOriginal);
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

    private void cargarProductos() {
        apiService.getProductos().enqueue(new Callback<List<ProductoDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductoDto>> call,
                                   @NonNull Response<List<ProductoDto>> response) {
                if (!isAdded()) return;

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(getContext(),
                            "Error al obtener productos (código " + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                listaProdOriginal.clear();
                listaProdOriginal.addAll(response.body());
                filtrarProductos(etBuscar.getText().toString());
            }

            @Override
            public void onFailure(@NonNull Call<List<ProductoDto>> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(),
                        "Error de conexión (productos): " + t.getLocalizedMessage(),
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

                listaPromoOriginal.clear();
                listaPromoOriginal.addAll(response.body());

                for (PromocionDto p : listaPromoOriginal) {
                    if (p.isActivo() && esFechaFinPasada(p.getFechaFin())) {
                        p.setActivo(false);

                        PromocionDto cambios = new PromocionDto();
                        cambios.setNombre(p.getNombre());
                        cambios.setDescripcion(p.getDescripcion());
                        cambios.setFechaInicio(p.getFechaInicio());
                        cambios.setFechaFin(p.getFechaFin());
                        cambios.setPrecioTotalConDescuento(p.getPrecioTotalConDescuento());
                        cambios.setActivo(false);

                        apiService.actualizarPromocion(p.getId(), cambios)
                                .enqueue(new Callback<PromocionDto>() {
                                    @Override
                                    public void onResponse(@NonNull Call<PromocionDto> call,
                                                           @NonNull Response<PromocionDto> response) { }

                                    @Override
                                    public void onFailure(@NonNull Call<PromocionDto> call,
                                                          @NonNull Throwable t) { }
                                });
                    }
                }

                filtrarPromociones(etBuscar.getText().toString());
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


    private void mostrarDialogoAgregarIngrediente() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_ingrediente_admin, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombreIngrediente);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionIngrediente);
        EditText etCantidad = dialogView.findViewById(R.id.etCantidadPorcion);
        Spinner spUnidad = dialogView.findViewById(R.id.spUnidadMedida);
        EditText etStock = dialogView.findViewById(R.id.etStockActual);
        EditText etPrecio = dialogView.findViewById(R.id.etPrecioUnitario);

        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.unidades_medida,
                android.R.layout.simple_spinner_item
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnidad.setAdapter(adapterSpinner);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btnGuardarIngrediente).setOnClickListener(v -> {

            String nombre = etNombre.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            String sCantidad = etCantidad.getText().toString().trim();
            String sStock = etStock.getText().toString().trim();
            String sPrecio = etPrecio.getText().toString().trim();
            String unidad = spUnidad.getSelectedItem().toString();

            if (nombre.isEmpty() || descripcion.isEmpty() || sCantidad.isEmpty()
                    || unidad.isEmpty() || sStock.isEmpty() || sPrecio.isEmpty()) {
                Toast.makeText(getContext(),
                        "Todos los campos son obligatorios",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            double cantidad = Double.parseDouble(sCantidad);
            double stock = Double.parseDouble(sStock);
            double precio = Double.parseDouble(sPrecio);

            IngredienteDto nuevo = new IngredienteDto();
            nuevo.setNombre(nombre);
            nuevo.setDescripcion(descripcion);
            nuevo.setCantidadPorcion(cantidad);
            nuevo.setUnidadMedida(unidad);
            nuevo.setStockActual(stock);
            nuevo.setPrecioUnitario(precio);

            apiService.crearIngrediente(nuevo).enqueue(new Callback<IngredienteDto>() {
                @Override
                public void onResponse(@NonNull Call<IngredienteDto> call,
                                       @NonNull Response<IngredienteDto> response) {
                    if (!isAdded()) return;

                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(),
                                "Ingrediente creado correctamente",
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        cargarIngredientes();
                    } else {
                        Toast.makeText(getContext(),
                                "Error al crear ingrediente",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<IngredienteDto> call,
                                      @NonNull Throwable t) {
                    if (!isAdded()) return;
                    Toast.makeText(getContext(),
                            "Error de conexión",
                            Toast.LENGTH_SHORT).show();
                }
            });

        });

        dialogView.findViewById(R.id.btnCancelarIngrediente)
                .setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void mostrarDialogoEditar(@NonNull IngredienteDto ingrediente) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_ingrediente_admin, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombreIngrediente);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionIngrediente);
        EditText etCantidad = dialogView.findViewById(R.id.etCantidadPorcion);
        Spinner spUnidad = dialogView.findViewById(R.id.spUnidadMedida);
        EditText etStock = dialogView.findViewById(R.id.etStockActual);
        EditText etPrecio = dialogView.findViewById(R.id.etPrecioUnitario);

        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.unidades_medida,
                android.R.layout.simple_spinner_item
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnidad.setAdapter(adapterSpinner);

        etNombre.setText(ingrediente.getNombre());
        etDescripcion.setText(ingrediente.getDescripcion());
        etCantidad.setText(String.valueOf(ingrediente.getCantidadPorcion()));
        etStock.setText(String.valueOf(ingrediente.getStockActual()));
        etPrecio.setText(String.valueOf(ingrediente.getPrecioUnitario()));

        if (ingrediente.getUnidadMedida() != null) {
            String unidadActual = ingrediente.getUnidadMedida();
            for (int i = 0; i < adapterSpinner.getCount(); i++) {
                CharSequence item = adapterSpinner.getItem(i);
                if (item != null &&
                        unidadActual.equalsIgnoreCase(item.toString())) {
                    spUnidad.setSelection(i);
                    break;
                }
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btnGuardarIngrediente).setOnClickListener(v -> {

            String nombre = etNombre.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            String sCantidad = etCantidad.getText().toString().trim();
            String sStock = etStock.getText().toString().trim();
            String sPrecio = etPrecio.getText().toString().trim();
            String unidad = spUnidad.getSelectedItem().toString();

            if (nombre.isEmpty() || descripcion.isEmpty() || sCantidad.isEmpty()
                    || unidad.isEmpty() || sStock.isEmpty() || sPrecio.isEmpty()) {
                Toast.makeText(getContext(),
                        "Todos los campos son obligatorios",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            double cantidad = Double.parseDouble(sCantidad);
            double stock = Double.parseDouble(sStock);
            double precio = Double.parseDouble(sPrecio);

            IngredienteDto cambios = new IngredienteDto();
            cambios.setNombre(nombre);
            cambios.setDescripcion(descripcion);
            cambios.setCantidadPorcion(cantidad);
            cambios.setUnidadMedida(unidad);
            cambios.setStockActual(stock);
            cambios.setPrecioUnitario(precio);

            apiService.actualizarIngrediente(ingrediente.getId(), cambios)
                    .enqueue(new Callback<IngredienteDto>() {
                        @Override
                        public void onResponse(@NonNull Call<IngredienteDto> call,
                                               @NonNull Response<IngredienteDto> response) {
                            if (!isAdded()) return;
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(getContext(),
                                        "Ingrediente actualizado",
                                        Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                cargarIngredientes();
                            } else {
                                Toast.makeText(getContext(),
                                        "Error al actualizar (código " + response.code() + ")",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<IngredienteDto> call,
                                              @NonNull Throwable t) {
                            if (!isAdded()) return;
                            Toast.makeText(getContext(),
                                    "Error de conexión al actualizar",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        dialogView.findViewById(R.id.btnCancelarIngrediente)
                .setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void mostrarDialogoCategoriaProducto(@Nullable CategoriaProductoDto categoria) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_categoria_producto_admin, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombreCategoria);

        boolean esEdicion = (categoria != null);
        if (esEdicion) {
            etNombre.setText(categoria.getNombre());
        }

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btnGuardarCategoria).setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();

            if (nombre.isEmpty()) {
                Toast.makeText(getContext(),
                        "El nombre es obligatorio",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            CategoriaProductoDto dto = new CategoriaProductoDto();
            dto.setNombre(nombre);
            dto.setDescripcion("");

            if (!esEdicion) {
                apiService.crearCategoriaProducto(dto)
                        .enqueue(new Callback<CategoriaProductoDto>() {
                            @Override
                            public void onResponse(
                                    @NonNull Call<CategoriaProductoDto> call,
                                    @NonNull Response<CategoriaProductoDto> response) {
                                if (!isAdded()) return;
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(),
                                            "Categoría creada",
                                            Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    cargarCategorias();
                                } else {
                                    Toast.makeText(getContext(),
                                            "Error al crear categoría",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    @NonNull Call<CategoriaProductoDto> call,
                                    @NonNull Throwable t) {
                                if (!isAdded()) return;
                                Toast.makeText(getContext(),
                                        "Error de conexión",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                apiService.actualizarCategoriaProducto(categoria.getId(), dto)
                        .enqueue(new Callback<CategoriaProductoDto>() {
                            @Override
                            public void onResponse(
                                    @NonNull Call<CategoriaProductoDto> call,
                                    @NonNull Response<CategoriaProductoDto> response) {
                                if (!isAdded()) return;
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(),
                                            "Categoría actualizada",
                                            Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    cargarCategorias();
                                } else {
                                    Toast.makeText(getContext(),
                                            "Error al actualizar categoría",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    @NonNull Call<CategoriaProductoDto> call,
                                    @NonNull Throwable t) {
                                if (!isAdded()) return;
                                Toast.makeText(getContext(),
                                        "Error de conexión",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        dialogView.findViewById(R.id.btnCancelarCategoria)
                .setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    private void mostrarDialogoProducto(@Nullable ProductoDto producto) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_producto_admin, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombreProducto);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionProducto);
        EditText etPrecio = dialogView.findViewById(R.id.etPrecioProducto);
        Spinner spCategoria = dialogView.findViewById(R.id.spCategoriaProducto);

        List<String> nombresCat = new ArrayList<>();
        for (CategoriaProductoDto c : listaCatOriginal) {
            nombresCat.add(c.getNombre());
        }
        if (nombresCat.isEmpty()) {
            nombresCat.add("Sin categoría");
        }

        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                nombresCat
        );
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapterCategorias);

        boolean esEdicion = (producto != null);
        if (esEdicion) {
            etNombre.setText(producto.getNombre());
            etDescripcion.setText(producto.getDescripcion());
            etPrecio.setText(String.valueOf(producto.getPrecio()));

            int index = 0;
            for (int i = 0; i < listaCatOriginal.size(); i++) {
                if (listaCatOriginal.get(i).getId() == producto.getIdCategoria()) {
                    index = i;
                    break;
                }
            }
            spCategoria.setSelection(index);
        }

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btnGuardarProducto).setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            String sPrecio = etPrecio.getText().toString().trim();

            if (nombre.isEmpty() || descripcion.isEmpty() || sPrecio.isEmpty()) {
                Toast.makeText(getContext(),
                        "Todos los campos son obligatorios",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            double precio;
            try {
                precio = Double.parseDouble(sPrecio);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(),
                        "Precio inválido",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int posCat = spCategoria.getSelectedItemPosition();
            int idCategoria = 0;
            if (!listaCatOriginal.isEmpty() && posCat >= 0 && posCat < listaCatOriginal.size()) {
                idCategoria = listaCatOriginal.get(posCat).getId();
            }

            ProductoDto dto = new ProductoDto();
            dto.setNombre(nombre);
            dto.setDescripcion(descripcion);
            dto.setPrecio(precio);
            dto.setIdCategoria(idCategoria);

            if (producto == null) {
                dto.setDisponible(false);
            } else {
                dto.setDisponible(producto.isDisponible());
            }

            if (!esEdicion) {
                apiService.crearProducto(dto).enqueue(new Callback<ProductoDto>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductoDto> call,
                                           @NonNull Response<ProductoDto> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(),
                                    "Producto creado",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            cargarProductos();
                        } else {
                            Toast.makeText(getContext(),
                                    "Error al crear producto",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductoDto> call,
                                          @NonNull Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(),
                                "Error de conexión",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                apiService.actualizarProducto(producto.getId(), dto)
                        .enqueue(new Callback<ProductoDto>() {
                            @Override
                            public void onResponse(@NonNull Call<ProductoDto> call,
                                                   @NonNull Response<ProductoDto> response) {
                                if (!isAdded()) return;
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(),
                                            "Producto actualizado",
                                            Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    cargarProductos();
                                } else {
                                    Toast.makeText(getContext(),
                                            "Error al actualizar producto",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<ProductoDto> call,
                                                  @NonNull Throwable t) {
                                if (!isAdded()) return;
                                Toast.makeText(getContext(),
                                        "Error de conexión",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        dialogView.findViewById(R.id.btnCancelarProducto)
                .setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    private void mostrarDialogoPromocion(@Nullable PromocionDto promocion) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_promocion_admin, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombrePromocion);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionPromocion);
        EditText etFechaInicio = dialogView.findViewById(R.id.etFechaInicioPromocion);
        EditText etFechaFin = dialogView.findViewById(R.id.etFechaFinPromocion);
        EditText etPrecio = dialogView.findViewById(R.id.etPrecioPromocion);

        boolean esEdicion = (promocion != null);
        if (esEdicion) {
            etNombre.setText(promocion.getNombre());
            etDescripcion.setText(promocion.getDescripcion());
            etPrecio.setText(String.valueOf(promocion.getPrecioTotalConDescuento()));

            if (promocion.getFechaInicio() != null) {
                etFechaInicio.setText(promocion.getFechaInicio());
            }
            if (promocion.getFechaFin() != null) {
                etFechaFin.setText(promocion.getFechaFin());
            }
        } else {
            // sugerimos fechas por defecto: hoy y hoy+30 días
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            String hoy = sdf.format(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 30);
            String fin = sdf.format(cal.getTime());
            etFechaInicio.setText(hoy);
            etFechaFin.setText(fin);
        }

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btnGuardarPromocion).setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            String sPrecio = etPrecio.getText().toString().trim();
            String fInicio = etFechaInicio.getText().toString().trim();
            String fFin = etFechaFin.getText().toString().trim();

            if (nombre.isEmpty() || sPrecio.isEmpty() || fInicio.isEmpty() || fFin.isEmpty()) {
                Toast.makeText(getContext(),
                        "Nombre, precio y fechas son obligatorios",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!esFormatoFechaValido(fInicio) || !esFormatoFechaValido(fFin)) {
                Toast.makeText(getContext(),
                        "Formato de fecha inválido. Usa yyyy-MM-dd",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (esFechaFinAntesQueInicio(fInicio, fFin)) {
                Toast.makeText(getContext(),
                        "La fecha de fin no puede ser antes de la fecha de inicio",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            double precio;
            try {
                precio = Double.parseDouble(sPrecio);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(),
                        "Precio inválido",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            PromocionDto dto = new PromocionDto();
            dto.setNombre(nombre);
            dto.setDescripcion(descripcion);
            dto.setFechaInicio(fInicio);
            dto.setFechaFin(fFin);
            dto.setPrecioTotalConDescuento(precio);

            if (!esEdicion) {
                dto.setActivo(false);

                apiService.crearPromocion(dto).enqueue(new Callback<PromocionDto>() {
                    @Override
                    public void onResponse(@NonNull Call<PromocionDto> call,
                                           @NonNull Response<PromocionDto> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(),
                                    "Promoción creada",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            cargarPromociones();
                        } else {
                            Toast.makeText(getContext(),
                                    "Error al crear promoción",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PromocionDto> call,
                                          @NonNull Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(),
                                "Error de conexión",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                dto.setActivo(promocion.isActivo());

                apiService.actualizarPromocion(promocion.getId(), dto)
                        .enqueue(new Callback<PromocionDto>() {
                            @Override
                            public void onResponse(@NonNull Call<PromocionDto> call,
                                                   @NonNull Response<PromocionDto> response) {
                                if (!isAdded()) return;
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(),
                                            "Promoción actualizada",
                                            Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    cargarPromociones();
                                } else {
                                    Toast.makeText(getContext(),
                                            "Error al actualizar promoción",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<PromocionDto> call,
                                                  @NonNull Throwable t) {
                                if (!isAdded()) return;
                                Toast.makeText(getContext(),
                                        "Error de conexión",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        dialogView.findViewById(R.id.btnCancelarPromocion)
                .setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }



    @Override
    public void onIngredienteEdit(@NonNull IngredienteDto ingrediente) {
        mostrarDialogoEditar(ingrediente);
    }

    @Override
    public void onCategoriaEdit(@NonNull CategoriaProductoDto categoria) {
        mostrarDialogoCategoriaProducto(categoria);
    }

    @Override
    public void onProductoEdit(@NonNull ProductoDto producto) {
        mostrarDialogoProducto(producto);
    }

    @Override
    public void onConfigIngredientes(@NonNull ProductoDto producto) {
        configurarIngredientesProducto(producto);
    }

    @Override
    public void onCambiarDisponible(@NonNull ProductoDto producto, boolean nuevoEstado) {
        producto.setDisponible(nuevoEstado);

        ProductoDto cambios = new ProductoDto();
        cambios.setNombre(producto.getNombre());
        cambios.setDescripcion(producto.getDescripcion());
        cambios.setPrecio(producto.getPrecio());
        cambios.setDisponible(nuevoEstado);
        cambios.setIdCategoria(producto.getIdCategoria());

        apiService.actualizarProducto(producto.getId(), cambios)
                .enqueue(new Callback<ProductoDto>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductoDto> call,
                                           @NonNull Response<ProductoDto> response) {
                        if (!isAdded()) return;
                        if (!response.isSuccessful()) {
                            Toast.makeText(getContext(),
                                    "Error al cambiar disponibilidad",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductoDto> call,
                                          @NonNull Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(),
                                "Error de conexión",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onProductoDelete(@NonNull ProductoDto producto) {
        if (!isAdded()) return;

        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar producto")
                .setMessage("¿Seguro que quieres eliminar el producto:\n\n" +
                        (producto.getNombre() != null ? producto.getNombre() : "Sin nombre") + "?")
                .setPositiveButton("Sí, eliminar", (dialogInterface, which) -> {

                    apiService.eliminarProducto(producto.getId())
                            .enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(@NonNull Call<Void> call,
                                                       @NonNull Response<Void> response) {
                                    if (!isAdded()) return;

                                    if (response.isSuccessful()) {
                                        Toast.makeText(
                                                        getContext(),
                                                        "Producto eliminado correctamente",
                                                        Toast.LENGTH_SHORT
                                                )
                                                .show();
                                        cargarProductos();
                                    } else if (response.code() == 409) {
                                        // 409 -> conflicto: alguna regla de negocio lo impide
                                        String detalle = "";
                                        try {
                                            if (response.errorBody() != null) {
                                                detalle = response.errorBody().string();
                                            }
                                        } catch (IOException e) {
                                            detalle = "";
                                        }

                                        if (detalle != null) detalle = detalle.trim();

                                        if ("PROMOCION".equalsIgnoreCase(detalle)) {
                                            Toast.makeText(
                                                            getContext(),
                                                            "No se puede eliminar: el producto está asociado a una promoción.",
                                                            Toast.LENGTH_LONG
                                                    )
                                                    .show();
                                        } else if ("INGREDIENTES".equalsIgnoreCase(detalle)) {
                                            Toast.makeText(
                                                            getContext(),
                                                            "No se puede eliminar: el producto está asociado a uno o más ingredientes.",
                                                            Toast.LENGTH_LONG
                                                    )
                                                    .show();
                                        } else {
                                            Toast.makeText(
                                                            getContext(),
                                                            "No se puede eliminar el producto por una restricción del sistema.",
                                                            Toast.LENGTH_LONG
                                                    )
                                                    .show();
                                        }

                                    } else {
                                        Toast.makeText(
                                                        getContext(),
                                                        "Error al eliminar producto (código " + response.code() + ")",
                                                        Toast.LENGTH_SHORT
                                                )
                                                .show();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Void> call,
                                                      @NonNull Throwable t) {
                                    if (!isAdded()) return;
                                    Toast.makeText(
                                                    getContext(),
                                                    "Error de conexión al eliminar producto",
                                                    Toast.LENGTH_SHORT
                                            )
                                            .show();
                                }
                            });

                })
                .setNegativeButton("Cancelar", (dialogInterface, which) -> dialogInterface.dismiss())
                .show();
    }



    @Override
    public void onPromocionEdit(@NonNull PromocionDto promocion) {
        mostrarDialogoPromocion(promocion);
    }

    @Override
    public void onConfigProductos(@NonNull PromocionDto promocion) {
        configurarProductosPromocion(promocion);
    }

    @Override
    public void onCambiarDisponiblePromocion(@NonNull PromocionDto promo, boolean nuevoEstado) {

        if (nuevoEstado && esFechaFinPasada(promo.getFechaFin())) {
            Toast.makeText(getContext(),
                    "La fecha de fin (" + promo.getFechaFin() + ") ya pasó.\n" +
                            "Modifica la fecha de fin para poder activar la promoción.",
                    Toast.LENGTH_LONG).show();
            cargarPromociones();
            return;
        }

        promo.setActivo(nuevoEstado);

        PromocionDto cambios = new PromocionDto();
        cambios.setNombre(promo.getNombre());
        cambios.setDescripcion(promo.getDescripcion());
        cambios.setFechaInicio(promo.getFechaInicio());
        cambios.setFechaFin(promo.getFechaFin());
        cambios.setPrecioTotalConDescuento(promo.getPrecioTotalConDescuento());
        cambios.setActivo(nuevoEstado);

        apiService.actualizarPromocion(promo.getId(), cambios)
                .enqueue(new Callback<PromocionDto>() {
                    @Override
                    public void onResponse(@NonNull Call<PromocionDto> call,
                                           @NonNull Response<PromocionDto> response) {
                        if (!isAdded()) return;
                        if (!response.isSuccessful()) {
                            Toast.makeText(getContext(),
                                    "Error al cambiar disponibilidad de la promoción",
                                    Toast.LENGTH_SHORT).show();
                            cargarPromociones();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PromocionDto> call,
                                          @NonNull Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(),
                                "Error de conexión",
                                Toast.LENGTH_SHORT).show();
                        cargarPromociones();
                    }
                });
    }

    @Override
    public void onPromocionDelete(@NonNull PromocionDto promo) {
        if (!isAdded()) return;

        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar promoción")
                .setMessage("¿Seguro que quieres eliminar la promoción \"" +
                        promo.getNombre() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    apiService.eliminarPromocion(promo.getId())
                            .enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(@NonNull Call<Void> call,
                                                       @NonNull Response<Void> response) {
                                    if (!isAdded()) return;
                                    if (response.isSuccessful()) {
                                        Toast.makeText(getContext(),
                                                "Promoción eliminada",
                                                Toast.LENGTH_SHORT).show();
                                        cargarPromociones();
                                    } else {
                                        Toast.makeText(getContext(),
                                                "Error al eliminar (código "
                                                        + response.code() + ")",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<Void> call,
                                                      @NonNull Throwable t) {
                                    if (!isAdded()) return;
                                    Toast.makeText(getContext(),
                                            "Error de conexión al eliminar",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void configurarIngredientesProducto(@NonNull ProductoDto producto) {
        apiService.getIngredientes().enqueue(new Callback<List<IngredienteDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<IngredienteDto>> call,
                                   @NonNull Response<List<IngredienteDto>> respIng) {
                if (!isAdded()) return;
                if (!respIng.isSuccessful() || respIng.body() == null) {
                    Toast.makeText(getContext(),
                            "Error al obtener ingredientes", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<IngredienteDto> ingredientes = respIng.body();

                apiService.getProductoIngredientes().enqueue(
                        new Callback<List<ProductoIngredienteDto>>() {
                            @Override
                            public void onResponse(
                                    @NonNull Call<List<ProductoIngredienteDto>> call,
                                    @NonNull Response<List<ProductoIngredienteDto>> respPI) {
                                if (!isAdded()) return;

                                List<ProductoIngredienteDto> relaciones;

                                if (!respPI.isSuccessful()) {
                                    if (respPI.code() == 404) {
                                        relaciones = new ArrayList<>();
                                    } else {
                                        Toast.makeText(getContext(),
                                                "Error al obtener relaciones (código "
                                                        + respPI.code() + ")",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                } else if (respPI.body() == null) {
                                    relaciones = new ArrayList<>();
                                } else {
                                    relaciones = respPI.body();
                                }

                                mostrarDialogConfigIngredientes(producto, ingredientes, relaciones);
                            }

                            @Override
                            public void onFailure(
                                    @NonNull Call<List<ProductoIngredienteDto>> call,
                                    @NonNull Throwable t) {
                                if (!isAdded()) return;
                                Toast.makeText(getContext(),
                                        "Error de conexión (relaciones)",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onFailure(@NonNull Call<List<IngredienteDto>> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(),
                        "Error de conexión (ingredientes)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogConfigIngredientes(
            @NonNull ProductoDto producto,
            @NonNull List<IngredienteDto> ingredientes,
            @NonNull List<ProductoIngredienteDto> relaciones) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(
                R.layout.dialog_config_ingredientes_producto, null);

        RecyclerView rv = dialogView.findViewById(
                R.id.recyclerConfigIngredientesProducto);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        List<ConfigIngredientesProductoAdapter.ItemConfig> items = new ArrayList<>();
        for (IngredienteDto ing : ingredientes) {
            ConfigIngredientesProductoAdapter.ItemConfig ic =
                    new ConfigIngredientesProductoAdapter.ItemConfig();
            ic.idIngrediente = ing.getId();
            ic.nombreIngrediente = ing.getNombre();
            ic.unidad = ing.getUnidadMedida();
            ic.cantidad = 0;
            ic.idProductoIngrediente = 0;
            ic.seleccionado = false;

            for (ProductoIngredienteDto pi : relaciones) {
                if (pi.getIdProducto() == producto.getId()
                        && pi.getIdIngrediente() == ing.getId()) {
                    ic.idProductoIngrediente = pi.getId();
                    ic.cantidad = pi.getCantidadIngrediente();
                    ic.seleccionado = ic.cantidad > 0;
                    break;
                }
            }
            items.add(ic);
        }

        ConfigIngredientesProductoAdapter adapter =
                new ConfigIngredientesProductoAdapter(items);
        rv.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btnGuardarConfigIngredientes)
                .setOnClickListener(v -> {
                    boolean ok = guardarConfigIngredientes(
                            producto,
                            adapter.getItems(),
                            ingredientes
                    );
                    if (ok) {
                        dialog.dismiss();
                    }
                });

        dialogView.findViewById(R.id.btnCancelarConfigIngredientes)
                .setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private boolean guardarConfigIngredientes(
            @NonNull ProductoDto producto,
            @NonNull List<ConfigIngredientesProductoAdapter.ItemConfig> items,
            @NonNull List<IngredienteDto> ingredientes) {

        if (!isAdded()) return false;

        Map<Integer, Double> stockPorIngrediente = new HashMap<>();
        for (IngredienteDto ing : ingredientes) {
            double stock = ing.getStockActual();
            stockPorIngrediente.put(ing.getId(), stock);
        }

        boolean tieneIngredientes = false;

        for (ConfigIngredientesProductoAdapter.ItemConfig ic : items) {

            if (ic.seleccionado && ic.cantidad > 0) {
                tieneIngredientes = true;

                double stockDisponible =
                        stockPorIngrediente.getOrDefault(ic.idIngrediente, 0.0);

                if (ic.cantidad > stockDisponible) {
                    String nombreIng = (ic.nombreIngrediente != null)
                            ? ic.nombreIngrediente
                            : "ingrediente";

                    String msg = "Stock insuficiente de " + nombreIng +
                            "\nStock actual: " + stockDisponible + " " +
                            (ic.unidad != null ? ic.unidad : "") +
                            "\nRequerido por producto: " + ic.cantidad;

                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }

        for (ConfigIngredientesProductoAdapter.ItemConfig ic : items) {

            if (ic.seleccionado && ic.cantidad > 0) {

                ProductoIngredienteDto dto = new ProductoIngredienteDto();
                dto.setCantidadIngrediente(ic.cantidad);
                dto.setIdIngrediente(ic.idIngrediente);
                dto.setIdProducto(producto.getId());

                if (ic.idProductoIngrediente == 0) {
                    apiService.crearProductoIngrediente(dto).enqueue(
                            new Callback<ProductoIngredienteDto>() {
                                @Override
                                public void onResponse(
                                        @NonNull Call<ProductoIngredienteDto> call,
                                        @NonNull Response<ProductoIngredienteDto> response) { }

                                @Override
                                public void onFailure(
                                        @NonNull Call<ProductoIngredienteDto> call,
                                        @NonNull Throwable t) { }
                            });
                } else {
                    apiService.actualizarProductoIngrediente(ic.idProductoIngrediente, dto)
                            .enqueue(new Callback<ProductoIngredienteDto>() {
                                @Override
                                public void onResponse(
                                        @NonNull Call<ProductoIngredienteDto> call,
                                        @NonNull Response<ProductoIngredienteDto> response) { }

                                @Override
                                public void onFailure(
                                        @NonNull Call<ProductoIngredienteDto> call,
                                        @NonNull Throwable t) { }
                            });
                }

            } else if (!ic.seleccionado && ic.idProductoIngrediente != 0) {
                apiService.eliminarProductoIngrediente(ic.idProductoIngrediente)
                        .enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(
                                    @NonNull Call<Void> call,
                                    @NonNull Response<Void> response) { }

                            @Override
                            public void onFailure(
                                    @NonNull Call<Void> call,
                                    @NonNull Throwable t) { }
                        });
            }
        }

        actualizarDisponibilidadSegunIngredientes(producto, tieneIngredientes);

        Toast.makeText(getContext(),
                "Ingredientes del producto actualizados",
                Toast.LENGTH_SHORT).show();

        return true;
    }

    private void actualizarDisponibilidadSegunIngredientes(@NonNull ProductoDto producto,
                                                           boolean tieneIngredientes) {

        producto.setDisponible(tieneIngredientes);

        ProductoDto cambios = new ProductoDto();
        cambios.setNombre(producto.getNombre());
        cambios.setDescripcion(producto.getDescripcion());
        cambios.setPrecio(producto.getPrecio());
        cambios.setDisponible(tieneIngredientes);
        cambios.setIdCategoria(producto.getIdCategoria());

        apiService.actualizarProducto(producto.getId(), cambios)
                .enqueue(new Callback<ProductoDto>() {
                    @Override
                    public void onResponse(@NonNull Call<ProductoDto> call,
                                           @NonNull Response<ProductoDto> response) {
                        if (!isAdded()) return;

                        if (response.isSuccessful()) {
                            cargarProductos();
                        } else {
                            Toast.makeText(getContext(),
                                    "Error al actualizar disponibilidad",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ProductoDto> call,
                                          @NonNull Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(),
                                "Error de conexión al actualizar disponibilidad",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void configurarProductosPromocion(@NonNull PromocionDto promo) {
        apiService.getProductos().enqueue(new Callback<List<ProductoDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProductoDto>> call,
                                   @NonNull Response<List<ProductoDto>> respProd) {
                if (!isAdded()) return;
                if (!respProd.isSuccessful() || respProd.body() == null) {
                    Toast.makeText(getContext(),
                            "Error al obtener productos",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                List<ProductoDto> productos = respProd.body();

                apiService.getPromocionProductos().enqueue(
                        new Callback<List<PromocionProductoDto>>() {
                            @Override
                            public void onResponse(
                                    @NonNull Call<List<PromocionProductoDto>> call,
                                    @NonNull Response<List<PromocionProductoDto>> respPP) {
                                if (!isAdded()) return;

                                List<PromocionProductoDto> relaciones;
                                if (!respPP.isSuccessful()) {
                                    if (respPP.code() == 404) {
                                        relaciones = new ArrayList<>();
                                    } else {
                                        Toast.makeText(getContext(),
                                                "Error al obtener productos de la promoción",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                } else if (respPP.body() == null) {
                                    relaciones = new ArrayList<>();
                                } else {
                                    relaciones = respPP.body();
                                }

                                mostrarDialogConfigProductosPromocion(promo, productos, relaciones);
                            }

                            @Override
                            public void onFailure(
                                    @NonNull Call<List<PromocionProductoDto>> call,
                                    @NonNull Throwable t) {
                                if (!isAdded()) return;
                                Toast.makeText(getContext(),
                                        "Error de conexión (promoción-producto)",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
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

    private void mostrarDialogConfigProductosPromocion(
            @NonNull PromocionDto promo,
            @NonNull List<ProductoDto> productos,
            @NonNull List<PromocionProductoDto> relaciones) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(
                R.layout.dialog_config_productos_promocion, null);

        RecyclerView rv = dialogView.findViewById(
                R.id.recyclerConfigProductosPromocion);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        List<ConfigProductosPromocionAdapter.ItemConfig> items = new ArrayList<>();
        for (ProductoDto p : productos) {
            ConfigProductosPromocionAdapter.ItemConfig ic =
                    new ConfigProductosPromocionAdapter.ItemConfig();
            ic.idProducto = p.getId();
            ic.nombreProducto = p.getNombre();
            ic.cantidad = 0;
            ic.idPromocionProducto = 0;
            ic.seleccionado = false;

            for (PromocionProductoDto pp : relaciones) {
                if (pp.getIdPromocion() == promo.getId()
                        && pp.getIdProducto() == p.getId()) {
                    ic.idPromocionProducto = pp.getId();
                    ic.cantidad = (int) pp.getCantidadProducto();
                    ic.seleccionado = ic.cantidad > 0;
                    break;
                }
            }
            items.add(ic);
        }

        ConfigProductosPromocionAdapter adapter =
                new ConfigProductosPromocionAdapter(items);
        rv.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        dialogView.findViewById(R.id.btnGuardarConfigProductosPromo)
                .setOnClickListener(v -> {
                    guardarConfigProductosPromocion(promo, adapter.getItems());
                    dialog.dismiss();
                });

        dialogView.findViewById(R.id.btnCancelarConfigProductosPromo)
                .setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void guardarConfigProductosPromocion(
            @NonNull PromocionDto promo,
            @NonNull List<ConfigProductosPromocionAdapter.ItemConfig> items) {

        boolean tieneProductos = false;

        for (ConfigProductosPromocionAdapter.ItemConfig ic : items) {
            if (ic.seleccionado && ic.cantidad > 0) {
                tieneProductos = true;

                PromocionProductoDto dto = new PromocionProductoDto();
                dto.setIdPromocion(promo.getId());
                dto.setIdProducto(ic.idProducto);
                dto.setCantidadProducto(ic.cantidad);

                if (ic.idPromocionProducto == 0) {
                    apiService.crearPromocionProducto(dto).enqueue(
                            new Callback<PromocionProductoDto>() {
                                @Override
                                public void onResponse(
                                        @NonNull Call<PromocionProductoDto> call,
                                        @NonNull Response<PromocionProductoDto> response) { }

                                @Override
                                public void onFailure(
                                        @NonNull Call<PromocionProductoDto> call,
                                        @NonNull Throwable t) { }
                            });
                } else {
                    apiService.actualizarPromocionProducto(ic.idPromocionProducto, dto)
                            .enqueue(new Callback<PromocionProductoDto>() {
                                @Override
                                public void onResponse(
                                        @NonNull Call<PromocionProductoDto> call,
                                        @NonNull Response<PromocionProductoDto> response) { }

                                @Override
                                public void onFailure(
                                        @NonNull Call<PromocionProductoDto> call,
                                        @NonNull Throwable t) { }
                            });
                }

            } else if (!ic.seleccionado && ic.idPromocionProducto != 0) {
                apiService.eliminarPromocionProducto(ic.idPromocionProducto)
                        .enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(
                                    @NonNull Call<Void> call,
                                    @NonNull Response<Void> response) { }

                            @Override
                            public void onFailure(
                                    @NonNull Call<Void> call,
                                    @NonNull Throwable t) { }
                        });
            }
        }

        actualizarDisponibilidadSegunProductosPromo(promo, tieneProductos);

        Toast.makeText(getContext(),
                "Productos de la promoción actualizados",
                Toast.LENGTH_SHORT).show();
    }

    private void actualizarDisponibilidadSegunProductosPromo(@NonNull PromocionDto promo,
                                                             boolean tieneProductos) {
        promo.setActivo(tieneProductos);

        PromocionDto cambios = new PromocionDto();
        cambios.setNombre(promo.getNombre());
        cambios.setDescripcion(promo.getDescripcion());
        cambios.setFechaInicio(promo.getFechaInicio());
        cambios.setFechaFin(promo.getFechaFin());
        cambios.setPrecioTotalConDescuento(promo.getPrecioTotalConDescuento());
        cambios.setActivo(tieneProductos);

        apiService.actualizarPromocion(promo.getId(), cambios)
                .enqueue(new Callback<PromocionDto>() {
                    @Override
                    public void onResponse(@NonNull Call<PromocionDto> call,
                                           @NonNull Response<PromocionDto> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful()) {
                            cargarPromociones();
                        } else {
                            Toast.makeText(getContext(),
                                    "Error al actualizar disponibilidad de la promo",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PromocionDto> call,
                                          @NonNull Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(getContext(),
                                "Error de conexión al actualizar promo",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private boolean esFormatoFechaValido(@NonNull String fecha) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setLenient(false);
            sdf.parse(fecha);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean esFechaFinAntesQueInicio(@NonNull String fInicio, @NonNull String fFin) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setLenient(false);
            java.util.Date dInicio = sdf.parse(fInicio);
            java.util.Date dFin = sdf.parse(fFin);
            if (dInicio == null || dFin == null) return false;
            return dFin.before(dInicio);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean esFechaFinPasada(@Nullable String fFin) {
        if (fFin == null || fFin.trim().isEmpty()) return false;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setLenient(false);
            java.util.Date dFin = sdf.parse(fFin);
            if (dFin == null) return false;

            Calendar hoy = Calendar.getInstance();
            hoy.set(Calendar.HOUR_OF_DAY, 0);
            hoy.set(Calendar.MINUTE, 0);
            hoy.set(Calendar.SECOND, 0);
            hoy.set(Calendar.MILLISECOND, 0);

            return dFin.before(hoy.getTime());
        } catch (Exception e) {
            return false;
        }
    }

}
