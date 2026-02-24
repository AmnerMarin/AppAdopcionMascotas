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

import java.util.List;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Firebase.DbRepositorioAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Solicitud;
import sistemas.unc.edu.appadopcionmascotas.R;
import sistemas.unc.edu.appadopcionmascotas.UI.AdaptadorSolicitudAdoptante;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SolicitudesAdoptanteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SolicitudesAdoptanteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView rvSolicitudes;
    private AdaptadorSolicitudAdoptante adaptador;
    private DAOAdopcion dao;
    private int idAdoptante = -1;

    public SolicitudesAdoptanteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SolicitudesAdoptanteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SolicitudesAdoptanteFragment newInstance(String param1, String param2) {
        SolicitudesAdoptanteFragment fragment = new SolicitudesAdoptanteFragment();
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
        return inflater.inflate(R.layout.ly_fragment_solicitudes_adoptante, container, false);
    }

    //SOBRE ESCRITURA DEL METODO ONVIEWCREATED
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvSolicitudes = view.findViewById(R.id.rvMisSolicitudesAdoptante);
        rvSolicitudes.setLayoutManager(new LinearLayoutManager(getContext()));
        dao = new DAOAdopcion(requireActivity());

        obtenerIdAdoptante();

        // --- NUEVO: Sincronizar con Firebase al entrar ---
        if (idAdoptante != -1) {
            DbRepositorioAdopcion repo = new DbRepositorioAdopcion(getContext());
            repo.sincronizarSolicitudesAdoptante(idAdoptante, () -> {
                // Este código se ejecuta cuando Firebase termina de bajar datos a SQLite
                cargarSolicitudes();
            });
        }

        cargarSolicitudes(); // Carga inicial (datos que ya estaban en SQLite)
    }
    private void obtenerIdAdoptante() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);

        if (idUsuario != -1) {
            idAdoptante = dao.obtenerIdAdoptantePorUsuario(idUsuario);
        }
    }

    private void cargarSolicitudes() {
        if (idAdoptante != -1) {
            // Llamamos al método del DAO que creamos en el paso anterior
            List<Solicitud> lista = dao.obtenerMisSolicitudes(idAdoptante);

            // Configuramos el adaptador con la lista obtenida
            adaptador = new AdaptadorSolicitudAdoptante(getContext(), lista);
            rvSolicitudes.setAdapter(adaptador);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarSolicitudes();
    }
}