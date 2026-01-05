package com.example.login.cliente.pedidoc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;
import com.example.login.network.model.PromocionDto;

import java.util.List;

public class PromocionClienteAdapter
        extends RecyclerView.Adapter<PromocionClienteAdapter.PromoViewHolder> {

    public interface OnPromocionClienteListener {
        void onAgregarPromocion(@NonNull PromocionDto promocion);
    }

    private final List<PromocionDto> lista;
    private final OnPromocionClienteListener listener;

    public PromocionClienteAdapter(List<PromocionDto> lista,
                                   OnPromocionClienteListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PromoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_promocion_cliente, parent, false);
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

        holder.tvFechas.setText(
                "Del " + promo.getFechaInicio() + " al " + promo.getFechaFin()
        );

        holder.btnAgregar.setOnClickListener(v -> {
            if (listener != null) listener.onAgregarPromocion(promo);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class PromoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDescripcion, tvPrecio, tvFechas;
        Button btnAgregar;

        PromoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombrePromoCli);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionPromoCli);
            tvPrecio = itemView.findViewById(R.id.tvPrecioPromoCli);
            tvFechas = itemView.findViewById(R.id.tvFechasPromoCli);
            btnAgregar = itemView.findViewById(R.id.btnAgregarPromoCli);
        }
    }
}
