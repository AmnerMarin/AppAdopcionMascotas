package sistemas.unc.edu.appadopcionmascotas.UI;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import sistemas.unc.edu.appadopcionmascotas.ActividadChat;
import sistemas.unc.edu.appadopcionmascotas.Model.Conversacion;
import sistemas.unc.edu.appadopcionmascotas.R;

public class AdaptadorConversaciones extends RecyclerView.Adapter<AdaptadorConversaciones.ViewHolder> {

    private List<Conversacion> lista;
    private Context context;

    public AdaptadorConversaciones(Context context, List<Conversacion> lista) {
        this.context = context;
        this.lista = lista;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversacion, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Conversacion conv = lista.get(position);

        holder.tvNombre.setText(conv.getNombre());
        holder.tvMensaje.setText(conv.getUltimoMensaje());
        holder.tvHora.setText(conv.getHora());
        holder.tvMascota.setText("🐶 " + conv.getNombreMascota());

        // Al dar clic en la conversación, abrimos el chat detallado
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActividadChat.class);
            intent.putExtra("ID_CHAT", conv.getIdChat());
            intent.putExtra("NOMBRE_DESTINO", conv.getNombre());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // TODO: Agregar elementos de la vista

        TextView tvNombre, tvMensaje, tvHora, tvMascota;
        ShapeableImageView imgUserIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // TODO: Enlazar elementos de la vista
            tvNombre = itemView.findViewById(R.id.tvNombreUsuario);
            tvMensaje = itemView.findViewById(R.id.tvUltimoMensaje);
            tvHora = itemView.findViewById(R.id.tvHora);
            tvMascota = itemView.findViewById(R.id.tvMascota);
        }
    }
}
