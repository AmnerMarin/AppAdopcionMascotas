package sistemas.unc.edu.appadopcionmascotas.UI;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.List;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Solicitud;
import sistemas.unc.edu.appadopcionmascotas.R;

public class AdaptadorSolicitudRefugio extends  RecyclerView.Adapter<AdaptadorSolicitudRefugio.ViewHolder> {

    private List<Solicitud> lista;
    private Context context;
    private DAOAdopcion dao;

    public AdaptadorSolicitudRefugio(Context context, List<Solicitud> lista) {
        this.context = context;
        this.lista = lista;
        this.dao = new DAOAdopcion((Activity)context);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_solicitud_refugio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Solicitud solicitud = lista.get(position);

        holder.txtMascota.setText("Mascota: " + solicitud.getNombreMascota());
        holder.txtAdoptante.setText("Interesado: " + solicitud.getNombreAdoptante());
        holder.txtFecha.setText("Fecha: " + solicitud.getFecha());

        // --- LÓGICA DE CONTROL DE ESTADO ---
        String estadoActual = solicitud.getEstado(); // "Pendiente", "Aprobada", "Rechazada"

        if ("Pendiente".equalsIgnoreCase(estadoActual)) {
            // Estado inicial: Ambos botones visibles y habilitados
            holder.btnAceptar.setVisibility(View.VISIBLE);
            holder.btnRechazar.setVisibility(View.VISIBLE);
            holder.btnAceptar.setEnabled(true);
            holder.btnAceptar.setText("Aceptar");
            // Restaurar color original por si acaso
            holder.btnAceptar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    context.getResources().getColor(R.color.primary_color)));
        } else {
            // Ya fue procesada: Ocultamos el botón de rechazar y bloqueamos el de aceptar
            holder.btnRechazar.setVisibility(View.GONE);
            holder.btnAceptar.setEnabled(false); // No se puede volver a cliquear
            holder.btnAceptar.setText(estadoActual.toUpperCase());

            // Cambiar color según el resultado
            int colorRes = (estadoActual.equalsIgnoreCase("Aprobada"))
                    ? R.color.primary_color
                    : R.color.danger_red;

            holder.btnAceptar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    context.getResources().getColor(colorRes)));
        }

        // --- EVENTOS CLICK ---
        holder.btnAceptar.setOnClickListener(v -> {
            dao.aprobarSolicitud(solicitud.getIdAdopcion(), solicitud.getIdMascota());
            solicitud.setEstado("Aprobada"); // Actualizamos el objeto local
            notifyItemChanged(holder.getBindingAdapterPosition()); // Refrescamos la vista del item
        });

        holder.btnRechazar.setOnClickListener(v -> {
            dao.rechazarSolicitud(solicitud.getIdAdopcion());
            solicitud.setEstado("Rechazada"); // Actualizamos el objeto local
            notifyItemChanged(holder.getBindingAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMascota, txtAdoptante, txtFecha;
        Button btnAceptar, btnRechazar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtMascota = itemView.findViewById(R.id.txtNombreAnimal);
            txtAdoptante = itemView.findViewById(R.id.txtNombreAdoptante);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            btnAceptar = itemView.findViewById(R.id.btnAceptar);
            btnRechazar = itemView.findViewById(R.id.btnRechazar);
        }
    }
}
