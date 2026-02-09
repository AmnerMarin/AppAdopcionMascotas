package sistemas.unc.edu.appadopcionmascotas;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ActividadLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_login);

        TextView tvRegistro = findViewById(R.id.tvRegistrarse);
        Button btnLogin = findViewById(R.id.btnIniciarSesion);

        btnLogin.setOnClickListener(new View.OnClickListener() { //solamente para prueba luego se programara par que se abra segun el tipo de cueta
            @Override
            public void onClick(View v) {
                Intent oI = new Intent(ActividadLogin.this, ActividadRefugio.class);
                startActivity(oI);
            }
        });

        String textoCompleto = "¿No tienes una cuenta? Regístrate"; // Buscamos la posición de la palabra "Regístrate"
        SpannableString ss = new SpannableString(textoCompleto);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(ActividadLogin.this, ActividadRegister.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.primary_color)); // Color verde del logo
                ds.setUnderlineText(false); // Quitamos subrayado
                ds.setFakeBoldText(true); // Ponerlo en negrita
            }
        };

        //Se aplica el click solo a la palabra "Regístrate" (índices 23 al final)
        ss.setSpan(clickableSpan, 23, textoCompleto.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRegistro.setText(ss);
        tvRegistro.setMovementMethod(LinkMovementMethod.getInstance()); // para que el click funcione

    }
}