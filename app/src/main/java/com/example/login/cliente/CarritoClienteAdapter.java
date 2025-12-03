// com/example/login/cliente/CarritoClienteAdapter.java
package com.example.login.cliente;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;

import java.util.List;

public class CarritoClienteAdapter
        extends RecyclerView.Adapter<CarritoClienteAdapter.CarritoViewHolder> {

    public interface OnCarritoListener {
        void onIncrementar(@NonNull ItemCarrito item);
        void onDecrementar(@NonNull ItemCarrito item);
        void onEliminar(@NonNull ItemCarrito item);
    }

    public static class ItemCarrito {
        public enum Tipo { PRODUCTO, PROMOCION }

        public Tipo tipo;
        public int idReferencia;      // idProducto o idPromocion
        public String nombre;
        public double precioUnitario;
        public int cantidad;
    }

    private final List<ItemCarrito> items;
    private final OnCarritoListener listener;

    public CarritoClienteAdapter(List<ItemCarrito> items,
                                 OnCarritoListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public List<ItemCarrito> getItems() {
        return items;
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_carrito_cliente, parent, false);
        return new CarritoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        ItemCarrito item = items.get(position);

        String etiquetaTipo = item.tipo == ItemCarrito.Tipo.PROMOCION ? "(PROMO)" : "(PROD)";
        holder.tvNombre.setText(etiquetaTipo + " " + item.nombre);
        holder.tvCantidad.setText("x" + item.cantidad);
        holder.tvPrecioUnitario.setText("$ " + item.precioUnitario);
        holder.tvSubtotal.setText("$ " + (item.precioUnitario * item.cantidad));

        holder.btnMas.setOnClickListener(v -> {
            if (listener != null) listener.onIncrementar(item);
        });

        holder.btnMenos.setOnClickListener(v -> {
            if (listener != null) listener.onDecrementar(item);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) listener.onEliminar(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CarritoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCantidad, tvPrecioUnitario, tvSubtotal;
        ImageButton btnMas, btnMenos, btnEliminar;

        CarritoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreCarrito);
            tvCantidad = itemView.findViewById(R.id.tvCantidadCarrito);
            tvPrecioUnitario = itemView.findViewById(R.id.tvPrecioUnitarioCarrito);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotalCarrito);
            btnMas = itemView.findViewById(R.id.btnMasCarrito);
            btnMenos = itemView.findViewById(R.id.btnMenosCarrito);
            btnEliminar = itemView.findViewById(R.id.btnEliminarCarrito);
        }
    }
}
