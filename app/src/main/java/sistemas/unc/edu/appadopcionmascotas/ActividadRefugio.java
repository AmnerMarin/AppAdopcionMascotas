package sistemas.unc.edu.appadopcionmascotas;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import sistemas.unc.edu.appadopcionmascotas.fragments.DashboardFragment;
import sistemas.unc.edu.appadopcionmascotas.fragments.MensajesFragment;
import sistemas.unc.edu.appadopcionmascotas.fragments.PerfilFragment;

public class ActividadRefugio extends AppCompatActivity {

    BottomNavigationView bottom_nav_refugio;
    FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_refugio);

        bottom_nav_refugio = findViewById(R.id.bottom_nav_refugio);
        fabAdd = findViewById(R.id.fabAdd);


        cargarFragment(new DashboardFragment());
        bottom_nav_refugio.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.itemDashboard) {
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

        fabAdd.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ActividadAgregarAnimal.class);
            view.getContext().startActivity(intent);

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