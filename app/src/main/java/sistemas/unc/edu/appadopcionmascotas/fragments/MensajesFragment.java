package sistemas.unc.edu.appadopcionmascotas.fragments;

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

import sistemas.unc.edu.appadopcionmascotas.UI.AdaptadorMensaje;
import sistemas.unc.edu.appadopcionmascotas.Model.Mensaje;
import sistemas.unc.edu.appadopcionmascotas.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MensajesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MensajesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MensajesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MensajesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MensajesFragment newInstance(String param1, String param2) {
        MensajesFragment fragment = new MensajesFragment();
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
        return inflater.inflate(R.layout.ly_fragment_mensajes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvMensajes = view.findViewById(R.id.rvMensaje);
        rvMensajes.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Mensaje> mensaje = new ArrayList<>();
        mensaje.add(new Mensaje("María García", "maria@email.com", "31/1/2026", "Luna", "Hola, estoy interesada en adoptar a Luna. ¿Podría visitarla este fin de semana?"));
        mensaje.add(new Mensaje("Juan Pérez", "juan@email.com", "28/1/2026", "Max", "Hola, estoy interesado en adoptar a Max. ¿Podría visitarlo este fin de semana?"));
        mensaje.add(new Mensaje("Ana López", "ana@email.com", "25/1/2026", "Bella", "Hola, estoy interesada en adoptar a Bella. ¿Podría visitarla este fin de semana?" ));

        AdaptadorMensaje adaptadorMensaje = new AdaptadorMensaje(mensaje);
        rvMensajes.setAdapter(adaptadorMensaje);
    }
}