package sistemas.unc.edu.appadopcionmascotas.UI;

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

import java.util.ArrayList;
import java.util.List;

import sistemas.unc.edu.appadopcionmascotas.ActividadVerAnimal;
import sistemas.unc.edu.appadopcionmascotas.Model.Animal;
import sistemas.unc.edu.appadopcionmascotas.R;

public class AdaptadorAnimalAdoptante extends RecyclerView.Adapter<AdaptadorAnimalAdoptante.AnimalVH>{

    private Context contexto;
    private List<Animal> listaanimales;
    private List<Animal> listaanimalesfiltrados = new ArrayList<>();

    //Generar Constructor

    //1. Crear el ViewHolder
    public AdaptadorAnimalAdoptante(Context contexto, List<Animal> lista) {
        this.contexto = contexto;
        this.listaanimales = lista;
    }

    @NonNull
    @Override
    public AdaptadorAnimalAdoptante.AnimalVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(contexto).inflate(R.layout.item_animal_adoptante,parent,false);
        return new AnimalVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalVH holder, int position) {

        Animal animal = listaanimales.get(position);
//
        holder.txtNombre.setText(animal.getNombre());
        holder.txtTipo.setText(animal.getEspecie()); // Ej: "Perro"
        holder.txtRaza.setText(animal.getRaza());    // Ej: "Golden Retriever"
        holder.txtEdad.setText(animal.getEdad());    // Ej: "3 años"
        holder.txtSexo.setText(animal.getSexo());    // Ej: "Hembra"

        byte [] foto = animal.getFoto();

        if(foto != null){

            Bitmap oImagen = BitmapFactory.decodeByteArray(foto, 0, foto.length);
            holder.img_animal.setImageBitmap(oImagen);
        }
        else {
            holder.img_animal.setImageResource(R.drawable.perro_prueba);
        }

        //Lógica del botón Favorito (Corazón)
        if (animal.isFavorito()) {
            holder.btnFavorito.setImageResource(R.drawable.corazon_lleno);
        } else {
            holder.btnFavorito.setImageResource(R.drawable.selector_favoritos); // El vacío original
        }

        // 2. EL EVENTO CLICK
        holder.btnFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cambiamos el estado en el objeto (importante para el scroll)
                boolean nuevoEstado = !animal.isFavorito();
                animal.setFavorito(nuevoEstado);

                // Cambiamos el icono visualmente
                if (nuevoEstado) {
                    // Pone el corazón LLENO
                    holder.btnFavorito.setImageResource(R.drawable.corazon_lleno);
                    Toast.makeText(view.getContext(), "Añadido a favoritos ❤️", Toast.LENGTH_SHORT).show();
                } else {
                    // Vuelve al corazón VACÍO
                    holder.btnFavorito.setImageResource(R.drawable.selector_favoritos);
                    Toast.makeText(view.getContext(), "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 5. Clic en TODA la tarjeta para "Ver Detalles"
        // Usamos 'holder.itemView' porque tu diseño no tiene un botón específico de "Ver"
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActividadVerAnimal.class);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaanimales.size();
    }

    public class AnimalVH extends RecyclerView.ViewHolder {

        // Declaración de los componentes de la UI
        ImageView img_animal;
        ImageButton btnFavorito;
        TextView txtNombre, txtTipo, txtRaza, txtEdad, txtSexo;

        public AnimalVH(@NonNull View itemView) {
            super(itemView);

            // Asociamos los elementos del XML (findViewById)

            // ¡OJO! Asegúrate de agregar android:id="@+id/img_animal" a tu ShapeableImageView en el XML
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

