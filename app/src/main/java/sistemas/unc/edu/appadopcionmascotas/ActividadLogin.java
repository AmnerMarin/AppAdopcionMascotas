package sistemas.unc.edu.appadopcionmascotas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Usuario;

public class ActividadLogin extends AppCompatActivity {

    DAOAdopcion daoAdopcion = new DAOAdopcion(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_login);

        TextView tvRegistro = findViewById(R.id.tvRegistrarse);
        EditText etCorreo = findViewById(R.id.etCorreo);
        TextInputEditText etContrasena = findViewById(R.id.etContrasena);
        Button btnLogin = findViewById(R.id.btnIniciarSesion);

        // BOTÓN OPCIONAL DE CERRAR SESIÓN (solo para pruebas)
        Button btnCerrarSesion = findViewById(R.id.btnCerrarSesion); // <-- agrega este botón en tu layout si quieres
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnClickListener(v -> {
                cerrarSesion();
                Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            });
        }

        // 3. INGRESAR DIRECTO SI YA ESTA LOGUEADO
        SharedPreferences prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);
        String rol = prefs.getString("rol_usuario", "");

        if (idUsuario != -1) {
            // Ya hay una sesión, saltamos directamente a la pantalla que corresponde
            Intent intent;
            if (rol.equals("Refugio")) {
                intent = new Intent(this, ActividadRefugio.class);
            } else {
                intent = new Intent(this, ActividadAdoptante.class);
            }
            startActivity(intent);
            finish(); // Cerramos el login
        }

        // 2. LOGUEO
        btnLogin.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString();
            String contra = etContrasena.getText().toString();

            if (correo.isEmpty() || contra.isEmpty()) {
                Toast.makeText(this, "Correo y contraseña son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            Usuario user = daoAdopcion.login(correo, contra);
            if (user != null) {
                Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show();
                guardarSesion(user.getIdusuario(), user.getRol(), user.getCorreo());

                Intent intent = null;
                if (user.getRol().equals("Refugio")) {
                    intent = new Intent(ActividadLogin.this, ActividadRefugio.class);
                } else if (user.getRol().equals("Adoptante")) {
                    intent = new Intent(ActividadLogin.this, ActividadAdoptante.class);
                }
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });

        // 1. CAMBIAR EL COLOR DE REGISTER Y REDIRIGIR A REGISTER
        String textoCompleto = "¿No tienes una cuenta? Regístrate";
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
                ds.setColor(getResources().getColor(R.color.primary_color));
                ds.setUnderlineText(false);
                ds.setFakeBoldText(true);
            }
        };
        ss.setSpan(clickableSpan, 23, textoCompleto.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRegistro.setText(ss);
        tvRegistro.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void guardarSesion(int id, String rol, String correo) {
        SharedPreferences preferences = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("id_usuario", id);
        editor.putString("rol_usuario", rol);
        editor.putString("correo_usuario", correo);
        editor.apply();
    }

    // NUEVO: Método para limpiar sesión
    private void cerrarSesion() {
        SharedPreferences preferences = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear(); // borra todo
        editor.apply();
    }
}
