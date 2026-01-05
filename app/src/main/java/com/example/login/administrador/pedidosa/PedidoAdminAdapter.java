package com.example.login.administrador.pedidosa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;
import com.example.login.network.model.DetallePedidoDto;
import com.example.login.network.model.PedidoDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedidoAdminAdapter
        extends RecyclerView.Adapter<PedidoAdminAdapter.PedidoViewHolder> {

    public interface OnEstadoChangeListener {
        void onCambiarEstado(@NonNull PedidoDto pedido, int nuevoEstado);
    }

    public static class PedidoConDetalles {
        public PedidoDto pedido;
        public List<DetallePedidoDto> detalles;

        public PedidoConDetalles(PedidoDto pedido, List<DetallePedidoDto> detalles) {
            this.pedido = pedido;
            this.detalles = detalles;
        }
    }

    private final List<PedidoConDetalles> lista;
    private final OnEstadoChangeListener listener;

    private Map<Integer, String> nombresProductos = new HashMap<>();
    private Map<Integer, String> nombresPromos = new HashMap<>();

    private final String[] estadosLabels = {
            "Pendiente", "En proceso", "Terminado", "Pagado", "Cancelado"
    };

    public PedidoAdminAdapter(List<PedidoConDetalles> lista,
                              OnEstadoChangeListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    public void setNombresProductos(Map<Integer, String> map) {
        this.nombresProductos = map != null ? map : new HashMap<>();
        notifyDataSetChanged();
    }

    public void setNombresPromos(Map<Integer, String> map) {
        this.nombresPromos = map != null ? map : new HashMap<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido_admin, parent, false);
        return new PedidoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        PedidoConDetalles data = lista.get(position);
        PedidoDto p = data.pedido;

        holder.tvTicket.setText("Ticket: " + p.getNumeroTicket());

        String nombreCliente = p.getNombreCliente();
        if (nombreCliente != null && !nombreCliente.trim().isEmpty()) {
            holder.tvCliente.setText("Cliente: " + nombreCliente);
        } else {
            holder.tvCliente.setText("Cliente: " + p.getIdCliente());
        }

        holder.tvFecha.setText("Fecha: " + p.getFechaPedido());
        holder.tvTotal.setText(String.format("Total: $ %.2f", p.getTotal()));

        String obs = p.getObservaciones();
        if (obs == null || obs.trim().isEmpty()) {
            holder.tvObs.setText("Observaciones: Sin observaciones");
        } else {
            holder.tvObs.setText("Observaciones: " + obs);
        }

        ArrayAdapter<String> adapterEstados =
                new ArrayAdapter<>(holder.itemView.getContext(),
                        android.R.layout.simple_spinner_item,
                        estadosLabels);
        adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spEstado.setAdapter(adapterEstados);

        int idx = p.getEstado() >= 1 && p.getEstado() <= 5 ? p.getEstado() - 1 : 0;

        holder.spEstado.setOnItemSelectedListener(null);
        holder.spEstado.setSelection(idx);

        holder.spEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean first = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (first) {
                    first = false;
                    return;
                }
                int nuevoEstado = position + 1;
                if (nuevoEstado != p.getEstado() && listener != null) {
                    listener.onCambiarEstado(p, nuevoEstado);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        StringBuilder sb = new StringBuilder();
        if (data.detalles == null || data.detalles.isEmpty()) {
            sb.append("Sin productos/promociones registrados.");
        } else {
            for (DetallePedidoDto d : data.detalles) {
                String etiqueta;
                String nombreItem;

                if (d.getTipoItem() == 2) { // PROMOCION
                    etiqueta = "(PROMO)";
                    int idPromo = d.getIdPromocion() != null ? d.getIdPromocion() : 0;
                    nombreItem = nombresPromos.getOrDefault(
                            idPromo,
                            "Promo #" + idPromo
                    );
                } else { // PRODUCTO
                    etiqueta = "(PROD)";
                    int idProd = d.getIdProducto() != null ? d.getIdProducto() : 0;
                    nombreItem = nombresProductos.getOrDefault(
                            idProd,
                            "Producto #" + idProd
                    );
                }

                sb.append("• ")
                        .append(etiqueta).append(" ")
                        .append(nombreItem)
                        .append("   x").append(d.getCantidad())
                        .append("\n");
            }
        }
        holder.tvItems.setText(sb.toString().trim());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTicket, tvCliente, tvFecha, tvTotal, tvObs, tvItems;
        Spinner spEstado;

        PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTicket = itemView.findViewById(R.id.tvTicketPedidoAdmin);
            tvCliente = itemView.findViewById(R.id.tvClientePedidoAdmin);
            tvFecha = itemView.findViewById(R.id.tvFechaPedidoAdmin);
            tvTotal = itemView.findViewById(R.id.tvTotalPedidoAdmin);
            tvObs = itemView.findViewById(R.id.tvObsPedidoAdmin);
            tvItems = itemView.findViewById(R.id.tvItemsPedidoAdmin);
            spEstado = itemView.findViewById(R.id.spEstadoPedidoAdmin);
        }
    }
}
