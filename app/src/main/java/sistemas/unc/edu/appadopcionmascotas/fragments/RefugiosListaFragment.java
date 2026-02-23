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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import sistemas.unc.edu.appadopcionmascotas.API.RetrofitClient;
import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Refugio;
import sistemas.unc.edu.appadopcionmascotas.Model.RefugioAPI;
import sistemas.unc.edu.appadopcionmascotas.R;
import sistemas.unc.edu.appadopcionmascotas.UI.AdaptadorUbicacionRefugios;

public class RefugiosListaFragment extends Fragment {

    private RecyclerView recycler;
    private AdaptadorUbicacionRefugios adapter;
    private DAOAdopcion adopcion;
    private List<Refugio> listaGlobal;

    public RefugiosListaFragment() {}

    public static RefugiosListaFragment newInstance(String param1, String param2) {
        return new RefugiosListaFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ly_fragment_refugios_lista, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recycler = view.findViewById(R.id.rv_refugios);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adopcion = new DAOAdopcion(requireActivity());

        cargarRefugiosCombinados();
    }

    private void cargarRefugiosCombinados() {
        listaGlobal = adopcion.listarRefugio();
        adapter = new AdaptadorUbicacionRefugios(getContext(), listaGlobal);
        recycler.setAdapter(adapter);

        RetrofitClient.getApiService().obtenerRefugiosExternos().enqueue(new Callback<List<RefugioAPI>>() {
            @Override
            public void onResponse(Call<List<RefugioAPI>> call, Response<List<RefugioAPI>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    for (RefugioAPI apiItem : response.body()) {
                        Refugio refWeb = new Refugio();

                        // Aquí pasamos los datos obligando a que se llenen
                        refWeb.setNombre_refugio(apiItem.getNombre_refugio());
                        refWeb.setDireccion(apiItem.getDireccion());
                        refWeb.setTelefono(apiItem.getTelefonoAPI());
                        refWeb.setDesripcion(apiItem.getDescripcion());
                        refWeb.setCorreo(apiItem.getEmail());

                        refWeb.setEsExterno(true);
                        refWeb.setIdUsuario(-1);

                        listaGlobal.add(refWeb);
                    }

                    if (adapter != null) {
                        adapter.actualizarLista(listaGlobal);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<RefugioAPI>> call, Throwable t) {
                android.util.Log.e("API_ERROR", "Fallo al conectar con Mocki: " + t.getMessage());
            }
        });
    }
}