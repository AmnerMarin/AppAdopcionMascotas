package sistemas.unc.edu.appadopcionmascotas.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sistemas.unc.edu.appadopcionmascotas.ActividadLogin;
import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Animal;
import sistemas.unc.edu.appadopcionmascotas.Model.Refugio;
import sistemas.unc.edu.appadopcionmascotas.R;
import sistemas.unc.edu.appadopcionmascotas.UI.AdaptadorAnimal;

public class PerfilFragment extends Fragment {

    private TextView txtNombreRefugio, txtDireccion;
    private TextView txtPublicados, txtMensajes, txtAdoptados;
    private RecyclerView rvAnimales;
    private MaterialButton btnVerTodos, btnCerrarSesion;

    // includes
    private TextView txtEmail, txtTelefono, txtDireccionInfo;

    private DAOAdopcion dao;
    private AdaptadorAnimal adaptador;
    private int idRefugio;

    public PerfilFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.ly_fragment_perfil, container, false);

        dao = new DAOAdopcion(requireActivity());

        inicializarVistas(view);
        cargarDatosRefugio();
        configurarRecycler();
        configurarBotones(view);

        return view;
    }

    private void inicializarVistas(View view) {

        txtNombreRefugio = view.findViewById(R.id.txtNombreRefugio);
        txtDireccion = view.findViewById(R.id.txtDireccion);

        txtPublicados = view.findViewById(R.id.txt_cantidadpublicadosinfo);
        txtMensajes = view.findViewById(R.id.txt_mensajesinfo);
        txtAdoptados = view.findViewById(R.id.txt_adoptadosinfo);

        rvAnimales = view.findViewById(R.id.rvAnimales);
        btnVerTodos = view.findViewById(R.id.btnVerTodos);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);

        // includes (IMPORTANTE: se llaman igual que los id dentro del layout incluido)
        txtEmail = view.findViewById(R.id.txtValueEmail);
        txtTelefono = view.findViewById(R.id.txtValuePhone);
        txtDireccionInfo = view.findViewById(R.id.txtValueAddress);
    }

    private void cargarDatosRefugio() {

        // 2. UNA SOLA LECTURA DE SESIÓN
        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);
        String correo = prefs.getString("correo_usuario", "");

        if (idUsuario != -1) {
            // 3. USAR LOS DAOS
            DAOAdopcion daoMascotas = new DAOAdopcion(requireActivity());

            // Obtener datos del Refugio y Estadísticas
            idRefugio = daoMascotas.obtenerIdRefugioPorUsuario(idUsuario);
            Refugio miRefugio = daoMascotas.obtenerPerfilRefugio(idUsuario);

            if (miRefugio != null) {

                txtNombreRefugio.setText(miRefugio.getNombre_refugio());
                txtDireccion.setText(miRefugio.getDireccion());

                txtDireccionInfo.setText(miRefugio.getDireccion());
                txtTelefono.setText(miRefugio.getTelefono());
                txtEmail.setText(correo);
            }

            // ===============================
            // LLENAR ESTADÍSTICAS
            // ===============================
            if (idRefugio != -1) {

                Map<String, Integer> stats =
                        dao.obtenerEstadisticasDashboard(idRefugio);

                txtPublicados.setText(
                        String.valueOf(stats.getOrDefault("publicados", 0)));

                txtAdoptados.setText(
                        String.valueOf(stats.getOrDefault("adoptados", 0)));

                txtMensajes.setText("0");
            }
        }
    }

    private void configurarRecycler () {
        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);
        idRefugio = dao.obtenerIdRefugioPorUsuario(idUsuario);

        List<Animal> todos = dao.listarAnimalesPorRefugio(idRefugio);
        List<Animal> primerosTres = new ArrayList<>();

        for (int i = 0; i < todos.size() && i < 3; i++) {
            primerosTres.add(todos.get(i));
        }

        adaptador = new AdaptadorAnimal(primerosTres, getContext());

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false);

        rvAnimales.setLayoutManager(layoutManager);
        rvAnimales.setAdapter(adaptador);

        btnVerTodos.setText("Ver todos (" + todos.size() + ")");
    }


    private void configurarBotones (View view){

        // Buscamos el menú de la actividad una sola vez
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                requireActivity().findViewById(R.id.bottom_nav_refugio); // <--- USA EL ID DE TU BOTTOMNAV

        // GESTIONAR / DASHBOARD
        view.findViewById(R.id.rowGestionar).setOnClickListener(v -> {
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.itemDashboard); // ID en tu menu.xml
            }
        });

        // MENSAJES
        view.findViewById(R.id.rowMensajes).setOnClickListener(v -> {
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.itemMensajes); // ID en tu menu.xml
            }
        });

        // VER TODOS (Dashboard)
        btnVerTodos.setOnClickListener(v -> {
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.itemDashboard);
            }
        });

        btnCerrarSesion.setOnClickListener(v -> {
            SharedPreferences prefs = requireActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(getContext(), ActividadLogin.class));
        });
    }
}