package sistemas.unc.edu.appadopcionmascotas;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class ActividadRegister extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_register);

        TextView tvLogin = findViewById(R.id.tvIrALogin);
        MaterialButtonToggleGroup toggleGroupRol = findViewById(R.id.toggleGroupRol);
        TextView txtLabelNombre = findViewById(R.id.txtLabelNombre);
        EditText etNombre = findViewById(R.id.etNombreCompleto);
        TextView txtLabelUbicacion = findViewById(R.id.txtLabelUbicacion);
        MaterialButton btnIngresarAlMapa = findViewById(R.id.btnIngresarAlMapa);


        toggleGroupRol.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnRefugio) {

                    txtLabelNombre.setText("Nombre del refugio"); // Cambiamos  el nombre para refugio
                    etNombre.setHint("Ej: Refugio Huellitas");

                    txtLabelUbicacion.setVisibility(View.VISIBLE);//mostramos la dirección
                    btnIngresarAlMapa.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.btnAdoptante) {

                    txtLabelNombre.setText("Nombre completo"); // Cambios para adoptante
                    etNombre.setHint("Ej: María García");

                    txtLabelUbicacion.setVisibility(View.GONE);   // Ocultamos la direccion
                    btnIngresarAlMapa.setVisibility(View.GONE);
                }
            }
        });

        String texto = "¿Ya tienes una cuenta? Inicia Sesión";
        SpannableString ss = new SpannableString(texto);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                finish();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.primary_color)); // Nuestro color verde
                ds.setUnderlineText(false);
                ds.setFakeBoldText(true);
            }
        };

        ss.setSpan(clickableSpan, 23, texto.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvLogin.setText(ss);
        tvLogin.setMovementMethod(LinkMovementMethod.getInstance());


    }
}