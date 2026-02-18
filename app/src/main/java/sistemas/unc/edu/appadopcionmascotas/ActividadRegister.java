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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Adoptante;
import sistemas.unc.edu.appadopcionmascotas.Model.Refugio;
import sistemas.unc.edu.appadopcionmascotas.Model.Usuario;

public class ActividadRegister extends AppCompatActivity {

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
        TextView txtLabelUbicacion = findViewById(R.id.txtLabelUbicacion);
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

                    txtLabelUbicacion.setVisibility(View.VISIBLE);//mostramos el label de ubicación
                    btnAbrirMapa.setVisibility(View.VISIBLE); //mostramos el boton de abrir mapa
                    txtLabelDescripcion.setVisibility(View.VISIBLE); //mostramos el label de descripcion
                    etDescripcion.setVisibility(View.VISIBLE);
                    txtLabelApellidos.setVisibility(View.GONE); // Ocultamos el label de apellidos
                    etApellidos.setVisibility(View.GONE); // Ocultamos el campo de apellidos

                } else if (checkedId == R.id.btnAdoptante) {

                    txtLabelNombre.setText("Nombre:"); // Cambios para adoptante
                    etNombre.setHint("Ej: Garcia");

                    txtLabelUbicacion.setVisibility(View.GONE);   // Ocultamos el label de ubicación
                    btnAbrirMapa.setVisibility(View.GONE); // ocultamos el boton de abrir mapa
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
        btnRegistrarse.setOnClickListener(item->{

            //Datos comunes
            String direccion = etDireccion.getText().toString();
            String correo = etCorreo.getText().toString();
            String contrasenia = etContrasena.getText().toString();
            String telefono = etTelefono.getText().toString();

            // Validar datos comunes
            if (correo.isEmpty() || contrasenia.isEmpty()|| direccion.isEmpty() || telefono.isEmpty()) {
                Toast.makeText(this, "Correo y contraseña son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Preguntar: ¿Qué botón está seleccionado en el ToggleGroup?
            int idSeleccionado = toggleGroupRol.getCheckedButtonId();
            boolean resultado = false;

            if (idSeleccionado == R.id.btnRefugio) {
                //---   FLUJO DE REFUGIO ----
                String nombreRefugio = etNombre.getText().toString();
                String descripcion = etDescripcion.getText().toString();
                //ubicacion proximamente

                if (nombreRefugio.isEmpty() || descripcion.isEmpty()) {
                    Toast.makeText(this, "Nombre del refugio y descripcion son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Crear los obejetos
                Usuario user = new Usuario(correo, contrasenia, "Refugio");

                long idGenerado = dao.insertarUsuario(user);

                if(idGenerado != -1){

                    int IdUsuario = (int) idGenerado;

                    Refugio ref = new Refugio(IdUsuario, nombreRefugio, descripcion, direccion, telefono, 0,0);

                    resultado = dao.insertarRefugio(ref);

                }
            }else{
                //---   FLUJO DE ADOPTANTE ----
                String nombreAdoptante = etNombre.getText().toString();
                String apellidos = etApellidos.getText().toString();

                if (nombreAdoptante.isEmpty() || apellidos.isEmpty()) {
                    Toast.makeText(this, "Nombre y apellidos son obligatorios", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Crear los objetos
                Usuario user = new Usuario(correo, contrasenia, "Adoptante");

                long idGenerado = dao.insertarUsuario(user);

                if(idGenerado != -1){

                    int IdUsuario = (int) idGenerado;

                    Adoptante adoptante = new Adoptante(IdUsuario, nombreAdoptante, apellidos, telefono, direccion);

                    resultado = dao.insertarAdoptante(adoptante);

                }
            }

            // 3. Respuesta final al usuario
            if (resultado) {
                Toast.makeText(this, "¡Registro exitoso! Bienvenido", Toast.LENGTH_LONG).show();

                // Ir al Login o al Dashboard directamente
                Intent intent = new Intent(ActividadRegister.this, ActividadLogin.class);
                startActivity(intent);

                ActividadRegister.this.finish(); // Cerramos el registro para que no pueda volver atrás

            } else {
                Toast.makeText(this, "Error: El correo ya existe o hubo un problema técnico", Toast.LENGTH_SHORT).show();
            }

        });
    }
}
