package sistemas.unc.edu.appadopcionmascotas;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import sistemas.unc.edu.appadopcionmascotas.Model.Animal;

public class AdaptadorAnimal extends RecyclerView.Adapter<AdaptadorAnimal.ViewHolder> {

    private List<Animal> listaAnimales;
    public AdaptadorAnimal(List<Animal> listaAnimales) {
        this.listaAnimales = listaAnimales;
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
        holder.imgAnimal.setImageResource(animal.getImagenRes());
        holder.btnVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //abrir la activity ver
                Intent intent = new Intent(view.getContext(), ActividadVerAnimal.class);
                view.getContext().startActivity(intent);


            }
        });

        holder.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //abrir la activity ver
                Intent intent = new Intent(view.getContext(), ActividadEditarAnimal.class);
                view.getContext().startActivity(intent);

            }
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
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvRaza = itemView.findViewById(R.id.tvRaza);
            tvEspecie = itemView.findViewById(R.id.tvEspecie);
            imgAnimal = itemView.findViewById(R.id.imgAnimal);
            btnVer = itemView.findViewById(R.id.btnVer);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
