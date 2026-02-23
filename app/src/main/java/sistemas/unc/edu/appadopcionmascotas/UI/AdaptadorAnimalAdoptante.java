package sistemas.unc.edu.appadopcionmascotas.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
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
import sistemas.unc.edu.appadopcionmascotas.Firebase.DbRepositorioFavoritos;
import sistemas.unc.edu.appadopcionmascotas.Model.Animal;
import sistemas.unc.edu.appadopcionmascotas.R;

public class AdaptadorAnimalAdoptante extends RecyclerView.Adapter<AdaptadorAnimalAdoptante.AnimalVH> {

    private Context contexto;
    private List<Animal> listaanimales;
    private DAOAdopcion adopcion;
    private DbRepositorioFavoritos repoFavoritos;
    private int idAdoptante;

    public interface OnFavoritoListener {
        void onFavoritoEliminado(Animal animal);
    }
    private OnFavoritoListener favoritoListener;

    public AdaptadorAnimalAdoptante(Context contexto, List<Animal> lista, int idAdoptante, OnFavoritoListener listener) {
        this.contexto = contexto;
        this.listaanimales = lista;
        this.idAdoptante = idAdoptante;
        this.favoritoListener = listener;
        this.adopcion = new DAOAdopcion((Activity) contexto);
        this.repoFavoritos = new DbRepositorioFavoritos(contexto);
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

        holder.txtNombre.setText(animal.getNombre());
        holder.txtTipo.setText(animal.getEspecie());
        holder.txtRaza.setText(animal.getRaza());
        holder.txtEdad.setText(animal.getEdad());
        holder.txtSexo.setText(animal.getSexo());

        byte[] foto = animal.getFoto();
        if (foto != null) {
            Bitmap oImagen = BitmapFactory.decodeByteArray(foto, 0, foto.length);
            holder.img_animal.setImageBitmap(oImagen);
        } else {
            holder.img_animal.setImageResource(R.drawable.perro_prueba);
        }

        // Leer estado desde la base de datos
        if (adopcion.esFavorito(idAdoptante, animal.getIdMascota())) {
            animal.setFavorito(true);
            holder.btnFavorito.setImageResource(R.drawable.corazon_lleno);
        } else {
            animal.setFavorito(false);
            holder.btnFavorito.setImageResource(R.drawable.selector_favoritos);
        }

        // ===============================================
        // EVENTO DEL CORAZÓN (ACTUALIZACIÓN INMEDIATA)
        // ===============================================
        holder.btnFavorito.setOnClickListener(view -> {
            boolean eraFavorito = animal.isFavorito();

            // 1. Cambiamos la interfaz INMEDIATAMENTE para que el usuario no espere
            if (eraFavorito) {
                animal.setFavorito(false);
                holder.btnFavorito.setImageResource(R.drawable.selector_favoritos);
                if (favoritoListener != null) favoritoListener.onFavoritoEliminado(animal);

                // 2. Ejecutamos la eliminación en BD y Firebase
                repoFavoritos.eliminarFavorito(idAdoptante, animal.getIdMascota(), new DbRepositorioFavoritos.FavoritoCallback() {
                    @Override
                    public void onSuccess() {
                        // Todo salió bien en la nube, no hay que hacer nada visualmente porque ya lo cambiamos
                    }
                    @Override
                    public void onError(String msg) {
                        // Si hubo error, regresamos el corazón a su estado original
                        new Handler(Looper.getMainLooper()).post(() -> {
                            animal.setFavorito(true);
                            holder.btnFavorito.setImageResource(R.drawable.corazon_lleno);
                            Toast.makeText(contexto, "Error de red, inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
                        });
                    }
                });

            } else {
                // 1. Cambiamos la interfaz INMEDIATAMENTE
                animal.setFavorito(true);
                holder.btnFavorito.setImageResource(R.drawable.corazon_lleno);

                // 2. Ejecutamos el guardado en BD y Firebase
                repoFavoritos.agregarFavorito(idAdoptante, animal.getIdMascota(), new DbRepositorioFavoritos.FavoritoCallback() {
                    @Override
                    public void onSuccess() {}
                    @Override
                    public void onError(String msg) {
                        // Revertir si hay error
                        new Handler(Looper.getMainLooper()).post(() -> {
                            animal.setFavorito(false);
                            holder.btnFavorito.setImageResource(R.drawable.selector_favoritos);
                            Toast.makeText(contexto, "Error de red, inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        });

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

    public void actualizarLista(List<Animal> nuevaLista) {
        this.listaanimales = nuevaLista;
        notifyDataSetChanged();
    }
}