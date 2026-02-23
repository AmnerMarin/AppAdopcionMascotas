package sistemas.unc.edu.appadopcionmascotas.UI;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import sistemas.unc.edu.appadopcionmascotas.ActividadEditarAnimal;
import sistemas.unc.edu.appadopcionmascotas.ActividadVerAnimal;
import sistemas.unc.edu.appadopcionmascotas.Firebase.DbAnimalRepositorio;
import sistemas.unc.edu.appadopcionmascotas.Model.Animal;
import sistemas.unc.edu.appadopcionmascotas.R;

public class AdaptadorAnimal extends RecyclerView.Adapter<AdaptadorAnimal.ViewHolder> {

    private List<Animal> listaAnimales;
    private Context context;

    public AdaptadorAnimal(List<Animal> listaAnimales, Context context) {
        this.listaAnimales = listaAnimales;
        this.context = context;
    }

    @NonNull
    @Override
    public AdaptadorAnimal.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_animal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorAnimal.ViewHolder holder, int position) {
        Animal animal = listaAnimales.get(position);
        holder.tvNombre.setText(animal.getNombre());
        holder.tvRaza.setText(animal.getRaza());
        holder.tvEspecie.setText(animal.getEspecie());

        byte[] foto = animal.getFoto();

        if (foto != null && foto.length > 0) {
            Bitmap oImagen = BitmapFactory.decodeByteArray(foto, 0, foto.length);
            holder.imgAnimal.setImageBitmap(oImagen);
        } else {
            holder.imgAnimal.setImageResource(R.drawable.perro_prueba);
        }

        holder.btnVer.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ActividadVerAnimal.class);
            intent.putExtra("ID_ANIMAL", animal.getIdMascota());
            view.getContext().startActivity(intent);
        });

        // Configurar el click para editar
        View.OnClickListener listenerEditar = view -> {
            Intent intent = new Intent(view.getContext(), ActividadEditarAnimal.class);
            intent.putExtra("ID_ANIMAL", animal.getIdMascota());
            view.getContext().startActivity(intent);
        };
        holder.btnEditar.setOnClickListener(listenerEditar);
        holder.itemView.setOnClickListener(listenerEditar);

        // ==========================================
        // NUEVA LÓGICA DE ELIMINAR CON FIREBASE
        // ==========================================
        holder.btnEliminar.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(holder.itemView.getContext())
                    .setTitle("¿Eliminar a " + animal.getNombre() + "?")
                    .setMessage("Esta acción eliminará la publicación de este dispositivo y de la nube. No se puede deshacer.")
                    .setPositiveButton("Eliminar", (dialog, which) -> {

                        holder.btnEliminar.setEnabled(false); // Evitar doble clic
                        Toast.makeText(holder.itemView.getContext(), "Eliminando...", Toast.LENGTH_SHORT).show();

                        // Usamos el repositorio en lugar del DAO
                        DbAnimalRepositorio repo = new DbAnimalRepositorio(holder.itemView.getContext());

                        repo.eliminarAnimal(animal.getIdMascota(), animal.getFirebaseUID(), new DbAnimalRepositorio.AnimalCallback() {
                            @Override
                            public void onSuccess() {
                                int adapterPos = holder.getBindingAdapterPosition();
                                if (adapterPos != RecyclerView.NO_POSITION) {
                                    listaAnimales.remove(adapterPos);
                                    notifyItemRemoved(adapterPos);
                                    notifyItemRangeChanged(adapterPos, listaAnimales.size());

                                    Toast.makeText(holder.itemView.getContext(),
                                            "Animal eliminado correctamente",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(String mensaje) {
                                Toast.makeText(holder.itemView.getContext(),
                                        "Error al eliminar: " + mensaje,
                                        Toast.LENGTH_LONG).show();
                                holder.btnEliminar.setEnabled(true);
                            }
                        });
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return listaAnimales.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvRaza, tvEspecie;
        ShapeableImageView imgAnimal;
        MaterialButton btnVer, btnEditar, btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombrePerfil);
            tvRaza = itemView.findViewById(R.id.tvRaza);
            tvEspecie = itemView.findViewById(R.id.tvEspecie);
            imgAnimal = itemView.findViewById(R.id.imgAnimal);
            btnVer = itemView.findViewById(R.id.btnVer);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}