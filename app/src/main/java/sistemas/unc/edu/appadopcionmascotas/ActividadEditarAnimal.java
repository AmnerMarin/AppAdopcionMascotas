package sistemas.unc.edu.appadopcionmascotas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Firebase.DbAnimalRepositorio;
import sistemas.unc.edu.appadopcionmascotas.Model.Animal;

public class ActividadEditarAnimal extends AppCompatActivity {

    private static final String EXTRA_ID_ANIMAL = "ID_ANIMAL";

    private final String[] especies = {"Perro", "Gato", "Conejo", "Hámster", "Ave"};
    private final String[] tamanos = {"Pequeño", "Mediano", "Grande"};
    private final String[] sexos = {"Macho", "Hembra"};
    private final String[] edades = {
            "Cachorro (0-1 año)",
            "Joven (1-3 años)",
            "Adulto (4-7 años)",
            "Senior (8-10 años)",
            "Muy Senior (11+ años)"
    };

    private ImageView imgAnimal;
    private LinearLayout layoutPlaceholderEditar;
    private TextInputEditText edtNombre, edtRaza, edtPeso, edtTemperamento, edtHistoria;
    private MaterialAutoCompleteTextView actEspecie, actTamano, actSexo, actEdad;
    private MaterialButton btnGuardar, btnCambiarFoto, btnBack, btnCancelarEditar;

    private DAOAdopcion dao;
    private DbAnimalRepositorio repoAnimales;
    private int idAnimal;
    private int idRefugio;
    private String firebaseUID; // Importante para Firebase
    private byte[] fotoBytes;
    private boolean fotoCambiada = false;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream is = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        if (bitmap != null) {
                            bitmap = redimensionarBitmap(bitmap, 500);
                            imgAnimal.setImageBitmap(bitmap);
                            fotoBytes = bitmapToByteArray(bitmap);
                            fotoCambiada = true;
                            imgAnimal.setVisibility(View.VISIBLE);
                            layoutPlaceholderEditar.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_editar_animal);

