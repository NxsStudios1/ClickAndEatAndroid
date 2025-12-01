// com/example/login/administrador/ConfigProductosPromocionAdapter.java
package com.example.login.administrador;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;

import java.util.List;

public class ConfigProductosPromocionAdapter
        extends RecyclerView.Adapter<ConfigProductosPromocionAdapter.ItemViewHolder> {

    public static class ItemConfig {
        public int idProducto;
        public String nombreProducto;
        public int idPromocionProducto; // 0 si es nuevo
        public int cantidad;
        public boolean seleccionado;
    }

    private final List<ItemConfig> items;

    public ConfigProductosPromocionAdapter(List<ItemConfig> items) {
        this.items = items;
    }

    public List<ItemConfig> getItems() {
        return items;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_config_producto_promocion, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemConfig item = items.get(position);

        holder.check.setOnCheckedChangeListener(null);
        holder.check.setChecked(item.seleccionado);
        holder.tvNombre.setText(item.nombreProducto != null ? item.nombreProducto : "");
        holder.tvId.setText("ID: " + item.idProducto);
        holder.etCantidad.setText(item.cantidad > 0 ? String.valueOf(item.cantidad) : "");

        holder.check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.seleccionado = isChecked;
        });

        holder.etCantidad.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                String txt = s.toString().trim();
                if (txt.isEmpty()) {
                    item.cantidad = 0;
                } else {
                    try {
                        item.cantidad = Integer.parseInt(txt);
                    } catch (NumberFormatException e) {
                        item.cantidad = 0;
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        CheckBox check;
        TextView tvNombre, tvId;
        EditText etCantidad;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            check = itemView.findViewById(R.id.checkProductoPromo);
            tvNombre = itemView.findViewById(R.id.tvNombreProductoPromo);
            tvId = itemView.findViewById(R.id.tvIdProductoPromo);
            etCantidad = itemView.findViewById(R.id.etCantidadProductoPromo);
        }
    }
}
