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
import com.example.login.network.model.PromocionDto;

import java.util.List;

public class PromocionesAdminAdapter
        extends RecyclerView.Adapter<PromocionesAdminAdapter.PromoViewHolder> {

    public interface OnPromocionClickListener {
        void onPromocionEdit(@NonNull PromocionDto promocion);
        void onConfigProductos(@NonNull PromocionDto promocion);
        void onCambiarDisponiblePromocion(@NonNull PromocionDto promocion, boolean nuevoEstado);
        void onPromocionDelete(@NonNull PromocionDto promocion);   // NUEVO
    }


    private final List<PromocionDto> lista;
    private final OnPromocionClickListener listener;

    public PromocionesAdminAdapter(List<PromocionDto> lista,
                                   OnPromocionClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PromoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_promocion_admin, parent, false);
        return new PromoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PromoViewHolder holder, int position) {
        PromocionDto promo = lista.get(position);

        holder.tvNombre.setText(promo.getNombre());
        holder.tvDescripcion.setText(
                promo.getDescripcion() != null ? promo.getDescripcion() : ""
        );
        holder.tvPrecio.setText("$ " + promo.getPrecioTotalConDescuento());
        holder.tvId.setText("ID: " + promo.getId());

        holder.tvFechaInicio.setText("Inicio: " + promo.getFechaInicio());
        holder.tvFechaFin.setText("Fin: " + promo.getFechaFin());

        holder.swDisponible.setOnCheckedChangeListener(null);
        holder.swDisponible.setChecked(promo.isActivo());
        holder.swDisponible.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onCambiarDisponiblePromocion(promo, isChecked);
            }
        });

        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) listener.onPromocionEdit(promo);
        });

        holder.btnConfig.setOnClickListener(v -> {
            if (listener != null) listener.onConfigProductos(promo);
        });

        // NUEVO: eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) listener.onPromocionDelete(promo);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class PromoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvPrecio, tvId;
        TextView tvFechaInicio, tvFechaFin;
        Switch swDisponible;
        ImageButton btnEditar, btnConfig, btnEliminar; // NUEVO

        PromoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombrePromocion);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionPromocion);
            tvPrecio = itemView.findViewById(R.id.tvPrecioPromocion);
            tvId = itemView.findViewById(R.id.tvIdPromocion);
            tvFechaInicio = itemView.findViewById(R.id.tvFechaInicioPromocion);
            tvFechaFin = itemView.findViewById(R.id.tvFechaFinPromocion);
            swDisponible = itemView.findViewById(R.id.swDisponiblePromocion);
            btnConfig = itemView.findViewById(R.id.btnConfigProductosPromo);
            btnEditar = itemView.findViewById(R.id.btnEditarPromocion);
            btnEliminar = itemView.findViewById(R.id.btnEliminarPromocion); // NUEVO
        }
    }
}
