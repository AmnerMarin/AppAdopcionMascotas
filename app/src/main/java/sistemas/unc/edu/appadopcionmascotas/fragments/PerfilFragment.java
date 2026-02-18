package sistemas.unc.edu.appadopcionmascotas.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import sistemas.unc.edu.appadopcionmascotas.ActividadLogin;
import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Refugio;
import sistemas.unc.edu.appadopcionmascotas.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
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
        return inflater.inflate(R.layout.ly_fragment_perfil, container, false);
    }


    private DAOAdopcion dao;
    private int idRefugio;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);

        // 2. UNA SOLA LECTURA DE SESIÓN
        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);
        String correo = prefs.getString("correo_usuario", "");

        if (idUsuario != -1) {
            // 3. USAR LOS DAOS
            DAOAdopcion daoAdopcion= new DAOAdopcion(requireActivity());

            // Obtener datos del Refugio y Estadísticas
            idRefugio = daoAdopcion.obtenerIdRefugioPorUsuario(idUsuario);
//            Refugio miRefugio = daoAdopcion.obtenerPerfilRefugio(idUsuario);
//            // Llenar datos del Perfil
//            if (miRefugio != null) {
//                txtNombrePerfil.setText(miRefugio.getNombre_refugio());
//                txtDireccionPerfil.setText(miRefugio.getDireccion());
//                txtDireccionenInfo.setText(miRefugio.getDireccion());
//                txtTelefonoPerfil.setText(miRefugio.getTelefono());
//                txtEmailPerfil.setText(correo);
//            }

//            // Llenar Estadísticas
//            if (idRefugio != -1) {
//                Map<String, Integer> stats = daoMascotas.obtenerEstadisticasDashboard(idRefugio);
//                tvPublicados.setText(String.valueOf(stats.getOrDefault("publicados", 0)));
//                tvAdoptados.setText(String.valueOf(stats.getOrDefault("adoptados", 0)));
//                tvMensajes.setText("0");
//            }
        }

        // 4. LISTENERS (Usando Lambdas para ahorrar espacio)
//        btnGestionarPublicac.setOnClickListener(v -> cambiarTab(R.id.itemDashboard));
//        btnMensajesRecibidos.setOnClickListener(v -> cambiarTab(R.id.itemMensajes));
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    private void cerrarSesion() {
        // 1. Abrir las preferencias donde guardamos el ID y el Rol
        SharedPreferences preferences = getActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // 2. Limpiar todos los datos guardados
        editor.clear();
        editor.apply(); // O commit() para hacerlo instantáneo

        // 3. Mostrar un mensaje al usuario
        Toast.makeText(getActivity(), "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show();

        // 4. Redirigir al Login y limpiar el historial de pantallas
        Intent intent = new Intent(getActivity(), ActividadLogin.class);
        // Estas banderas sirven para que el usuario no pueda volver al perfil al dar "atrás"
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}