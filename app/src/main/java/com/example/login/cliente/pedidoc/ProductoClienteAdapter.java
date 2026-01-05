package com.example.login.cliente.pedidoc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;
import com.example.login.network.model.ProductoDto;

import java.util.List;

public class ProductoClienteAdapter
        extends RecyclerView.Adapter<ProductoClienteAdapter.ProdViewHolder> {

    public interface OnProductoClienteListener {
        void onAgregarProducto(@NonNull ProductoDto producto);
    }

    private final List<ProductoDto> lista;
    private final OnProductoClienteListener listener;

    public ProductoClienteAdapter(List<ProductoDto> lista,
                                  OnProductoClienteListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_cliente, parent, false);
        return new ProdViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProdViewHolder holder, int position) {
        ProductoDto p = lista.get(position);

        holder.tvNombre.setText(p.getNombre());
        holder.tvDescripcion.setText(
                p.getDescripcion() != null ? p.getDescripcion() : ""
        );
        holder.tvPrecio.setText("$ " + p.getPrecio());

        holder.btnAgregar.setOnClickListener(v -> {
            if (listener != null) listener.onAgregarProducto(p);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ProdViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvPrecio;
        Button btnAgregar;

        ProdViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreProductoCli);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionProductoCli);
            tvPrecio = itemView.findViewById(R.id.tvPrecioProductoCli);
            btnAgregar = itemView.findViewById(R.id.btnAgregarProductoCli);
        }
    }
}
