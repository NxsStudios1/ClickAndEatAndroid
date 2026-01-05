package com.example.login.cliente.pedidoc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;
import com.example.login.network.model.DetallePedidoDto;
import com.example.login.network.model.PedidoDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PedidoClienteAdapter
        extends RecyclerView.Adapter<PedidoClienteAdapter.PedidoClienteViewHolder> {

    public static class PedidoConDetalles {
        public PedidoDto pedido;
        public List<DetallePedidoDto> detalles;

        public PedidoConDetalles(PedidoDto pedido, List<DetallePedidoDto> detalles) {
            this.pedido = pedido;
            this.detalles = detalles;
        }
    }

    private final List<PedidoConDetalles> lista;

    private Map<Integer, String> nombresProductos = new HashMap<>();
    private Map<Integer, String> nombresPromos = new HashMap<>();

    public PedidoClienteAdapter(List<PedidoConDetalles> lista) {
        this.lista = lista;
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
    public PedidoClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido_cliente_usuario, parent, false);
        return new PedidoClienteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoClienteViewHolder holder, int position) {
        PedidoConDetalles data = lista.get(position);
        PedidoDto p = data.pedido;

        holder.tvTicket.setText("Ticket: " + p.getNumeroTicket());
        holder.tvFecha.setText("Fecha: " + p.getFechaPedido());
        holder.tvTotal.setText(String.format("Total: $ %.2f", p.getTotal()));

        String estadoText;
        switch (p.getEstado()) {
            case 1: estadoText = "Pendiente"; break;
            case 2: estadoText = "En proceso"; break;
            case 3: estadoText = "Terminado"; break;
            case 4: estadoText = "Pagado"; break;
            case 5: estadoText = "Cancelado"; break;
            default: estadoText = "Desconocido";
        }
        holder.tvEstado.setText("Estado: " + estadoText);

        String obs = p.getObservaciones();
        if (obs == null || obs.trim().isEmpty()) {
            holder.tvObs.setText("Observaciones: Sin observaciones");
        } else {
            holder.tvObs.setText("Observaciones: " + obs);
        }

        StringBuilder sb = new StringBuilder();
        if (data.detalles == null || data.detalles.isEmpty()) {
            sb.append("Sin productos/promociones registrados.");
        } else {
            for (DetallePedidoDto d : data.detalles) {

                String etiqueta;
                String nombreItem;

                if (d.getTipoItem() == 2) { // PROMO
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

    static class PedidoClienteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTicket, tvEstado, tvFecha, tvTotal, tvObs, tvItems;

        public PedidoClienteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTicket = itemView.findViewById(R.id.tvTicketPedidoCliente);
            tvEstado = itemView.findViewById(R.id.tvEstadoPedidoCliente);
            tvFecha = itemView.findViewById(R.id.tvFechaPedidoCliente);
            tvTotal = itemView.findViewById(R.id.tvTotalPedidoCliente);
            tvObs = itemView.findViewById(R.id.tvObsPedidoCliente);
            tvItems = itemView.findViewById(R.id.tvItemsPedidoCliente);
        }
    }
}
