package com.example.login.administrador.inventarioa;

import android.app.AlertDialog;
import android.os.Bundle;
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
import com.example.login.network.model.ProductoDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductosAdminFragment extends Fragment
        implements ProductosAdminAdapter.OnProductoClickListener {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAgregar;

    private ProductosAdminAdapter adapter;
    private final List<ProductoDto> listaProductos = new ArrayList<>();
    private final List<CategoriaProductoDto> listaCategorias = new ArrayList<>();

    private SpringApiService apiService;

    public ProductosAdminFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_productos_admin, container, false);

        recyclerView = view.findViewById(R.id.recyclerProductos);
        fabAgregar = view.findViewById(R.id.fabAgregarProducto);

        apiService = ApiClient.getInstance().create(SpringApiService.class);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductosAdminAdapter(listaProductos, this);
        recyclerView.setAdapter(adapter);

        fabAgregar.setOnClickListener(v -> mostrarDialogoAgregarProducto());

        cargarCategoriasYProductos();

        return view;
    }

    private void cargarCategoriasYProductos() {
        // Primero categorías (para el spinner)
        apiService.getCategoriasProducto().enqueue(new Callback<List<CategoriaProductoDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoriaProductoDto>> call,
                                   @NonNull Response<List<CategoriaProductoDto>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    listaCategorias.clear();
                    listaCategorias.addAll(response.body());
                }

                cargarProductos();
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoriaProductoDto>> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(),
                        "Error al obtener categorías",
                        Toast.LENGTH_SHORT).show();
                cargarProductos();
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

                listaProductos.clear();
                listaProductos.addAll(response.body());
                adapter.notifyDataSetChanged();
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

    private void mostrarDialogoAgregarProducto() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_producto_admin, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombreProducto);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionProducto);
        EditText etPrecio = dialogView.findViewById(R.id.etPrecioProducto);
        Spinner spCategoria = dialogView.findViewById(R.id.spCategoriaProducto);

        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                obtenerNombresCategorias()
        );
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapterCategorias);

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
            int idCategoria = (posCat >= 0 && posCat < listaCategorias.size())
                    ? listaCategorias.get(posCat).getId()
                    : 0;

            ProductoDto nuevo = new ProductoDto();
            nuevo.setNombre(nombre);
            nuevo.setDescripcion(descripcion);
            nuevo.setPrecio(precio);
            nuevo.setDisponible(true);
            nuevo.setIdCategoria(idCategoria);

            apiService.crearProducto(nuevo).enqueue(new Callback<ProductoDto>() {
                @Override
                public void onResponse(@NonNull Call<ProductoDto> call,
                                       @NonNull Response<ProductoDto> response) {
                    if (!isAdded()) return;

                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(),
                                "Producto creado",
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        cargarCategoriasYProductos();
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

        });

        dialogView.findViewById(R.id.btnCancelarProducto)
                .setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void mostrarDialogoEditarProducto(@NonNull ProductoDto producto) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_producto_admin, null);

        EditText etNombre = dialogView.findViewById(R.id.etNombreProducto);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionProducto);
        EditText etPrecio = dialogView.findViewById(R.id.etPrecioProducto);
        Spinner spCategoria = dialogView.findViewById(R.id.spCategoriaProducto);

        etNombre.setText(producto.getNombre());
        etDescripcion.setText(producto.getDescripcion());
        etPrecio.setText(String.valueOf(producto.getPrecio()));

        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                obtenerNombresCategorias()
        );
        adapterCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(adapterCategorias);

        int index = buscarIndiceCategoria(producto.getIdCategoria());
        if (index >= 0) spCategoria.setSelection(index);

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
            int idCategoria = (posCat >= 0 && posCat < listaCategorias.size())
                    ? listaCategorias.get(posCat).getId()
                    : 0;

            ProductoDto cambios = new ProductoDto();
            cambios.setNombre(nombre);
            cambios.setDescripcion(descripcion);
            cambios.setPrecio(precio);
            cambios.setDisponible(producto.isDisponible());
            cambios.setIdCategoria(idCategoria);

            apiService.actualizarProducto(producto.getId(), cambios)
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
                                cargarCategoriasYProductos();
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
        });

        dialogView.findViewById(R.id.btnCancelarProducto)
                .setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private List<String> obtenerNombresCategorias() {
        List<String> nombres = new ArrayList<>();
        for (CategoriaProductoDto c : listaCategorias) {
            nombres.add(c.getNombre());
        }
        if (nombres.isEmpty()) {
            nombres.add("Sin categoría");
        }
        return nombres;
    }

    private int buscarIndiceCategoria(int idCategoria) {
        for (int i = 0; i < listaCategorias.size(); i++) {
            if (listaCategorias.get(i).getId() == idCategoria) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onProductoEdit(@NonNull ProductoDto producto) {
        mostrarDialogoEditarProducto(producto);
    }

    @Override
    public void onConfigIngredientes(@NonNull ProductoDto producto) {
        Toast.makeText(getContext(),
                "Configurar ingredientes de: " + producto.getNombre(),
                Toast.LENGTH_SHORT).show();
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
                .setMessage("¿Seguro que quieres eliminar el producto:\n\n"
                        + (producto.getNombre() != null ? producto.getNombre() : "Sin nombre") + "?")
                .setPositiveButton("Sí, eliminar", (dialogInterface, i) -> {

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
                                        cargarCategoriasYProductos();
                                    } else if (response.code() == 409) {
                                        String mensaje = "No se puede eliminar el producto.";
                                        try {
                                            if (response.errorBody() != null) {
                                                mensaje = response.errorBody().string();
                                            }
                                        } catch (Exception ignored) { }

                                        Toast.makeText(
                                                        getContext(),
                                                        mensaje,
                                                        Toast.LENGTH_LONG
                                                )
                                                .show();
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
                .setNegativeButton("Cancelar", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }
}
