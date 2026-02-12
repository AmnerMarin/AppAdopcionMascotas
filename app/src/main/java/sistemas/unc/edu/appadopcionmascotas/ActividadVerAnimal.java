package sistemas.unc.edu.appadopcionmascotas;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class ActividadVerAnimal extends AppCompatActivity {
    private boolean esGuardado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_ver_animal);
        MaterialButton btnGuardar = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esGuardado = !esGuardado;

                if (esGuardado) {
                    btnGuardar.setText("Guardado");

                    btnGuardar.setIconResource(R.drawable.corazon_lleno);

                    btnGuardar.setIconTint(ColorStateList.valueOf(
                            ContextCompat.getColor(getApplicationContext(), R.color.white)));

                    btnGuardar.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(getApplicationContext(), R.color.orange_adopted)));
                    btnGuardar.setStrokeColor(ColorStateList.valueOf(
                            ContextCompat.getColor(getApplicationContext(), R.color.orange_adopted)));

                    btnGuardar.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

                } else {
                    btnGuardar.setText("Guardar");

                    btnGuardar.setIconResource(R.drawable.corazon);

                    btnGuardar.setIconTint(ColorStateList.valueOf(
                            ContextCompat.getColor(getApplicationContext(), R.color.text_dark)));


                    btnGuardar.setBackgroundTintList(ContextCompat.getColorStateList(
                            getApplicationContext(), R.color.btn_guardar_selector));

                    btnGuardar.setStrokeColor(ColorStateList.valueOf(
                            ContextCompat.getColor(getApplicationContext(), R.color.gray)));

                    btnGuardar.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_dark));
                }
            }
        });
    }
}