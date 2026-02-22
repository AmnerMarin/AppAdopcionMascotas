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

    // Para controlar qué items están expandidos
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

        String correoRefugio = dao.obtenerCorreoPorIdUsuario(refugio.getIdUsuario());

        int idrefugio = dao.obtenerIdRefugioPorUsuario(refugio.getIdUsuario());

        int cantidadAnimales = dao.obtenerCantidadAnimalesPorRefugio(idrefugio);

        holder.lb_localizacion_refugio_email.setText(correoRefugio);

        // ✅ NUEVO: Agregar la descripción del refugio
        // Nota: Asegúrate de que el método en tu modelo Refugio se llame getDescripcion() o getDesripcion() (con error de tipeo) según como lo tengas.
        holder.lb_localizacion_refugio_descripcion.setText(refugio.getDesripcion());

        holder.lb_localizacion_chip.setText(cantidadAnimales + " animales");

        // Control de estado expandido
        if (itemsExpandido.contains(position)) {
            holder.groupExpandible.setVisibility(View.VISIBLE);
        } else {
            holder.groupExpandible.setVisibility(View.GONE);
        }

        // Click expandir (✅ MEJORADO PARA ANIMACIONES SUAVES)
        holder.itemView.setOnClickListener(v -> {
            if (itemsExpandido.contains(position)) {
                itemsExpandido.remove(position); // Si está abierto, lo cerramos
            } else {
                itemsExpandido.add(position); // Si está cerrado, lo abrimos
            }
            // Esto avisa al adaptador que la tarjeta cambió, forzando a que se redibuje con animación
            notifyItemChanged(position);
        });

        // BOTÓN CÓMO LLEGAR
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

    public class UbicacionVH extends RecyclerView.ViewHolder {

        TextView lb_localizacion_refugio_nombre;
        TextView lb_localizacion_refugio_direccion;
        TextView lb_localizacion_refugio_telefono;
        TextView lb_localizacion_refugio_email;
        TextView lb_localizacion_refugio_descripcion; // ✅ NUEVO CAMPO DECLARADO

        Chip lb_localizacion_chip;
        MaterialButton btnComoLlegar;
        Group groupExpandible;

        public UbicacionVH(@NonNull View itemView) {
            super(itemView);

            lb_localizacion_refugio_nombre = itemView.findViewById(R.id.lb_localizacion_refugio_nombre);
            lb_localizacion_refugio_direccion = itemView.findViewById(R.id.lb_localizacion_refugio_direccion);
            lb_localizacion_refugio_telefono = itemView.findViewById(R.id.lb_localizacion_refugio_telefono);
            lb_localizacion_refugio_email = itemView.findViewById(R.id.lb_localizacion_refugio_email);

            // ✅ NUEVO CAMPO ENLAZADO
            lb_localizacion_refugio_descripcion = itemView.findViewById(R.id.lb_localizacion_refugio_descripcion);

            lb_localizacion_chip = itemView.findViewById(R.id.lb_localizacion_chip);
            btnComoLlegar = itemView.findViewById(R.id.btnComoLlegar);
            groupExpandible = itemView.findViewById(R.id.group_expandible);
        }
    }
}