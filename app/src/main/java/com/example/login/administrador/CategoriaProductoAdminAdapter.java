// com/example/login/administrador/CategoriaProductoAdminAdapter.java
package com.example.login.administrador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;
import com.example.login.network.model.CategoriaProductoDto;

import java.util.List;

public class CategoriaProductoAdminAdapter
        extends RecyclerView.Adapter<CategoriaProductoAdminAdapter.ViewHolder> {

    public interface OnCategoriaClickListener {
        void onCategoriaEdit(@NonNull CategoriaProductoDto categoria);
    }

    private final List<CategoriaProductoDto> datos;
    private final OnCategoriaClickListener listener;

    public CategoriaProductoAdminAdapter(List<CategoriaProductoDto> datos,
                                         OnCategoriaClickListener listener) {
        this.datos = datos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_categoria_producto_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        CategoriaProductoDto cat = datos.get(position);

        h.tvNombre.setText(cat.getNombre());
        h.tvId.setText("ID: " + cat.getId());

        h.btnEditar.setOnClickListener(v -> {
            if (listener != null) listener.onCategoriaEdit(cat);
        });
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvId;
        ImageButton btnEditar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreCategoria);
            tvId = itemView.findViewById(R.id.tvIdCategoria);
            btnEditar = itemView.findViewById(R.id.btnEditarCategoria);
        }
    }
}
