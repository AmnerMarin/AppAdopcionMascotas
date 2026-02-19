package sistemas.unc.edu.appadopcionmascotas.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Animal;
import sistemas.unc.edu.appadopcionmascotas.R;
import sistemas.unc.edu.appadopcionmascotas.UI.AdaptadorAnimalAdoptante;

public class InicioAdoptanteFragment extends Fragment {

    private AdaptadorAnimalAdoptante adaptador;
    private List<Animal> listaAnimales;
    private DAOAdopcion dao;

    public InicioAdoptanteFragment() {}

    public static InicioAdoptanteFragment newInstance(String param1, String param2) {
        InicioAdoptanteFragment fragment = new InicioAdoptanteFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ly_fragment_inicio_adoptante, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnFiltro = view.findViewById(R.id.btnFiltro);
        btnFiltro.setOnClickListener(v -> mostrarFiltros());

        RecyclerView rcvanimales = view.findViewById(R.id.faadoptantesrv_animales_favoritos);
        rcvanimales.setLayoutManager(new LinearLayoutManager(getContext()));

        dao = new DAOAdopcion(requireActivity());

        // ID del usuario logueado
        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);
        int idAdoptante = dao.obtenerIdAdoptantePorUsuario(idUsuario);

        // Lista completa de animales
        listaAnimales = dao.listarMascota();

        // Adaptador con listener para actualizar la lista si se elimina un favorito
        adaptador = new AdaptadorAnimalAdoptante(getContext(), listaAnimales, idAdoptante, animal -> {
            // Si se elimina de favoritos, se actualiza la lista
            listaAnimales.remove(animal);
            adaptador.notifyDataSetChanged();
        });

        rcvanimales.setAdapter(adaptador);
    }

    private void mostrarFiltros() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        View view = getLayoutInflater().inflate(R.layout.bottomsheet_filtro, null);
        dialog.setContentView(view);

        ImageView btnClose = view.findViewById(R.id.btnClose);
        MaterialButton btnClear = view.findViewById(R.id.btnClear);
        MaterialButton btnApply = view.findViewById(R.id.btnApply);

        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnClear.setOnClickListener(v -> {});
        btnApply.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (adaptador != null) {
            // Recorremos la lista de animales y actualizamos el estado favorito desde la BD
            SharedPreferences prefs = requireActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
            int idUsuario = prefs.getInt("id_usuario", -1);
            int idAdoptante = dao.obtenerIdAdoptantePorUsuario(idUsuario);

            for (Animal animal : listaAnimales) {
                boolean esFav = dao.esFavorito(idAdoptante, animal.getIdMascota());
                animal.setFavorito(esFav);
            }

            adaptador.notifyDataSetChanged();
        }
    }

}