        idAnimal = getIntent().getIntExtra(EXTRA_ID_ANIMAL, -1);
        if (idAnimal == -1) {
            Toast.makeText(this, "Error: no se encontró el animal", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dao = new DAOAdopcion(this);
        repoAnimales = new DbAnimalRepositorio(this);

        inicializarVistas();
        configurarDropdowns();
        cargarDatosAnimal();
        configurarEventos();
    }

    private void inicializarVistas() {
        imgAnimal = findViewById(R.id.imgAnimal);
        layoutPlaceholderEditar = findViewById(R.id.layoutPlaceholderEditar);
        edtNombre = findViewById(R.id.edtNombre);
        edtRaza = findViewById(R.id.edtRaza);
        edtPeso = findViewById(R.id.edtPeso);
        edtTemperamento = findViewById(R.id.edtTemperamento);
        edtHistoria = findViewById(R.id.edtHistoria);
        actEspecie = findViewById(R.id.actEspecie);
        actTamano = findViewById(R.id.actTamano);
        actSexo = findViewById(R.id.actSexo);
        actEdad = findViewById(R.id.actEdad);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCambiarFoto = findViewById(R.id.btnCambiarFoto);
        btnBack = findViewById(R.id.btnBack);
        btnCancelarEditar = findViewById(R.id.btnCancelarEditar);
    }

    private void configurarDropdowns() {
        actEspecie.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, especies));
        actTamano.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tamanos));
        actSexo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sexos));
        actEdad.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, edades));
    }

    private void cargarDatosAnimal() {
        Animal animal = dao.obtenerDetalleAnimalConRefugio(idAnimal);
        if (animal == null) {
            Toast.makeText(this, "No se encontró el animal", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        idRefugio = animal.getIdRefugio();
        firebaseUID = animal.getFirebaseUID(); // Guardamos el UID para actualizar

        edtNombre.setText(animal.getNombre());
        edtRaza.setText(animal.getRaza());
        actEspecie.setText(animal.getEspecie(), false);
        actTamano.setText(animal.getTamano(), false);
        actSexo.setText(animal.getSexo(), false);
        actEdad.setText(animal.getEdad(), false);
        edtPeso.setText(animal.getPeso() > 0 ? String.valueOf(animal.getPeso()) : "");
        edtTemperamento.setText(animal.getTemperamento());
        edtHistoria.setText(animal.getHistoria());

        byte[] foto = animal.getFoto();
        if (foto != null && foto.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
            imgAnimal.setImageBitmap(bitmap);
            fotoBytes = foto;
            imgAnimal.setVisibility(View.VISIBLE);
            layoutPlaceholderEditar.setVisibility(View.GONE);
        } else {
            imgAnimal.setVisibility(View.GONE);
            layoutPlaceholderEditar.setVisibility(View.VISIBLE);
        }
    }

    private void configurarEventos() {
        btnBack.setOnClickListener(v -> finish());
        btnCancelarEditar.setOnClickListener(v -> finish());

        findViewById(R.id.frameEditar).setOnClickListener(v -> abrirGaleria());
        btnCambiarFoto.setOnClickListener(v -> abrirGaleria());

        btnGuardar.setOnClickListener(v -> guardarCambios());
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(Intent.createChooser(intent, "Seleccionar imagen"));
    }

    private void guardarCambios() {
        String nombre = edtNombre.getText().toString().trim();
        String especie = actEspecie.getText().toString().trim();
        String raza = edtRaza.getText().toString().trim();
        String edad = actEdad.getText().toString().trim();
        String tamano = actTamano.getText().toString().trim();
        String sexo = actSexo.getText().toString().trim();
        String temperamento = edtTemperamento.getText().toString().trim();
        String historia = edtHistoria.getText().toString().trim();

        if (nombre.isEmpty() || especie.isEmpty() || raza.isEmpty()) {
            Toast.makeText(this, "Nombre, especie y raza son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        String pesoStr = edtPeso.getText().toString().trim();
        double peso = pesoStr.isEmpty() ? 0 : Double.parseDouble(pesoStr.replace(",", "."));

        byte[] fotoActual = fotoCambiada ? fotoBytes : dao.obtenerDetalleAnimalConRefugio(idAnimal).getFoto();

        Animal animalActualizado = new Animal(
                idRefugio, nombre, especie, raza, peso, edad, sexo,
                temperamento, historia, "Disponible", tamano, fotoActual
        );
        animalActualizado.setFirebaseUID(firebaseUID);

        btnGuardar.setEnabled(false);
        Toast.makeText(this, "Actualizando, por favor espere...", Toast.LENGTH_SHORT).show();

        repoAnimales.editarAnimal(idAnimal, animalActualizado, fotoCambiada, new DbAnimalRepositorio.AnimalCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    // Verificamos que la actividad no se esté destruyendo antes de cerrarla
                    if (!isFinishing() && !isDestroyed()) {
                        Toast.makeText(ActividadEditarAnimal.this, "Cambios guardados ✔", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }

            @Override
            public void onError(String mensaje) {
                runOnUiThread(() -> {
                    if (!isFinishing() && !isDestroyed()) {
                        Toast.makeText(ActividadEditarAnimal.this, mensaje, Toast.LENGTH_LONG).show();
                        btnGuardar.setEnabled(true);
                    }
                });
            }
        });
    }
    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        return stream.toByteArray();
    }

    private Bitmap redimensionarBitmap(Bitmap imagen, int anchoMaximo) {
        int anchoOriginal = imagen.getWidth();
        int altoOriginal = imagen.getHeight();
        if (anchoOriginal <= anchoMaximo) return imagen;
        float aspectRatio = (float) altoOriginal / anchoOriginal;
        int altoNuevo = Math.round(anchoMaximo * aspectRatio);
        return Bitmap.createScaledBitmap(imagen, anchoMaximo, altoNuevo, true);
    }
}