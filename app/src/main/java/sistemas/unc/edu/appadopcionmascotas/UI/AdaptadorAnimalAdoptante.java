package sistemas.unc.edu.appadopcionmascotas.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sistemas.unc.edu.appadopcionmascotas.ActividadVerAnimal;
import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Animal;
import sistemas.unc.edu.appadopcionmascotas.R;

public class AdaptadorAnimalAdoptante extends RecyclerView.Adapter<AdaptadorAnimalAdoptante.AnimalVH> {

    private Context contexto;
    private List<Animal> listaanimales;
    private DAOAdopcion adopcion;
    private int idAdoptante; // <-- ID del adoptante

    // Listener para comunicar al fragment cuando se elimina un favorito
    public interface OnFavoritoListener {
        void onFavoritoEliminado(Animal animal);
    }
    private OnFavoritoListener favoritoListener;

    // Constructor
    public AdaptadorAnimalAdoptante(Context contexto, List<Animal> lista, int idAdoptante, OnFavoritoListener listener) {
        this.contexto = contexto;
        this.listaanimales = lista;
        this.adopcion = new DAOAdopcion((Activity) contexto);
        this.idAdoptante = idAdoptante;
        this.favoritoListener = listener;
    }

    @NonNull
    @Override
    public AnimalVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contexto).inflate(R.layout.item_animal_adoptante, parent, false);
        return new AnimalVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalVH holder, int position) {

        Animal animal = listaanimales.get(position);

        // --- Datos básicos ---
        holder.txtNombre.setText(animal.getNombre());
        holder.txtTipo.setText(animal.getEspecie());
        holder.txtRaza.setText(animal.getRaza());
        holder.txtEdad.setText(animal.getEdad());
        holder.txtSexo.setText(animal.getSexo());

        // --- Foto ---
        byte[] foto = animal.getFoto();
        if (foto != null) {
            Bitmap oImagen = BitmapFactory.decodeByteArray(foto, 0, foto.length);
            holder.img_animal.setImageBitmap(oImagen);
        } else {
            holder.img_animal.setImageResource(R.drawable.perro_prueba);
        }

        // --- Estado del favorito desde BD ---
        if (adopcion.esFavorito(idAdoptante, animal.getIdMascota())) {
            animal.setFavorito(true);
            holder.btnFavorito.setImageResource(R.drawable.corazon_lleno);
        } else {
            animal.setFavorito(false);
            holder.btnFavorito.setImageResource(R.drawable.selector_favoritos);
        }

        // --- Evento del botón favorito ---
        holder.btnFavorito.setOnClickListener(view -> {
            boolean nuevoEstado = !animal.isFavorito();
            animal.setFavorito(nuevoEstado);

            boolean resultado;

            if (nuevoEstado) {
                // Agregar a BD
                resultado = adopcion.agregarFavorito(idAdoptante, animal.getIdMascota());
                if (resultado) {
                    holder.btnFavorito.setImageResource(R.drawable.corazon_lleno);
                    Toast.makeText(contexto, "Añadido a favoritos", Toast.LENGTH_SHORT).show();
                } else {
                    animal.setFavorito(false);
                    Toast.makeText(contexto, "Error al agregar favorito", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Eliminar de BD
                resultado = adopcion.eliminarFavorito(idAdoptante, animal.getIdMascota());
                if (resultado) {
                    holder.btnFavorito.setImageResource(R.drawable.selector_favoritos);
                    Toast.makeText(contexto, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();

                    // Notificar al fragment que elimine de la lista
                    if (favoritoListener != null) {
                        favoritoListener.onFavoritoEliminado(animal);
                    }
                } else {
                    animal.setFavorito(true);
                    Toast.makeText(contexto, "Error al eliminar favorito", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // --- Click en toda la tarjeta para ver detalles ---
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(contexto, ActividadVerAnimal.class);
            intent.putExtra("ID_ANIMAL", animal.getIdMascota());
            contexto.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaanimales.size();
    }

    // --- ViewHolder ---
    public static class AnimalVH extends RecyclerView.ViewHolder {

        ImageView img_animal;
        ImageButton btnFavorito;
        TextView txtNombre, txtTipo, txtRaza, txtEdad, txtSexo;

        public AnimalVH(@NonNull View itemView) {
            super(itemView);
            img_animal = itemView.findViewById(R.id.img_animal);
            btnFavorito = itemView.findViewById(R.id.btnFavorito);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtTipo = itemView.findViewById(R.id.txtTipo);
            txtRaza = itemView.findViewById(R.id.txtRaza);
            txtEdad = itemView.findViewById(R.id.txtEdad);
            txtSexo = itemView.findViewById(R.id.txtSexo);
        }
    }
}
