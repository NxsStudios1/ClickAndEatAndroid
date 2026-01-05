package com.example.login.administrador.inventarioa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;
import com.example.login.network.model.IngredienteDto;

import java.util.List;

public class IngredientesAdminAdapter
        extends RecyclerView.Adapter<IngredientesAdminAdapter.ViewHolder> {

    public interface OnIngredienteClickListener {
        void onIngredienteEdit(@NonNull IngredienteDto ingrediente);
    }

    private final List<IngredienteDto> datos;
    private final OnIngredienteClickListener listener;

    public IngredientesAdminAdapter(List<IngredienteDto> datos,
                                    OnIngredienteClickListener listener) {
        this.datos = datos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingrediente_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        IngredienteDto ing = datos.get(position);

        h.tvNombre.setText(ing.getNombre());
        h.tvDescripcion.setText(ing.getDescripcion());

        h.tvDetalleCantidad.setText(
                "Porción: " + ing.getCantidadPorcion() + " " + ing.getUnidadMedida()
        );

        h.tvDetalleStock.setText(
                "Stock: " + ing.getStockActual() + "  |  $" + ing.getPrecioUnitario()
        );

        h.btnEditar.setOnClickListener(v -> {
            if (listener != null) listener.onIngredienteEdit(ing);
        });
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvDetalleCantidad, tvDetalleStock;
        ImageButton btnEditar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreIngrediente);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionIngrediente);
            tvDetalleCantidad = itemView.findViewById(R.id.tvDetalleCantidad);
            tvDetalleStock = itemView.findViewById(R.id.tvDetalleStockPrecio);
            btnEditar = itemView.findViewById(R.id.btnEditarIngrediente);
        }
    }
}
