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

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.textfield.TextInputEditText;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Firebase.DbUsuarioRepositorio;
import sistemas.unc.edu.appadopcionmascotas.Model.Usuario;

public class ActividadLogin extends AppCompatActivity {

    DAOAdopcion daoAdopcion = new DAOAdopcion(this);

    // 1. Declarar Firebase y el Repositorio
    FirebaseAuth mAuth;
    DbUsuarioRepositorio repoUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_login);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        repoUsuarios = new DbUsuarioRepositorio(this);

        TextView tvRegistro = findViewById(R.id.tvRegistrarse);
        EditText etCorreo = findViewById(R.id.etCorreo);
        TextInputEditText etContrasena = findViewById(R.id.etContrasena);
        Button btnLogin = findViewById(R.id.btnIniciarSesion);

        // INGRESAR DIRECTO SI YA ESTA LOGUEADO
        SharedPreferences prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);
        String rol = prefs.getString("rol_usuario", "");

        if (idUsuario != -1) {
            Intent intent;
            if (rol.equals("Refugio")) {
                intent = new Intent(this, ActividadRefugio.class);
            } else {
                intent = new Intent(this, ActividadAdoptante.class);
            }
            startActivity(intent);
            finish();
        }

        // =========================================================
        // LOGUEO CON FIREBASE + SQLITE
        // =========================================================
        btnLogin.setOnClickListener(v -> {
            String correo = etCorreo.getText().toString().trim();
            String contra = etContrasena.getText().toString().trim();

            if (correo.isEmpty() || contra.isEmpty()) {
                Toast.makeText(this, "Correo y contraseña son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            btnLogin.setEnabled(false); // Bloquear botón temporalmente
            Toast.makeText(this, "Verificando credenciales...", Toast.LENGTH_SHORT).show();

            // Autenticamos primero con Firebase
            mAuth.signInWithEmailAndPassword(correo, contra)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();

                            // Verificamos si existe en el SQLite local
                            Usuario userLocal = daoAdopcion.login(correo, contra);

                            if (userLocal != null) {
                                // Si existe localmente, lo dejamos pasar directo
                                ingresarAlSistema(userLocal);
                            } else {
                                // Si NO existe localmente (creado desde la web), lo descargamos
                                Toast.makeText(this, "Descargando perfil desde la nube...", Toast.LENGTH_SHORT).show();

                                repoUsuarios.descargarUsuarioDeFirebase(uid, correo, contra, new DbUsuarioRepositorio.RegistroCallback() {
                                    @Override
                                    public void onSuccess() {
                                        // Volver a buscarlo en SQLite ahora que ya se descargó
                                        Usuario userDescargado = daoAdopcion.login(correo, contra);
                                        if (userDescargado != null) {
                                            ingresarAlSistema(userDescargado);
                                        } else {
                                            btnLogin.setEnabled(true);
                                            Toast.makeText(ActividadLogin.this, "Error interno al configurar sesión", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onError(String mensaje) {
                                        btnLogin.setEnabled(true);
                                        Toast.makeText(ActividadLogin.this, "Error al descargar perfil: " + mensaje, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } else {
                            // Falla Firebase (Contraseña mal, no existe el correo, etc.)
                            btnLogin.setEnabled(true);
                            Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // CAMBIAR EL COLOR DE REGISTER Y REDIRIGIR A REGISTER
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

    // Método de apoyo para no repetir código de guardado y cambio de pantalla
    private void ingresarAlSistema(Usuario user) {
        Toast.makeText(this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
        guardarSesion(user.getIdusuario(), user.getRol(), user.getCorreo());

        Intent intent;
        if (user.getRol().equals("Refugio")) {
            intent = new Intent(ActividadLogin.this, ActividadRefugio.class);
        } else {
            intent = new Intent(ActividadLogin.this, ActividadAdoptante.class);
        }
        startActivity(intent);
        finish();
    }

    private void guardarSesion(int id, String rol, String correo) {
        SharedPreferences preferences = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("id_usuario", id);
        editor.putString("rol_usuario", rol);
        editor.putString("correo_usuario", correo);
        editor.apply();
    }
}