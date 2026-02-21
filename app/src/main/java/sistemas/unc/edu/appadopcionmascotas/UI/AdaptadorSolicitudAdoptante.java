package sistemas.unc.edu.appadopcionmascotas.UI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import sistemas.unc.edu.appadopcionmascotas.Model.Solicitud;
import sistemas.unc.edu.appadopcionmascotas.R;

public class AdaptadorSolicitudAdoptante extends RecyclerView.Adapter<AdaptadorSolicitudAdoptante.ViewHolder> {

    private Context context;
    private List<Solicitud> lista; // Cambiado a List<Solicitud>

    public AdaptadorSolicitudAdoptante(Context context, List<Solicitud> lista) {
        this.context = context;
        this.lista = lista;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_solicitud_adoptante, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Solicitud solicitud = lista.get(position);

        holder.txtMascota.setText(solicitud.getNombreMascota());
        holder.txtRefugio.setText("Refugio: " + solicitud.getNombreAdoptante()); // Reutilizamos el campo
        holder.txtFecha.setText("Solicitado: " + solicitud.getFecha());
        holder.txtEstado.setText(solicitud.getEstado());

        // 1. Cargar imagen de la mascota
        if (solicitud.getFotoMascota() != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(solicitud.getFotoMascota(), 0, solicitud.getFotoMascota().length);
            holder.imgMascota.setImageBitmap(bmp);
        }

        // 2. Lógica de colores según el estado
        switch (solicitud.getEstado()) {
            case "Pendiente":
                holder.txtEstado.setTextColor(android.graphics.Color.parseColor("#A17D10"));
                break;
            case "Aprobada":
                holder.txtEstado.setTextColor(android.graphics.Color.parseColor("#2E7D32"));
                break;
            case "Rechazada":
                holder.txtEstado.setTextColor(android.graphics.Color.parseColor("#C62828"));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMascota, txtRefugio, txtFecha, txtEstado;
        ImageView imgMascota;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMascota = itemView.findViewById(R.id.txtNombreMascota);
            txtRefugio = itemView.findViewById(R.id.txtNombreRefugio);
            txtFecha = itemView.findViewById(R.id.txtFechaSolicitud);
            txtEstado = itemView.findViewById(R.id.txtEstadoSolicitud);
            imgMascota = itemView.findViewById(R.id.imgMascotaSolicitud);
        }
    }
}
