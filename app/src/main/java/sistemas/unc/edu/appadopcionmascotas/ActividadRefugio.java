package sistemas.unc.edu.appadopcionmascotas;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ActividadRefugio extends AppCompatActivity {

    BottomNavigationView bottom_nav_refugio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_refugio);

        bottom_nav_refugio = findViewById(R.id.bottom_nav_refugio);


    }

}