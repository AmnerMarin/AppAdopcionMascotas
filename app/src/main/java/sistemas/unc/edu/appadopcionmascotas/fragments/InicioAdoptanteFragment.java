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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
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

        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);
        int idAdoptante = dao.obtenerIdAdoptantePorUsuario(idUsuario);

        // Al iniciar, cargamos todos
        listaAnimales = dao.listarMascota();

        adaptador = new AdaptadorAnimalAdoptante(getContext(), listaAnimales, idAdoptante, animal -> {
            listaAnimales.remove(animal);
            adaptador.notifyDataSetChanged();
        });

        rcvanimales.setAdapter(adaptador);
    }

    private void mostrarFiltros() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.bottomsheet_filtro, null);
        dialog.setContentView(view);

        ImageView btnClose = view.findViewById(R.id.btnClose);
        MaterialButton btnClear = view.findViewById(R.id.btnClear);
        MaterialButton btnApply = view.findViewById(R.id.btnApply);

        ChipGroup groupType = view.findViewById(R.id.groupType);
        ChipGroup groupAge = view.findViewById(R.id.groupAge);
        ChipGroup groupSize = view.findViewById(R.id.groupSize);
        ChipGroup groupSex = view.findViewById(R.id.groupSex);

        // ====================================================
        // AQUÍ APLICAMOS LA LÓGICA INTELIGENTE A LOS 4 GRUPOS
        // ====================================================
        configurarLogicaDeTodos(groupType);
        configurarLogicaDeTodos(groupAge);
        configurarLogicaDeTodos(groupSize);
        configurarLogicaDeTodos(groupSex);

        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnClear.setOnClickListener(v -> {
            // Limpiamos y forzamos el click en "Todos" para reiniciar visualmente
            groupType.clearCheck(); seleccionarPrimerChip(groupType);
            groupAge.clearCheck(); seleccionarPrimerChip(groupAge);
            groupSize.clearCheck(); seleccionarPrimerChip(groupSize);
            groupSex.clearCheck(); seleccionarPrimerChip(groupSex);

            // Si el usuario limpia, cargamos la lista original
            listaAnimales = dao.listarMascota();
            actualizarFavoritosYMostrar();
        });

        btnApply.setOnClickListener(v -> {
            List<String> especies = getSelectedChipsTexts(groupType);
            List<String> edades = getSelectedChipsTexts(groupAge);
            List<String> tamanos = getSelectedChipsTexts(groupSize);
            List<String> sexos = getSelectedChipsTexts(groupSex);

            // Filtramos en la base de datos
            listaAnimales = dao.filtrarMascotas(especies, edades, tamanos, sexos);
            actualizarFavoritosYMostrar();

            dialog.dismiss();
        });

        dialog.show();
    }

    // ====================================================
    // NUEVO MÉTODO: Controla la lógica del botón "Todos"
    // ====================================================
    private void configurarLogicaDeTodos(ChipGroup chipGroup) {
        if (chipGroup == null || chipGroup.getChildCount() == 0) return;

        // Asumimos que "Todos" siempre es el primer Chip en tu XML (índice 0)
        Chip chipTodos = (Chip) chipGroup.getChildAt(0);

        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chipActual = (Chip) chipGroup.getChildAt(i);

            chipActual.setOnClickListener(v -> {
                boolean estaMarcado = chipActual.isChecked();

                if (chipActual == chipTodos) {
                    // Si el usuario tocó "Todos"
                    if (estaMarcado) {
                        chipGroup.clearCheck(); // Desmarca todo lo demás
                        chipTodos.setChecked(true); // Vuelve a marcar "Todos"
                    } else {
                        // No permitimos que "Todos" se desmarque a sí mismo si no hay nada más
                        chipTodos.setChecked(true);
                    }
                } else {
                    // Si el usuario tocó cualquier otro Chip (Perro, Gato, Pequeño, etc.)
                    if (estaMarcado) {
                        chipTodos.setChecked(false); // Apagamos el "Todos" automáticamente
                    } else {
                        // Si desmarcó este chip y no queda ninguno seleccionado, encendemos "Todos" por seguridad
                        if (chipGroup.getCheckedChipIds().isEmpty()) {
                            chipTodos.setChecked(true);
                        }
                    }
                }
            });
        }
    }

    private List<String> getSelectedChipsTexts(ChipGroup chipGroup) {
        List<String> selecciones = new ArrayList<>();
        if (chipGroup == null) return selecciones;

        List<Integer> checkedIds = chipGroup.getCheckedChipIds();
        for (int id : checkedIds) {
            Chip chip = chipGroup.findViewById(id);
            if (chip != null) {
                selecciones.add(chip.getText().toString());
            }
        }

        if (selecciones.isEmpty()) {
            selecciones.add("Todos");
        }
        return selecciones;
    }

    private void seleccionarPrimerChip(ChipGroup chipGroup) {
        if (chipGroup != null && chipGroup.getChildCount() > 0) {
            Chip primerChip = (Chip) chipGroup.getChildAt(0);
            primerChip.setChecked(true);
        }
    }

    private void actualizarFavoritosYMostrar() {
        if (adaptador != null && listaAnimales != null) {
            SharedPreferences prefs = requireActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
            int idUsuario = prefs.getInt("id_usuario", -1);
            int idAdoptante = dao.obtenerIdAdoptantePorUsuario(idUsuario);

            for (Animal animal : listaAnimales) {
                boolean esFav = dao.esFavorito(idAdoptante, animal.getIdMascota());
                animal.setFavorito(esFav);
            }
            adaptador.actualizarLista(listaAnimales);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        actualizarFavoritosYMostrar();
    }
}