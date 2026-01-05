package com.example.login.cliente.comentac;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.R;
import com.example.login.network.model.ComentarioConRespuestaUi;

import java.util.List;

public class ComentariosClienteAdapter
        extends RecyclerView.Adapter<ComentariosClienteAdapter.ViewHolder> {

    public interface OnComentarioClickListener {
        void onComentarioClick(ComentarioConRespuestaUi comentario);
    }

    private final List<ComentarioConRespuestaUi> datos;
    private final OnComentarioClickListener listener;

    public ComentariosClienteAdapter(List<ComentarioConRespuestaUi> datos,
                                     OnComentarioClickListener listener) {
        this.datos = datos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comentario_cliente, parent, false);

        return new ViewHolder(view, listener, datos);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {

        ComentarioConRespuestaUi item = datos.get(position);

        h.tvNombreCliente.setText(item.nombreCliente != null
                ? item.nombreCliente : "Cliente");

        h.tvFechaComentario.setText(item.fechaComentario != null
                ? item.fechaComentario : "");

        h.tvCategoriaCalificacion.setText(
                "Categoría: " + item.categoria +
                        "   Calificación: " + item.calificacion
        );

        h.tvAsunto.setText("Asunto: " + item.asunto);
        h.tvTextoComentario.setText(item.contenido);

        if (item.contenidoRespuesta != null &&
                !item.contenidoRespuesta.trim().isEmpty()) {

            h.layoutRespuesta.setVisibility(View.VISIBLE);
            h.tvNombreAdmin.setText(
                    "Administrador: " + item.nombreAdmin
            );
            h.tvFechaRespuesta.setText(item.fechaRespuesta);
            h.tvTextoRespuesta.setText(item.contenidoRespuesta);

        } else {
            h.layoutRespuesta.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombreCliente, tvFechaComentario,
                tvCategoriaCalificacion, tvAsunto, tvTextoComentario;

        LinearLayout layoutRespuesta;
        TextView tvNombreAdmin, tvFechaRespuesta, tvTextoRespuesta;

        ViewHolder(@NonNull View itemView,
                   OnComentarioClickListener listener,
                   List<ComentarioConRespuestaUi> datos) {

            super(itemView);

            tvNombreCliente = itemView.findViewById(R.id.tvNombreCliente);
            tvFechaComentario = itemView.findViewById(R.id.tvFechaComentario);
            tvCategoriaCalificacion =
                    itemView.findViewById(R.id.tvCategoriaCalificacion);
            tvAsunto = itemView.findViewById(R.id.tvAsuntoComentario);
            tvTextoComentario = itemView.findViewById(R.id.tvTextoComentario);

            layoutRespuesta =
                    itemView.findViewById(R.id.layoutRespuestaAdmin);
            tvNombreAdmin =
                    itemView.findViewById(R.id.tvNombreAdmin);
            tvFechaRespuesta =
                    itemView.findViewById(R.id.tvFechaRespuesta);
            tvTextoRespuesta =
                    itemView.findViewById(R.id.tvTextoRespuesta);

            itemView.setOnClickListener(v -> {
                if (listener == null) return;
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onComentarioClick(datos.get(pos));
                }
            });
        }
    }
}
