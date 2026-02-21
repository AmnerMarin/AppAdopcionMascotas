package sistemas.unc.edu.appadopcionmascotas;

import android.app.ComponentCaller;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Animal;

public class ActividadVerAnimal extends AppCompatActivity {
    private boolean esGuardado = false;
    private ImageView imgFotoMascota;

    private TextView txtNombre, txtRaza, txtEdad, txtEstado, txtHistoria, txtsexo, txttamano;
    private TextView txtRefugioNombre, txtRefugioDireccion, txtRefugioTelefono;
    private Chip chiptemp1, chiptemp2, chiptem3, chipTipo;
    private ImageButton btnFavorito;
    private MaterialButton btnBack, btnContactar, btnSolicitarAdopcion, btnGuardar;
    private DAOAdopcion daoMascotas;
    private Animal animalActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_ver_animal);

        // 1. Mapeo de vistas
        imgFotoMascota = findViewById(R.id.imgFotoMascota);

        txtNombre = findViewById(R.id.txtNombreAnimal);
        txtRaza = findViewById(R.id.txtRazaAnimal);
        txtEdad = findViewById(R.id.txtEdad);
        txtEstado = findViewById(R.id.txtEstado);
        txtHistoria = findViewById(R.id.txtHistoria);
        txtsexo = findViewById(R.id.txtSexo);
        txttamano =  findViewById(R.id.txtTamano);
        chipTipo = findViewById(R.id.chipTipoAnimal);
        chiptem3 = findViewById(R.id.chiptem3);
        chiptemp1 =  findViewById(R.id.chiptem1);
        chiptemp2 = findViewById(R.id.chiptem2);

        btnBack = findViewById(R.id.btnBack);
        btnContactar = findViewById(R.id.btnContactar);
        btnSolicitarAdopcion = findViewById(R.id.btnSolicitarAdopcion);

        txtRefugioNombre = findViewById(R.id.txtRefugioNombre);
        txtRefugioDireccion = findViewById(R.id.txtRefugioDireccion);
        txtRefugioTelefono = findViewById(R.id.txtRefugioTelefono);

        daoMascotas = new DAOAdopcion(this);

        btnGuardar = findViewById(R.id.btnGuardar);


        // --------------------- En onCreate ---------------------
        int idAnimal = getIntent().getIntExtra("ID_ANIMAL", -1);
        animalActual = daoMascotas.obtenerDetalleAnimalConRefugio(idAnimal);

        // Obtener adoptante
        SharedPreferences prefs = getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);
        int idAdoptante = daoMascotas.obtenerIdAdoptantePorUsuario(idUsuario);

        // Actualizar estado inicial del botón según base de datos
        if (animalActual != null && idAdoptante != -1) {
            boolean esFavorito = daoMascotas.esFavorito(idAdoptante, animalActual.getIdMascota());
            actualizarIconoFavorito(esFavorito);
        }

        // Listener del botón Guardar/Favorito
        btnGuardar.setOnClickListener(v -> {
            if (animalActual != null && idAdoptante != -1) {
                int idMascota = animalActual.getIdMascota();
                boolean esAhoraFavorito;

                if (daoMascotas.esFavorito(idAdoptante, idMascota)) {
                    // Si ya es favorito, eliminar
                    if (daoMascotas.eliminarFavorito(idAdoptante, idMascota)) {
                        esAhoraFavorito = false;
                        Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
                    } else return; // error al eliminar
                } else {
                    // Si no es favorito, agregar
                    if (daoMascotas.agregarFavorito(idAdoptante, idMascota)) {
                        esAhoraFavorito = true;
                        Toast.makeText(this, "Añadido a favoritos", Toast.LENGTH_SHORT).show();
                    } else return; // error al agregar
                }

                // Actualizar visual del botón
                actualizarIconoFavorito(esAhoraFavorito);
            }
        });

        MaterialButton btnContactar = findViewById(R.id.btnContactar);
        btnContactar.setOnClickListener(e->{
                Intent i = new Intent(this, ActividadContactarRefugio.class);
                startActivity(i);
        });


        animalActual = daoMascotas.obtenerDetalleAnimalConRefugio(idAnimal);

        if (animalActual != null) {
            // Datos Mascota
            txtNombre.setText(animalActual.getNombre());
            txtRaza.setText(animalActual.getRaza());
            txtEdad.setText(animalActual.getEdad());
            txtEstado.setText(animalActual.getEstado());
            txtHistoria.setText(animalActual.getHistoria());
            txtsexo.setText(animalActual.getSexo());
            txttamano.setText(animalActual.getTamano());
            chipTipo.setText(animalActual.getEspecie());

            // Temperamento: separar por coma y asignar a los 3 chips
            String temp = animalActual.getTemperamento();
            if (temp != null && !temp.isEmpty()) {
                String[] tempArray = temp.split(",");

                // Limpiar espacios y asignar a chips
                if (tempArray.length > 0) chiptemp1.setText(tempArray[0].trim());
                if (tempArray.length > 1) chiptemp2.setText(tempArray[1].trim());
                if (tempArray.length > 2) chiptem3.setText(tempArray[2].trim());
            }

            // Datos Refugio
            txtRefugioNombre.setText(animalActual.getNombreRefugio());
            txtRefugioDireccion.setText(animalActual.getDireccionRefugio());
            txtRefugioTelefono.setText(animalActual.getTelefonoRefugio());

            // Foto
            if (animalActual.getFoto() != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(animalActual.getFoto(), 0, animalActual.getFoto().length);
                imgFotoMascota.setImageBitmap(bmp);
            }
        }


        // Configurar Botón Solicitar
        btnSolicitarAdopcion.setOnClickListener(v -> {
            if (daoMascotas.existeSolicitud(idAdoptante, animalActual.getIdMascota())) {
                Toast.makeText(this, "Ya tienes una solicitud pendiente", Toast.LENGTH_SHORT).show();
            } else {
                if (daoMascotas.insertarSolicitud(idAdoptante, animalActual.getIdMascota())) {
                    Toast.makeText(this, "Solicitud enviada", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(idAdoptante==-1) {
            btnContactar.setVisibility(View.GONE);
            btnGuardar.setVisibility(View.GONE);
        }

    }
    // Método helper para actualizar el botón según estado
    private void actualizarIconoFavorito(boolean esFavorito) {
        if (esFavorito) {
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
            btnGuardar.setIconResource(R.drawable.selector_favoritos);
            btnGuardar.setIconTint(ColorStateList.valueOf(
                    ContextCompat.getColor(getApplicationContext(), R.color.text_dark)));
            btnGuardar.setBackgroundTintList(ContextCompat.getColorStateList(
                    getApplicationContext(), R.color.btn_guardar_selector));
            btnGuardar.setStrokeColor(ColorStateList.valueOf(
                    ContextCompat.getColor(getApplicationContext(), R.color.gray)));
            btnGuardar.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_dark));
        }
    }
}