package sistemas.unc.edu.appadopcionmascotas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Firebase.DbRepositorioAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Firebase.DbRepositorioFavoritos;
import sistemas.unc.edu.appadopcionmascotas.Model.Animal;

public class ActividadVerAnimal extends AppCompatActivity {
    private ImageView imgFotoMascota;
    private TextView txtNombre, txtRaza, txtEdad, txtEstado, txtHistoria, txtsexo, txttamano;
    private TextView txtRefugioNombre, txtRefugioDireccion, txtRefugioTelefono;
    private Chip chiptemp1, chiptemp2, chiptem3, chipTipo;
    private MaterialButton btnBack, btnContactar, btnSolicitarAdopcion, btnGuardar;

    private DAOAdopcion daoMascotas;
    private DbRepositorioFavoritos repoFavoritos;
    private Animal animalActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_ver_animal);

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
        btnGuardar = findViewById(R.id.btnGuardar);

        daoMascotas = new DAOAdopcion(this);
        repoFavoritos = new DbRepositorioFavoritos(this);

        int idAnimal = getIntent().getIntExtra("ID_ANIMAL", -1);
        animalActual = daoMascotas.obtenerDetalleAnimalConRefugio(idAnimal);

        SharedPreferences prefs = getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);
        int idAdoptante = daoMascotas.obtenerIdAdoptantePorUsuario(idUsuario);

        if (animalActual != null && idAdoptante != -1) {
            boolean esFavorito = daoMascotas.esFavorito(idAdoptante, animalActual.getIdMascota());
            actualizarIconoFavorito(esFavorito);
        }

        // ===============================================
        // EVENTO DEL BOTÓN FAVORITO USANDO FIREBASE
        // ===============================================
        btnGuardar.setOnClickListener(v -> {
            if (animalActual != null && idAdoptante != -1) {
                int idMascota = animalActual.getIdMascota();
                boolean esActualmenteFav = daoMascotas.esFavorito(idAdoptante, idMascota);

                btnGuardar.setEnabled(false); // Bloquear botón para evitar clics dobles

                if (esActualmenteFav) {
                    repoFavoritos.eliminarFavorito(idAdoptante, idMascota, new DbRepositorioFavoritos.FavoritoCallback() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(() -> {
                                if(!isFinishing()){
                                    actualizarIconoFavorito(false);
                                    btnGuardar.setEnabled(true);
                                    Toast.makeText(ActividadVerAnimal.this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        @Override
                        public void onError(String msg) {
                            runOnUiThread(() -> {
                                if(!isFinishing()){
                                    btnGuardar.setEnabled(true);
                                    Toast.makeText(ActividadVerAnimal.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } else {
                    repoFavoritos.agregarFavorito(idAdoptante, idMascota, new DbRepositorioFavoritos.FavoritoCallback() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(() -> {
                                if(!isFinishing()){
                                    actualizarIconoFavorito(true);
                                    btnGuardar.setEnabled(true);
                                    Toast.makeText(ActividadVerAnimal.this, "Añadido a favoritos", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        @Override
                        public void onError(String msg) {
                            runOnUiThread(() -> {
                                if(!isFinishing()){
                                    btnGuardar.setEnabled(true);
                                    Toast.makeText(ActividadVerAnimal.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });

        btnContactar.setOnClickListener(v -> {
            if (animalActual != null && idAdoptante != -1) {
                int idRefugio = animalActual.getIdRefugio();
                int idChat = daoMascotas.obtenerOCrearChat(idAdoptante, idRefugio, animalActual.getIdMascota());

                if (idChat != -1) {
                    Intent intent = new Intent(this, ActividadChat.class);
                    intent.putExtra("ID_CHAT", idChat);
                    intent.putExtra("NOMBRE_DESTINO", animalActual.getNombreRefugio());
                    intent.putExtra("NOMBRE_MASCOTA", animalActual.getNombre());
                    startActivity(intent);
                }
            }
        });

        // ===============================================
        // EVENTO SOLICITAR ADOPCIÓN (CON FIREBASE)
        // ===============================================
        btnSolicitarAdopcion.setOnClickListener(v -> {
            if (daoMascotas.existeSolicitud(idAdoptante, animalActual.getIdMascota())) {
                Toast.makeText(this, "Ya tienes una solicitud pendiente", Toast.LENGTH_SHORT).show();
            } else {
                btnSolicitarAdopcion.setEnabled(false); // Bloquear para evitar dobles envíos
                Toast.makeText(this, "Enviando solicitud a la nube...", Toast.LENGTH_SHORT).show();

                DbRepositorioAdopcion repoAdopcion = new DbRepositorioAdopcion(this);
                repoAdopcion.registrarSolicitud(idAdoptante, animalActual.getIdMascota(), animalActual.getIdRefugio(), new DbRepositorioAdopcion.AdopcionCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            if (!isFinishing()) {
                                btnSolicitarAdopcion.setEnabled(true);
                                Toast.makeText(ActividadVerAnimal.this, "¡Solicitud enviada con éxito!", Toast.LENGTH_LONG).show();
                                // Opcional: Cerrar la actividad o cambiar el texto del botón
                            }
                        });
                    }

                    @Override
                    public void onError(String msg) {
                        runOnUiThread(() -> {
                            if (!isFinishing()) {
                                btnSolicitarAdopcion.setEnabled(true);
                                Toast.makeText(ActividadVerAnimal.this, "Error: " + msg, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });

        if(idAdoptante == -1) {
            btnContactar.setVisibility(View.GONE);
            btnGuardar.setVisibility(View.GONE);
            btnSolicitarAdopcion.setVisibility(View.GONE);
        }
        if(animalActual != null && "Adoptado".equals(animalActual.getEstado())) {
            btnSolicitarAdopcion.setVisibility(View.GONE);
        }

        cargarDatos();

        btnBack.setOnClickListener(v -> finish());
    }

    private void cargarDatos() {
        if (animalActual != null) {
            txtNombre.setText(animalActual.getNombre());
            txtRaza.setText(animalActual.getRaza());
            txtEdad.setText(animalActual.getEdad());
            txtEstado.setText(animalActual.getEstado());
            txtHistoria.setText(animalActual.getHistoria());
            txtsexo.setText(animalActual.getSexo());
            txttamano.setText(animalActual.getTamano());
            chipTipo.setText(animalActual.getEspecie());

            String temp = animalActual.getTemperamento();
            if (temp != null && !temp.isEmpty()) {
                String[] tempArray = temp.split(",");
                if (tempArray.length > 0) chiptemp1.setText(tempArray[0].trim());
                if (tempArray.length > 1) chiptemp2.setText(tempArray[1].trim());
                if (tempArray.length > 2) chiptem3.setText(tempArray[2].trim());
            }

            txtRefugioNombre.setText(animalActual.getNombreRefugio());
            txtRefugioDireccion.setText(animalActual.getDireccionRefugio());
            txtRefugioTelefono.setText(animalActual.getTelefonoRefugio());

            if (animalActual.getFoto() != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(animalActual.getFoto(), 0, animalActual.getFoto().length);
                imgFotoMascota.setImageBitmap(bmp);
            }
        }
    }

    private void actualizarIconoFavorito(boolean esFavorito) {
        if (esFavorito) {
            btnGuardar.setText("Guardado");
            btnGuardar.setIconResource(R.drawable.corazon_lleno);
            btnGuardar.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            btnGuardar.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange_adopted)));
            btnGuardar.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.orange_adopted)));
            btnGuardar.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else {
            btnGuardar.setText("Guardar");
            btnGuardar.setIconResource(R.drawable.selector_favoritos);
            btnGuardar.setIconTint(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.text_dark)));
            btnGuardar.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.btn_guardar_selector));
            btnGuardar.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray)));
            btnGuardar.setTextColor(ContextCompat.getColor(this, R.color.text_dark));
        }
    }
}