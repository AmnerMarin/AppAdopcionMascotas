package sistemas.unc.edu.appadopcionmascotas;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Firebase.DbAnimalRepositorio;
import sistemas.unc.edu.appadopcionmascotas.Firebase.DbRepositorioFavoritos;
import sistemas.unc.edu.appadopcionmascotas.fragments.FavoritosAdoptanteFragment;
import sistemas.unc.edu.appadopcionmascotas.fragments.InicioAdoptanteFragment;
import sistemas.unc.edu.appadopcionmascotas.fragments.MensajesFragment;
import sistemas.unc.edu.appadopcionmascotas.fragments.PerfilAdoptanteFragment;
import sistemas.unc.edu.appadopcionmascotas.fragments.RefugiosListaFragment;
import sistemas.unc.edu.appadopcionmascotas.fragments.SolicitudesAdoptanteFragment;

public class ActividadAdoptante extends AppCompatActivity {

    BottomNavigationView bottom_nav_adoptante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_adoptante);

        bottom_nav_adoptante = findViewById(R.id.bottom_nav_adoptante);

        // Cargar el fragment inicial
        cargarFragment(new InicioAdoptanteFragment());

        // ==============================================================
        // SINCRONIZACIÓN CON FIREBASE
        // ==============================================================
        DbAnimalRepositorio repo = new DbAnimalRepositorio(this);
        DbRepositorioFavoritos repoFav = new DbRepositorioFavoritos(this); // Instanciamos repo de favs

        // Obtenemos el idAdoptante local
        DAOAdopcion dao = new DAOAdopcion(this);
        int idUsuario = getSharedPreferences("sesion_usuario", MODE_PRIVATE).getInt("id_usuario", -1);
        int idAdoptanteSync = dao.obtenerIdAdoptantePorUsuario(idUsuario);

        Toast.makeText(this, "Sincronizando la nube...", Toast.LENGTH_SHORT).show();

        repo.sincronizarNuevosDesdeFirebase(() -> {
            // Cuando termine de sincronizar animales, que sincronice los favoritos
            if (idAdoptanteSync != -1) {
                repoFav.sincronizarFavoritos(idAdoptanteSync, () -> {
                    Toast.makeText(this, "¡Sincronización completada!", Toast.LENGTH_SHORT).show();

                    Fragment fragmentActual = getSupportFragmentManager().findFragmentById(R.id.contenedor_adoptante);
                    if (fragmentActual instanceof InicioAdoptanteFragment || fragmentActual instanceof FavoritosAdoptanteFragment) {
                        // Recargar el fragment actual para mostrar cambios
                        cargarFragment(fragmentActual.getClass() == InicioAdoptanteFragment.class ?
                                new InicioAdoptanteFragment() : new FavoritosAdoptanteFragment());
                    }
                });
            }
        });
        // ==============================================================

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
            else if(id == R.id.itemMensajesadoptante){
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
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.contenedor_adoptante, fragment)
                    .commit();
        }
        return true;
    }
}