package sistemas.unc.edu.appadopcionmascotas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import sistemas.unc.edu.appadopcionmascotas.fragments.DashboardFragment;
import sistemas.unc.edu.appadopcionmascotas.fragments.FavoritosAdoptanteFragment;
import sistemas.unc.edu.appadopcionmascotas.fragments.InicioAdoptanteFragment;
import sistemas.unc.edu.appadopcionmascotas.fragments.MensajesFragment;
import sistemas.unc.edu.appadopcionmascotas.fragments.PerfilAdoptanteFragment;
import sistemas.unc.edu.appadopcionmascotas.fragments.PerfilFragment;
import sistemas.unc.edu.appadopcionmascotas.fragments.RefugiosListaFragment;
import sistemas.unc.edu.appadopcionmascotas.fragments.SolicitudesAdoptanteFragment;

public class ActividadAdoptante extends AppCompatActivity {


    BottomNavigationView bottom_nav_adoptante;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_adoptante);

        bottom_nav_adoptante = findViewById(R.id.bottom_nav_adoptante);

        cargarFragment(new InicioAdoptanteFragment());

        bottom_nav_adoptante.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.iteminicioadoptante) {
                selectedFragment = new InicioAdoptanteFragment();
            }
            else if(id == R.id.itemSolicitudes){
                selectedFragment = new SolicitudesAdoptanteFragment();
            }
            else if(id == R.id.itemfavoritosdoptante){
                selectedFragment = new FavoritosAdoptanteFragment();
            }
            else if(id == R.id.itemrefugiosdoptante){
                selectedFragment = new RefugiosListaFragment();
            }
            else if(id == R.id.itemMensajesadoptante){ //este fragment va a ser comun para adoptante y refugios
                // Asegúrate de haber creado esta clase Fragment o marcará error en rojo
                selectedFragment = new MensajesFragment();
            }
            else if(id == R.id.itemPerfilAdoptante){
                selectedFragment = new PerfilAdoptanteFragment();
            }
            return cargarFragment(selectedFragment);
        });
    }


    private boolean cargarFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) //una animacion
                    .replace(R.id.contenedor_adoptante, fragment)
                    .commit();
        }
        return true;
    }
}