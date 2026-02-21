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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        dao = new DAOAdopcion(requireActivity());

        // Inicializamos las vistas PRIMERO para que no sean null
        TextView tvPublicados = view.findViewById(R.id.txtcountPublicados);
        TextView tvMensajes = view.findViewById(R.id.txtcountMensajes);
        TextView tvAdoptados = view.findViewById(R.id.txtcountAdoptados);
        RecyclerView rvAnimales = view.findViewById(R.id.rvAnimales);

        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);

        if (idUsuario != -1) {
            int idRefugio = dao.obtenerIdRefugioPorUsuario(idUsuario);

            if (idRefugio != -1) {
                // 1. Cargar Lista
                animales.clear();
                animales.addAll(dao.listarAnimalesPorRefugio(idRefugio));

                // 2. Cargar Stats
                Map<String, Integer> stats = dao.obtenerEstadisticasDashboard(idRefugio);

                // Usar getOrDefault para estar 100% seguros de que no sea null
                tvPublicados.setText(String.valueOf(stats.getOrDefault("publicados", 0)));
                tvAdoptados.setText(String.valueOf(stats.getOrDefault("adoptados", 0)));
                tvMensajes.setText("0");

                // 3. Configurar RecyclerView
                rvAnimales.setLayoutManager(new LinearLayoutManager(getContext()));
                adaptador = new AdaptadorAnimal(animales, getContext());
                rvAnimales.setAdapter(adaptador);
            }
        }
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
