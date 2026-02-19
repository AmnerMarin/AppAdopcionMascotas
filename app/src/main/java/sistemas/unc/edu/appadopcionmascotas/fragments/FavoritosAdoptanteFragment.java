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

import java.util.ArrayList;
import java.util.List;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Animal;
import sistemas.unc.edu.appadopcionmascotas.R;
import sistemas.unc.edu.appadopcionmascotas.UI.AdaptadorAnimalAdoptante;
//import sistemas.unc.edu.appadopcionmascotas.UI.AdaptadorAnimalAdoptante;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoritosAdoptanteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritosAdoptanteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FavoritosAdoptanteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoritosAdoptanteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoritosAdoptanteFragment newInstance(String param1, String param2) {
        FavoritosAdoptanteFragment fragment = new FavoritosAdoptanteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ly_fragment_favoritos_adoptante, container, false);
    }

    private AdaptadorAnimalAdoptante adaptador;
    private List<Animal> listaFavoritos;
    private DAOAdopcion dao;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvFavoritos = view.findViewById(R.id.rv_animales_favoritos);
        rvFavoritos.setLayoutManager(new LinearLayoutManager(getContext()));

        rvFavoritos = view.findViewById(R.id.rv_animales_favoritos);
        rvFavoritos.setLayoutManager(new LinearLayoutManager(getContext()));
        dao = new DAOAdopcion(requireActivity());

        // 2. Obtener el ID del usuario logueado
        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);

        if (idUsuario != -1) {
            // Primero obtenemos el id_adoptante real usando el id_usuario
            int idAdoptante = dao.obtenerIdAdoptantePorUsuario(idUsuario);

            // 3. Cargar la lista desde el DAO (Base de Datos)
            listaFavoritos = dao.obtenerFavoritosPorAdoptante(idAdoptante);

            // FORZAR EL ESTADO DE FAVORITO EN EL MODELO
            for (Animal a : listaFavoritos) {
                a.setFavorito(true);
            }
            // 4. Configurar Adaptador
            adaptador = new AdaptadorAnimalAdoptante(getContext(), listaFavoritos, idAdoptante, animal -> {
                // 1. Eliminar de la lista local
                listaFavoritos.remove(animal);
                // 2. Notificar al adaptador que cambió la lista
                adaptador.notifyDataSetChanged();
            });

            rvFavoritos.setAdapter(adaptador);

        }
    }
}