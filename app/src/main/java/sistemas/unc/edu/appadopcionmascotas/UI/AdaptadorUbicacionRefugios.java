package sistemas.unc.edu.appadopcionmascotas.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Refugio;
import sistemas.unc.edu.appadopcionmascotas.R;

public class AdaptadorUbicacionRefugios extends RecyclerView.Adapter<AdaptadorUbicacionRefugios.UbicacionVH> {

    private Context contexto;
    private List<Refugio> listaRefugios;
    private DAOAdopcion dao;
    private Set<Integer> itemsExpandido = new HashSet<>();

    public AdaptadorUbicacionRefugios(Context contexto, List<Refugio> listaRefugios) {
        this.contexto = contexto;
        this.listaRefugios = listaRefugios;
        this.dao = new DAOAdopcion((Activity) contexto);
    }

    @NonNull
    @Override
    public UbicacionVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contexto).inflate(R.layout.item_lugar, parent, false);
        return new UbicacionVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UbicacionVH holder, int position) {
        Refugio refugio = listaRefugios.get(position);

        holder.lb_localizacion_refugio_nombre.setText(refugio.getNombre_refugio());
        holder.lb_localizacion_refugio_direccion.setText(refugio.getDireccion());
        holder.lb_localizacion_refugio_telefono.setText(refugio.getTelefono());
        holder.lb_localizacion_refugio_descripcion.setText(refugio.getDesripcion());

        // LÓGICA DE EXTRANJEROS VS LOCALES
        if (refugio.isEsExterno()) {
            holder.lb_localizacion_refugio_email.setText(refugio.getCorreo());
            holder.lb_localizacion_chip.setText("Asociado Web");
        } else {
            String correoRefugio = dao.obtenerCorreoPorIdUsuario(refugio.getIdUsuario());
            int idrefugio = dao.obtenerIdRefugioPorUsuario(refugio.getIdUsuario());
            int cantidadAnimales = dao.obtenerCantidadAnimalesPorRefugio(idrefugio);

            holder.lb_localizacion_refugio_email.setText(correoRefugio);
            holder.lb_localizacion_chip.setText(cantidadAnimales + " animales");
        }

        if (itemsExpandido.contains(position)) {
            holder.groupExpandible.setVisibility(View.VISIBLE);
        } else {
            holder.groupExpandible.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (itemsExpandido.contains(position)) itemsExpandido.remove(position);
            else itemsExpandido.add(position);
            notifyItemChanged(position);
        });

        holder.btnComoLlegar.setOnClickListener(v -> {
            String direccion = refugio.getDireccion();
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(direccion));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            contexto.startActivity(mapIntent);
        });
    }

    @Override
    public int getItemCount() {
        return listaRefugios.size();
    }

    public void actualizarLista(List<Refugio> nuevaLista) {
        this.listaRefugios = nuevaLista;
        notifyDataSetChanged();
    }

    public class UbicacionVH extends RecyclerView.ViewHolder {
        TextView lb_localizacion_refugio_nombre, lb_localizacion_refugio_direccion, lb_localizacion_refugio_telefono, lb_localizacion_refugio_email, lb_localizacion_refugio_descripcion;
        Chip lb_localizacion_chip;
        MaterialButton btnComoLlegar;
        Group groupExpandible;

        public UbicacionVH(@NonNull View itemView) {
            super(itemView);
            lb_localizacion_refugio_nombre = itemView.findViewById(R.id.lb_localizacion_refugio_nombre);
            lb_localizacion_refugio_direccion = itemView.findViewById(R.id.lb_localizacion_refugio_direccion);
            lb_localizacion_refugio_telefono = itemView.findViewById(R.id.lb_localizacion_refugio_telefono);
            lb_localizacion_refugio_email = itemView.findViewById(R.id.lb_localizacion_refugio_email);
            lb_localizacion_refugio_descripcion = itemView.findViewById(R.id.lb_localizacion_refugio_descripcion);
            lb_localizacion_chip = itemView.findViewById(R.id.lb_localizacion_chip);
            btnComoLlegar = itemView.findViewById(R.id.btnComoLlegar);
            groupExpandible = itemView.findViewById(R.id.group_expandible);
        }
    }
}