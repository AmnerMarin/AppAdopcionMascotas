package sistemas.unc.edu.appadopcionmascotas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import sistemas.unc.edu.appadopcionmascotas.Firebase.DbAnimalRepositorio;
import sistemas.unc.edu.appadopcionmascotas.fragments.DashboardFragment;
import sistemas.unc.edu.appadopcionmascotas.fragments.MensajesFragment;
import sistemas.unc.edu.appadopcionmascotas.fragments.PerfilFragment;
import sistemas.unc.edu.appadopcionmascotas.fragments.SolicitudesRefugioFragment;

public class ActividadRefugio extends AppCompatActivity {

    BottomNavigationView bottom_nav_refugio;
    FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_refugio);

        bottom_nav_refugio = findViewById(R.id.bottom_nav_refugio);
        fabAdd = findViewById(R.id.fabAdd);

        // Cargar el fragment inicial
        cargarFragment(new DashboardFragment());

        // ==============================================================
        // SINCRONIZACIÓN CON FIREBASE
        // ==============================================================
        DbAnimalRepositorio repo = new DbAnimalRepositorio(this);
        Toast.makeText(this, "Sincronizando con la nube...", Toast.LENGTH_SHORT).show();

        repo.sincronizarNuevosDesdeFirebase(() -> {
            // Este código se ejecuta SOLO si se descargaron animales nuevos
            Toast.makeText(this, "¡Datos sincronizados! Actualizando...", Toast.LENGTH_SHORT).show();

            // Verificamos si el usuario sigue en el Dashboard para recargarlo
            Fragment fragmentActual = getSupportFragmentManager().findFragmentById(R.id.contenedor_refugio);
            if (fragmentActual instanceof DashboardFragment) {
                cargarFragment(new DashboardFragment());
            }
        });
        // ==============================================================

        bottom_nav_refugio.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.itemDashboard) {
                selectedFragment = new DashboardFragment();
            }
            if (id == R.id.itemSolicitudes) {
                selectedFragment = new SolicitudesRefugioFragment();
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
            Intent intent = new Intent(view.getContext(), ActividadPublicarAnimal.class);
            view.getContext().startActivity(intent);
        });
    }

    private boolean cargarFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.contenedor_refugio, fragment)
                    .commit();
        }
        return true;
    }
}