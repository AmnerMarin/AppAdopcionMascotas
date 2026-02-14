package sistemas.unc.edu.appadopcionmascotas.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import sistemas.unc.edu.appadopcionmascotas.Model.Mensaje;
import sistemas.unc.edu.appadopcionmascotas.R;

public class AdaptadorMensaje extends RecyclerView.Adapter<AdaptadorMensaje.ViewHolder> {

    List<Mensaje> listaMensajes;

    public AdaptadorMensaje(List<Mensaje> listaMensajes) {
        this.listaMensajes = listaMensajes;
    }
    @NonNull
    @Override
    public AdaptadorMensaje.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorMensaje.ViewHolder holder, int position) {
        Mensaje mensaje = listaMensajes.get(position);
        holder.tvNombreUsuario.setText(mensaje.getUsuario());
        holder.tvEmailUsuario.setText(mensaje.getEmail());
        holder.tvFecha.setText(mensaje.getFecha());
        holder.tvMascotaInteres.setText(mensaje.getMascotaInteres());
        holder.tvContenidoMensaje.setText(mensaje.getContenido());
        holder.imgUserIcon.setImageResource(R.drawable.usuario);
        holder.btnResponder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreUsuario, tvEmailUsuario, tvFecha, tvMascotaInteres, tvContenidoMensaje;
        MaterialButton btnResponder;
        ShapeableImageView imgUserIcon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreUsuario = itemView.findViewById(R.id.tvNombreUsuario);
            tvEmailUsuario = itemView.findViewById(R.id.tvEmailUsuario);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvMascotaInteres = itemView.findViewById(R.id.tvMascotaInteres);
            tvContenidoMensaje = itemView.findViewById(R.id.tvContenidoMensaje);
            btnResponder = itemView.findViewById(R.id.btnResponder);
            imgUserIcon = itemView.findViewById(R.id.imgUserIcon);
        }
    }
}
