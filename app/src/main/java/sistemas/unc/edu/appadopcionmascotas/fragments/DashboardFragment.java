package sistemas.unc.edu.appadopcionmascotas.fragments;

import android.app.Activity;
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
import sistemas.unc.edu.appadopcionmascotas.UI.AdaptadorAnimal;
import sistemas.unc.edu.appadopcionmascotas.Model.Animal;
import sistemas.unc.edu.appadopcionmascotas.R;

public class DashboardFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DashboardFragment() {
        // Required empty public constructor
    }

    private List<Animal> animales = new ArrayList<>();
    private AdaptadorAnimal adaptador;
    private DAOAdopcion dao;

    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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
        return inflater.inflate(R.layout.ly_fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Activity act = requireActivity();
        dao = new DAOAdopcion(act);

        SharedPreferences prefs = act.getSharedPreferences("sesion_usuario", Activity.MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);
        if (idUsuario == -1) return;

        int idRefugio = dao.obtenerIdRefugioPorUsuario(idUsuario);
        if (idRefugio != -1) {
            animales.clear();
            animales.addAll(dao.listarAnimalesPorRefugio(idRefugio));
        }

        RecyclerView rvAnimales = view.findViewById(R.id.rvAnimales);
        rvAnimales.setLayoutManager(new LinearLayoutManager(act));
        adaptador = new AdaptadorAnimal(animales, getContext());
        rvAnimales.setAdapter(adaptador);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (dao != null && adaptador != null) {
            SharedPreferences prefs = requireActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
            int idUsuario = prefs.getInt("id_usuario", -1);
            int idRefugio = dao.obtenerIdRefugioPorUsuario(idUsuario);

            if (idRefugio != -1) {
                animales.clear();
                animales.addAll(dao.listarAnimalesPorRefugio(idRefugio));
                adaptador.notifyDataSetChanged();
            }
        }
    }
}
