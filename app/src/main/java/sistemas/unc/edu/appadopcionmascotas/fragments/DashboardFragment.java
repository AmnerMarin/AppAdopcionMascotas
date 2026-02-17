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

import sistemas.unc.edu.appadopcionmascotas.UI.AdaptadorAnimal;
import sistemas.unc.edu.appadopcionmascotas.Model.Animal;
import sistemas.unc.edu.appadopcionmascotas.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.ly_fragment_dashboard, container, false);
    }

    //lo creamos

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rvAnimales = view.findViewById(R.id.rvAnimales);
        rvAnimales.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Animal> listaAnimales = new ArrayList<>();

        listaAnimales.add(new Animal(1,"Luna", "Perro", "Golden Retriever",20, "3 años", "Hembra", null, null, null, null));
        listaAnimales.add(new Animal(2,"Max", "Bulldog Francés", "Perro", 10,"1 año", "Macho", null, null,null,null));
        listaAnimales.add(new Animal(3,"Michi", "Siamés", "Gato",15, "2 años", "Hembra", null, null, null,null));
        listaAnimales.add(new Animal(4,"Rocky", "Pastor Alemán", "Perro",12, "4 años", "Macho", null, null, null,null));
        listaAnimales.add(new Animal(5,"Nala", "Mestizo", "Gato", 14, "5 meses", "Hembra", null, null, null,null));
        listaAnimales.add(new Animal(6,"Toby", "Beagle", "Perro", 18,"2 años", "Macho", null, null, null,null));

        AdaptadorAnimal adaptadorAnimal = new AdaptadorAnimal(listaAnimales);
        rvAnimales.setAdapter(adaptadorAnimal);



    }
}