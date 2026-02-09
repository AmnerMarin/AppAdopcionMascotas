package sistemas.unc.edu.appadopcionmascotas;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import sistemas.unc.edu.appadopcionmascotas.fragments.DashboardFragment;
import sistemas.unc.edu.appadopcionmascotas.fragments.MensajesFragment;
import sistemas.unc.edu.appadopcionmascotas.fragments.PerfilFragment;

public class ActividadRefugio extends AppCompatActivity {

    BottomNavigationView bottom_nav_refugio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_refugio);

        bottom_nav_refugio = findViewById(R.id.bottom_nav_refugio);

        cargarFragment(new DashboardFragment());
        bottom_nav_refugio.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if(id == R.id.itemDashboard){
                selectedFragment = new DashboardFragment();
            }
            else if(id == R.id.itemMensajes){
                selectedFragment = new MensajesFragment();
            }
            else if(id == R.id.itemPerfil){
                selectedFragment = new PerfilFragment();
            }
            return cargarFragment(selectedFragment);
        });
    }

    private boolean cargarFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) //una animacion
                    .replace(R.id.contenedor_refugio, fragment)
                    .commit();
        }
        return true;
    }
}