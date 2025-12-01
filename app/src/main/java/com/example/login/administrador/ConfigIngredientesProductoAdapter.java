// app/src/main/java/com/example/login/administrador/ConfigIngredientesProductoAdapter.java
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

public class ConfigIngredientesProductoAdapter
        extends RecyclerView.Adapter<ConfigIngredientesProductoAdapter.ViewHolder> {

    public static class ItemConfig {
        public int idProductoIngrediente;  // 0 si es nuevo
        public int idIngrediente;
        public String nombreIngrediente;
        public String unidad;
        public double cantidad;            // 0 = no usar
        public boolean seleccionado;
    }

    private final List<ItemConfig> datos;

    public ConfigIngredientesProductoAdapter(List<ItemConfig> datos) {
        this.datos = datos;
    }

    public List<ItemConfig> getItems() {
        return datos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_config_ingrediente_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        ItemConfig item = datos.get(position);

        h.cbSeleccion.setOnCheckedChangeListener(null);
        h.etCantidad.removeTextChangedListener(h.watcher);

        h.cbSeleccion.setChecked(item.seleccionado);
        h.tvNombre.setText(item.nombreIngrediente);
        h.tvUnidad.setText(item.unidad != null ? item.unidad : "");
        h.etCantidad.setText(item.cantidad > 0 ? String.valueOf(item.cantidad) : "");

        h.cbSeleccion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.seleccionado = isChecked;
            if (!isChecked) {
                item.cantidad = 0;
            }
        });

        h.watcher.setItem(item);
        h.etCantidad.addTextChangedListener(h.watcher);
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox cbSeleccion;
        TextView tvNombre, tvUnidad;
        EditText etCantidad;
        CantidadWatcher watcher;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSeleccion = itemView.findViewById(R.id.cbIngredienteProducto);
            tvNombre = itemView.findViewById(R.id.tvNombreIngConfig);
            tvUnidad = itemView.findViewById(R.id.tvUnidadIngConfig);
            etCantidad = itemView.findViewById(R.id.etCantidadIngConfig);
            watcher = new CantidadWatcher();
        }
    }

    private static class CantidadWatcher implements TextWatcher {
        private ItemConfig item;

        public void setItem(ItemConfig item) {
            this.item = item;
        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            if (item == null) return;
            String txt = s.toString().trim();
            if (txt.isEmpty()) {
                item.cantidad = 0;
            } else {
                try {
                    item.cantidad = Double.parseDouble(txt);
                    item.seleccionado = item.cantidad > 0;
                } catch (NumberFormatException e) {
                    item.cantidad = 0;
                }
            }
        }
    }
}
