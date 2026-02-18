package sistemas.unc.edu.appadopcionmascotas.UI;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Refugio;
import sistemas.unc.edu.appadopcionmascotas.R;

public class AdaptadorUbicacionRefugios extends RecyclerView.Adapter<AdaptadorUbicacionRefugios.UbicacionVH> {

    private Context contexto;
    private List<Refugio> listaRefugios;

    public AdaptadorUbicacionRefugios(Context contexto, List<Refugio> listaRefugios) {
        this.contexto = contexto;
        this.listaRefugios = listaRefugios;
    }

    @NonNull
    @Override
    public AdaptadorUbicacionRefugios.UbicacionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contexto).inflate(R.layout.item_lugar, parent, false);
        return new UbicacionVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorUbicacionRefugios.UbicacionVH holder, int position) {
        Refugio refugio = listaRefugios.get(position);
        holder.lb_localizacion_refugio_nombre.setText(refugio.getNombre_refugio());
        holder.lb_localizacion_refugio_direccion.setText(refugio.getDireccion());
        holder.lb_localizacion_refugio_telefono.setText(refugio.getTelefono());
        holder.lb_localizacion_refugio_email.setText(refugio.getDesripcion());

        DAOAdopcion dao = new DAOAdopcion((Activity) contexto);
        int cantidadAnimales = dao.obtenerCantidadAnimalesPorRefugio(refugio.getIdUsuario()); // O idRefugio si lo tienes en Refugio
        holder.lb_localizacion_chip.setText(cantidadAnimales + " animales");

        //Falta programar el botó como llegar
        holder.itemView.setOnClickListener(v -> {

            if (holder.groupExpandible.getVisibility() == View.VISIBLE) {

                holder.groupExpandible.setVisibility(View.GONE);

            } else {

                holder.groupExpandible.setVisibility(View.VISIBLE);
            }

        });
    }

    @Override
    public int getItemCount() {
        return listaRefugios.size();
    }

    public class UbicacionVH extends RecyclerView.ViewHolder{
        TextView lb_localizacion_refugio_nombre;
        TextView lb_localizacion_refugio_direccion;
        TextView lb_localizacion_refugio_telefono;
        TextView lb_localizacion_refugio_email;

        Chip lb_localizacion_chip;
        MaterialButton btnComoLlegar;

        Group groupExpandible;
        public UbicacionVH(@NonNull View itemView) {
            super(itemView);
            lb_localizacion_refugio_nombre = itemView.findViewById(R.id.lb_localizacion_refugio_nombre);
            lb_localizacion_refugio_direccion = itemView.findViewById(R.id.lb_localizacion_refugio_direccion);
            lb_localizacion_refugio_telefono = itemView.findViewById(R.id.lb_localizacion_refugio_telefono);
            lb_localizacion_refugio_email = itemView.findViewById(R.id.lb_localizacion_refugio_email);
            lb_localizacion_chip = itemView.findViewById(R.id.lb_localizacion_chip);
            btnComoLlegar = itemView.findViewById(R.id.btnComoLlegar);
            groupExpandible = itemView.findViewById(R.id.group_expandible);

        }
    }
}
