package sistemas.unc.edu.appadopcionmascotas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Firebase.DbAnimalRepositorio;
import sistemas.unc.edu.appadopcionmascotas.Model.Animal;

public class ActividadPublicarAnimal extends AppCompatActivity {

    private final String[] especies = {"Perro","Gato","Conejo","Hámster","Ave"};
    private final String[] tamanos = {"Pequeño","Mediano","Grande"};
    private final String[] sexos = {"Macho","Hembra"};
    private final String[] edades = {
            "Cachorro (0-1 año)",
            "Joven (1-3 años)",
            "Adulto (4-7 años)",
            "Senior (8-10 años)",
            "Muy Senior (11+ años)"
    };

    private ImageView imgFoto;
    private TextInputEditText txtNombre;
    private MaterialAutoCompleteTextView actEspecie, actTamano, actsexo, actedad;
    private TextInputEditText txtRaza;
    private TextInputEditText txtTemperamento;
    private TextInputEditText txtHistoria, edtpeso;
    private MaterialButton btnCancelar, btnPublicar, btnBack;

    private MaterialCardView cardFoto;
    private LinearLayout placeholder;

    private static final int REQUEST_GALERIA = 100;
    private byte[] foto;

    private DbAnimalRepositorio repoAnimales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_publicar_animal);

        repoAnimales = new DbAnimalRepositorio(this);

        inicializarVistas();
        configurarDropdowns();
        configurarEventos();
    }

    public void insertarAnimal() {
        DAOAdopcion dao = new DAOAdopcion(this);
        SharedPreferences prefs = getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);

        if(idUsuario == -1){
            Toast.makeText(this,"Usuario no encontrado",Toast.LENGTH_SHORT).show();
            btnPublicar.setEnabled(true);
            return;
        }

        int idRefugio = dao.obtenerIdRefugioPorUsuario(idUsuario);

        if(idRefugio == -1){
            Toast.makeText(this,"Este usuario no tiene refugio",Toast.LENGTH_SHORT).show();
            btnPublicar.setEnabled(true);
            return;
        }

        String nombre = txtNombre.getText().toString();
        String especie = actEspecie.getText().toString();
        String raza = txtRaza.getText().toString();
        String edad = actedad.getText().toString();
        String tamano = actTamano.getText().toString();
        String sexo = actsexo.getText().toString();
        String temperamento = txtTemperamento.getText().toString();
        String historia = txtHistoria.getText().toString();

        String pesoStr = edtpeso.getText().toString();
        double peso = pesoStr.isEmpty() ? 0 : Double.parseDouble(pesoStr);

        Animal animal = new Animal(
                idRefugio, nombre, especie, raza, peso, edad, sexo,
                temperamento, historia, "Disponible", tamano, foto
        );

        Toast.makeText(this, "Subiendo mascota, por favor espere...", Toast.LENGTH_LONG).show();

        repoAnimales.publicarAnimal(animal, new DbAnimalRepositorio.AnimalCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ActividadPublicarAnimal.this, "Animal publicado exitosamente ✔", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ActividadPublicarAnimal.this, ActividadRefugio.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(ActividadPublicarAnimal.this, mensaje, Toast.LENGTH_LONG).show();
                btnPublicar.setEnabled(true);
            }
        });
    }

    private void inicializarVistas() {
        imgFoto = findViewById(R.id.imgAnimal);
        txtNombre = findViewById(R.id.edtNombrePublicar);
        actEspecie = findViewById(R.id.actEspeciePublicar);
        txtRaza = findViewById(R.id.edtRazaPublicar);
        actedad = findViewById(R.id.actEdadPublicar);
        actTamano = findViewById(R.id.actTamanoPublicar);
        txtTemperamento = findViewById(R.id.edtTemperamentoPublicar);
        txtHistoria = findViewById(R.id.edtHistoriaPublicar);
        btnCancelar = findViewById(R.id.btnCancelarPublicar);
        btnPublicar = findViewById(R.id.btnPublicar);
        edtpeso = findViewById(R.id.edtPesoPublicar);
        actsexo = findViewById(R.id.actSexoPublicar);
        cardFoto = findViewById(R.id.cardFotoPublicar);
        placeholder = findViewById(R.id.layoutPlaceholder);
        btnBack = findViewById(R.id.btnBack);

        imgFoto.setVisibility(View.GONE);
        placeholder.setVisibility(View.VISIBLE);
    }

    private void configurarDropdowns() {
        actEspecie.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, especies));
        actTamano.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tamanos));
        actsexo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sexos));
        actedad.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, edades));
    }

    private void configurarEventos() {
        cardFoto.setOnClickListener(v->abrirGaleria());
        findViewById(R.id.frame).setOnClickListener(v->abrirGaleria());
        imgFoto.setOnClickListener(v->abrirGaleria());
        placeholder.setOnClickListener(v->abrirGaleria());

        btnCancelar.setOnClickListener(v -> finish());

        btnPublicar.setOnClickListener(v ->{
            if (!validarDatos()) {
                return;
            }
            btnPublicar.setEnabled(false); // Evitar doble clic
            insertarAnimal();
        });
        btnBack.setOnClickListener(v -> finish());
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Seleccionar una imagen"), REQUEST_GALERIA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GALERIA && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                android.net.Uri uriImagen = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriImagen);
                bitmap = redimensionarBitmap(bitmap, 500);

                imgFoto.setVisibility(View.VISIBLE);
                placeholder.setVisibility(View.GONE);
                imgFoto.setImageBitmap(bitmap);

                ByteArrayOutputStream flujo = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, flujo);
                foto = flujo.toByteArray();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap redimensionarBitmap(Bitmap imagen, int anchoMaximo) {
        int anchoOriginal = imagen.getWidth();
        int altoOriginal = imagen.getHeight();

        if (anchoOriginal <= anchoMaximo) return imagen;

        float aspectRadio = (float) altoOriginal / (float) anchoOriginal;
        int altoNuevo = Math.round(anchoMaximo * aspectRadio);

        return Bitmap.createScaledBitmap(imagen, anchoMaximo, altoNuevo, true);
    }

    private boolean validarDatos() {
        // [TU MISMO CÓDIGO DE VALIDACIÓN QUE TENÍAS ANTES, NO LO CAMBIÉ]
        String nombre = txtNombre.getText().toString().trim();
        if (nombre.isEmpty()) { txtNombre.setError("Ingrese el nombre"); return false; }
        if (foto == null) { Toast.makeText(this, "Debe seleccionar una imagen", Toast.LENGTH_SHORT).show(); return false; }
        return true;
    }
}