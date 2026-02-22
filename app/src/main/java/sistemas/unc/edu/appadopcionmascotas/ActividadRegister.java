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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

import kotlin.uuid.Uuid;
import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Firebase.DbUsuarioRepositorio;
import sistemas.unc.edu.appadopcionmascotas.Model.Adoptante;
import sistemas.unc.edu.appadopcionmascotas.Model.Refugio;
import sistemas.unc.edu.appadopcionmascotas.Model.Usuario;

public class ActividadRegister extends AppCompatActivity {
    double latitud = 0;
    double longitud = 0;

    DbUsuarioRepositorio dbUsuarioRepositorio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_register);

        TextView tvLogin = findViewById(R.id.tvIrALogin);
        MaterialButtonToggleGroup toggleGroupRol = findViewById(R.id.toggleGroupRol);
        TextView txtLabelNombre = findViewById(R.id.txtLabelNombreRegistro);
        EditText etNombre = findViewById(R.id.etNombreCompleto);
        TextView txtLabelApellidos = findViewById(R.id.txtLabelApellidos);
        EditText etApellidos = findViewById(R.id.etApellidos);

        //Obtenermos los datos comunes
        TextView txtLabelUbicacion = findViewById(R.id.etDireccion);
        Button abrirmapa =  findViewById(R.id.btnAbrirMapa);

        dbUsuarioRepositorio = new DbUsuarioRepositorio(this);

        abrirmapa.setOnClickListener(v ->{
            Intent intent = new Intent(this, ActividadMapas.class);
            startActivityForResult(intent, 1);
        }) ;

        TextView txtLabelDescripcion = findViewById(R.id.txtLabelDescripcion);
        EditText etDescripcion = findViewById(R.id.etDescripcionRefugio);
        MaterialButton btnAbrirMapa = findViewById(R.id.btnAbrirMapa);
        Button btnRegistrarse = findViewById(R.id.btnCrearCuenta);
        EditText etDireccion = findViewById(R.id.etDireccion);
        EditText etCorreo = findViewById(R.id.etRegistroCorreo);
        TextInputEditText etContrasena = findViewById(R.id.etRegistroPass);
        EditText etTelefono = findViewById(R.id.etRegistroDireccion);


        // Esto pasa cuando se hace click en un boton de toggle
        toggleGroupRol.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnRefugio) {

                    txtLabelNombre.setText("Nombre del refugio"); // Cambiamos para refugio
                    etNombre.setHint("Ej: Refugio Huellitas");

                    txtLabelDescripcion.setVisibility(View.VISIBLE); //mostramos el label de descripcion
                    etDescripcion.setVisibility(View.VISIBLE);
                    txtLabelApellidos.setVisibility(View.GONE); // Ocultamos el label de apellidos
                    etApellidos.setVisibility(View.GONE); // Ocultamos el campo de apellidos

                } else if (checkedId == R.id.btnAdoptante) {

                    txtLabelNombre.setText("Nombre:"); // Cambios para adoptante
                    etNombre.setHint("Ej: Garcia");

                    txtLabelDescripcion.setVisibility(View.GONE); // Ocultamos el label de descripcion
                    etDescripcion.setVisibility(View.GONE); // Ocultamos el campo de descripcion
                    txtLabelApellidos.setVisibility(View.VISIBLE); // Mostramos el label de apellidos
                    etApellidos.setVisibility(View.VISIBLE); // Mostramos el campo de apellidos
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
                ds.setColor(getResources().getColor(R.color.primary_color));
                ds.setUnderlineText(false);
                ds.setFakeBoldText(true);
            }
        };
        ss.setSpan(clickableSpan, 23, texto.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLogin.setText(ss);
        tvLogin.setMovementMethod(LinkMovementMethod.getInstance());


        DAOAdopcion dao= new DAOAdopcion(this);
        btnRegistrarse.setOnClickListener(item -> {

            String direccion = etDireccion.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();
            String contrasenia = etContrasena.getText().toString().trim();
            String telefono = etTelefono.getText().toString().trim();

            if (!validarDatosComunes(correo, contrasenia, direccion, telefono)) {
                return;
            }

            int idSeleccionado = toggleGroupRol.getCheckedButtonId();
            boolean resultado = false;

            if (idSeleccionado == R.id.btnRefugio) {

                String nombreRefugio = etNombre.getText().toString().trim();
                String descripcion = etDescripcion.getText().toString().trim();

                if (!validarRefugio(nombreRefugio, descripcion)) {
                    return;
                }

                Usuario user = new Usuario(correo, contrasenia, "Refugio");
                long idGenerado = dao.insertarUsuario(user);

                if (idGenerado != -1) {

                    int IdUsuario = (int) idGenerado;

                    Refugio ref = new Refugio(
                            IdUsuario,
                            nombreRefugio,
                            descripcion,
                            direccion,
                            telefono,
                            latitud,
                            longitud
                    );

                    resultado = dao.insertarRefugio(ref);
                    dbUsuarioRepositorio.registrarRefugio(user, ref, new DbUsuarioRepositorio.RegistroCallback() {
                        @Override
                        public void onSuccess() {

                            Toast.makeText(ActividadRegister.this, "¡Registro exitoso!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ActividadRegister.this, ActividadLogin.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(String mensaje) {

                            Toast.makeText(ActividadRegister.this, "Error: El correo ya existe o hubo un problema técnico", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            } else {

                String nombreAdoptante = etNombre.getText().toString().trim();
                String apellidos = etApellidos.getText().toString().trim();

                if (!validarAdoptante(nombreAdoptante, apellidos)) {
                    return;
                }

                Usuario user = new Usuario(correo, contrasenia, "Adoptante");
                long idGenerado = dao.insertarUsuario(user);

                if (idGenerado != -1) {

                    int IdUsuario = (int) idGenerado;

                    Adoptante adoptante = new Adoptante(
                            IdUsuario,
                            nombreAdoptante,
                            apellidos,
                            telefono,
                            direccion
                    );

                    resultado = dao.insertarAdoptante(adoptante);
                    dbUsuarioRepositorio.registrarAdoptante(user, adoptante, new DbUsuarioRepositorio.RegistroCallback() {
                        @Override
                        public void onSuccess() {

                            Toast.makeText(ActividadRegister.this, "¡Registro exitoso!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ActividadRegister.this, ActividadLogin.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(String mensaje) {

                            Toast.makeText(ActividadRegister.this, "Error: El correo ya existe o hubo un problema técnico", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }

            if (resultado) {
                Toast.makeText(this, "¡Registro exitoso! Bienvenido", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(ActividadRegister.this, ActividadLogin.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(this, "Error: El correo ya existe o hubo un problema técnico", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            latitud = data.getDoubleExtra("lat", 0);
            longitud = data.getDoubleExtra("lng", 0);
            String direccion = data.getStringExtra("direccion");

            EditText etDireccion = findViewById(R.id.etDireccion);
            etDireccion.setText(direccion);
        }
    }

    // ================= VALIDAR DATOS COMUNES =================
    private boolean validarDatosComunes(String correo, String contrasenia, String direccion, String telefono) {

        if (correo.isEmpty()) {
            Toast.makeText(this, "El correo es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (contrasenia.isEmpty()) {
            Toast.makeText(this, "La contraseña es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (contrasenia.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener mínimo 6 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (direccion.isEmpty()) {
            Toast.makeText(this, "La dirección es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (telefono.isEmpty()) {
            Toast.makeText(this, "El teléfono es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!telefono.matches("[0-9+ ]+")) {
            Toast.makeText(this, "Teléfono inválido", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    // ================= VALIDAR REFUGIO =================
    private boolean validarRefugio(String nombreRefugio, String descripcion) {

        if (nombreRefugio.isEmpty()) {
            Toast.makeText(this, "El nombre del refugio es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (nombreRefugio.length() < 3) {
            Toast.makeText(this, "Nombre del refugio muy corto", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (descripcion.isEmpty()) {
            Toast.makeText(this, "La descripción es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (descripcion.length() < 10) {
            Toast.makeText(this, "La descripción debe tener al menos 10 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    // ================= VALIDAR ADOPTANTE =================
    private boolean validarAdoptante(String nombre, String apellidos) {

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (apellidos.isEmpty()) {
            Toast.makeText(this, "Los apellidos son obligatorios", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            Toast.makeText(this, "Nombre inválido (solo letras)", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!apellidos.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            Toast.makeText(this, "Apellidos inválidos (solo letras)", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
