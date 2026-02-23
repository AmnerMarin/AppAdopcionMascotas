package sistemas.unc.edu.appadopcionmascotas.UI;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Firebase.DbRepositorioAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Animal;
import sistemas.unc.edu.appadopcionmascotas.Model.Solicitud;
import sistemas.unc.edu.appadopcionmascotas.R;

public class AdaptadorSolicitudRefugio extends RecyclerView.Adapter<AdaptadorSolicitudRefugio.ViewHolder> {

    private List<Solicitud> lista;
    private Context context;
    private DAOAdopcion dao;
    private DbRepositorioAdopcion repoAdopcion;

    public AdaptadorSolicitudRefugio(Context context, List<Solicitud> lista) {
        this.context = context;
        this.lista = lista;
        this.dao = new DAOAdopcion((Activity) context);
        this.repoAdopcion = new DbRepositorioAdopcion(context);
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
        String estadoActual = solicitud.getEstado();

        if ("Pendiente".equalsIgnoreCase(estadoActual)) {
            holder.btnAceptar.setVisibility(View.VISIBLE);
            holder.btnRechazar.setVisibility(View.VISIBLE);
            holder.btnAceptar.setEnabled(true);
            holder.btnRechazar.setEnabled(true);
            holder.btnAceptar.setText("Aceptar");
            holder.btnAceptar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    context.getResources().getColor(R.color.primary_color)));
        } else {
            holder.btnRechazar.setVisibility(View.GONE);
            holder.btnAceptar.setEnabled(false);
            holder.btnAceptar.setText(estadoActual.toUpperCase());

            int colorRes = (estadoActual.equalsIgnoreCase("Aprobada"))
                    ? R.color.primary_color
                    : R.color.danger_red;

            holder.btnAceptar.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    context.getResources().getColor(colorRes)));
        }

        // --- EVENTOS CLICK CON FIREBASE ---
        holder.btnAceptar.setOnClickListener(v -> {
            procesarRespuesta(holder, solicitud, true, position);
        });

        holder.btnRechazar.setOnClickListener(v -> {
            procesarRespuesta(holder, solicitud, false, position);
        });
    }

    private void procesarRespuesta(ViewHolder holder, Solicitud solicitud, boolean aprobar, int position) {
        holder.btnAceptar.setEnabled(false);
        holder.btnRechazar.setEnabled(false);

        // Obtenemos el FirebaseUID de la mascota para cambiar su estado a "Adoptado" en la nube
        Animal mascotaLocal = dao.obtenerDetalleAnimalConRefugio(solicitud.getIdMascota());
        String uidMascota = mascotaLocal != null ? mascotaLocal.getFirebaseUID() : null;

        // Obtenemos el FirebaseUID de la solicitud
        String uidSolicitud = solicitud.getFirebaseUID();

        if (uidSolicitud == null || uidSolicitud.isEmpty()) {
            Toast.makeText(context, "Error: La solicitud no tiene enlace a la nube", Toast.LENGTH_SHORT).show();
            holder.btnAceptar.setEnabled(true);
            holder.btnRechazar.setEnabled(true);
            return;
        }

        Toast.makeText(context, "Enviando respuesta...", Toast.LENGTH_SHORT).show();

        // Llamamos al Repositorio para guardar en Firebase y SQLite
        repoAdopcion.responderSolicitud(solicitud.getIdAdopcion(), uidSolicitud, solicitud.getIdMascota(), uidMascota, aprobar, new DbRepositorioAdopcion.AdopcionCallback() {
            @Override
            public void onSuccess() {
                // Volvemos al hilo principal para actualizar la interfaz
                new Handler(Looper.getMainLooper()).post(() -> {
                    solicitud.setEstado(aprobar ? "Aprobada" : "Rechazada");
                    notifyItemChanged(position);
                    Toast.makeText(context, "¡Respuesta registrada con éxito!", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String mensaje) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    holder.btnAceptar.setEnabled(true);
                    holder.btnRechazar.setEnabled(true);
                    Toast.makeText(context, "Error: " + mensaje, Toast.LENGTH_LONG).show();
                });
            }
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