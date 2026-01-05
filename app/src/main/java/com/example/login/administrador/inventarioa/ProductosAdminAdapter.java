package com.example.login.administrador.inventarioa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;
import com.example.login.network.model.CategoriaProductoDto;
import com.example.login.network.model.ProductoDto;

import java.util.ArrayList;
import java.util.List;

public class ProductosAdminAdapter
        extends RecyclerView.Adapter<ProductosAdminAdapter.ViewHolder> {

    public interface OnProductoClickListener {
        void onProductoEdit(@NonNull ProductoDto producto);
        void onConfigIngredientes(@NonNull ProductoDto producto);
        void onCambiarDisponible(@NonNull ProductoDto producto, boolean nuevoEstado);
        void onProductoDelete(@NonNull ProductoDto producto);   // <-- ya lo tenías
    }

    private final List<ProductoDto> productos;
    private final OnProductoClickListener listener;

    private List<CategoriaProductoDto> categorias = new ArrayList<>();

    public ProductosAdminAdapter(List<ProductoDto> productos,
                                 OnProductoClickListener listener) {
        this.productos = productos;
        this.listener = listener;
    }

    // la usaremos desde el Fragment para pasar las categorías
    public void setCategorias(List<CategoriaProductoDto> categorias) {
        this.categorias = (categorias != null) ? categorias : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        ProductoDto p = productos.get(position);

        h.tvNombre.setText(p.getNombre() != null ? p.getNombre() : "Sin nombre");
        h.tvDescripcion.setText(p.getDescripcion() != null ? p.getDescripcion() : "");

        String precioTexto = "$" + p.getPrecio();
        h.tvPrecio.setText(precioTexto);

        String nombreCat = obtenerNombreCategoria(p.getIdCategoria());
        h.tvCategoria.setText("Categoría: " + nombreCat);

        h.switchDisponible.setOnCheckedChangeListener(null);
        h.switchDisponible.setChecked(p.isDisponible());

        h.switchDisponible.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onCambiarDisponible(p, isChecked);
            }
        });

        h.btnEditar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductoEdit(p);
            }
        });

        h.btnIngredientes.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfigIngredientes(p);
            }
        });
        h.btnEliminar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductoDelete(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    private String obtenerNombreCategoria(int idCategoria) {
        if (categorias == null || categorias.isEmpty() || idCategoria == 0) {
            return "Sin categoría";
        }
        for (CategoriaProductoDto c : categorias) {
            if (c.getId() == idCategoria) {
                return c.getNombre() != null ? c.getNombre() : "Sin nombre";
            }
        }
        return "Sin categoría";
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvDescripcion, tvPrecio, tvCategoria;
        Switch switchDisponible;
        ImageButton btnEditar, btnIngredientes, btnEliminar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionProducto);
            tvPrecio = itemView.findViewById(R.id.tvPrecioProducto);
            tvCategoria = itemView.findViewById(R.id.tvCategoriaProducto);

            switchDisponible = itemView.findViewById(R.id.switchDisponibleProducto);
            btnEditar = itemView.findViewById(R.id.btnEditarProducto);
            btnIngredientes = itemView.findViewById(R.id.btnConfigIngredientesProducto);
            btnEliminar = itemView.findViewById(R.id.btnEliminarProducto); // <-- NUEVO
        }
    }
}
