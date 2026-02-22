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
    private TextInputEditText txtHistoria,edtpeso;
    private MaterialButton btnCancelar, btnPublicar;

    private MaterialCardView cardFoto;
    private LinearLayout placeholder;

    private static final int REQUEST_GALERIA = 100;
    private byte[] foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_publicar_animal);

        inicializarVistas();
        configurarDropdowns();
        configurarEventos();
    }

    public void insertarAnimal(){

        DAOAdopcion dao = new DAOAdopcion(this);

        SharedPreferences prefs =
                getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);

        int idUsuario = prefs.getInt("id_usuario", -1);

        if(idUsuario == -1){
            Toast.makeText(this,"Usuario no encontrado",Toast.LENGTH_SHORT).show();
            return;
        }

        int idRefugio = dao.obtenerIdRefugioPorUsuario(idUsuario);

        if(idRefugio == -1){
            Toast.makeText(this,"Este usuario no tiene refugio",Toast.LENGTH_SHORT).show();
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
        double peso = pesoStr.isEmpty()?0:Double.parseDouble(pesoStr);

        Animal animal = new Animal(
                idRefugio,
                nombre,
                especie,
                raza,
                peso,
                edad,
                sexo,
                temperamento,
                historia,
                "Disponible",
                tamano,
                foto
        );

        boolean ok = dao.insertarMascota(animal);

        if(ok)
            Toast.makeText(this,"Animal guardado ✔",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"Error al guardar",Toast.LENGTH_SHORT).show();
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

        // estado inicial
        imgFoto.setVisibility(View.GONE);
        placeholder.setVisibility(View.VISIBLE);
    }

    private void configurarDropdowns() {

        actEspecie.setAdapter(
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1,
                        especies));

        actTamano.setAdapter(
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1,
                        tamanos));

        actsexo.setAdapter(
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1,sexos));
        actedad.setAdapter(
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1,edades));
        actTamano.setAdapter(
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1,tamanos));


    }

    private void configurarEventos() {

        // tocar card → abrir galería
        cardFoto.setOnClickListener(v->abrirGaleria());
        findViewById(R.id.frame).setOnClickListener(v->abrirGaleria());
        imgFoto.setOnClickListener(v->abrirGaleria());
        placeholder.setOnClickListener(v->abrirGaleria());


        // cancelar → volver

        btnCancelar.setOnClickListener(v -> finish());

        btnPublicar.setOnClickListener(v ->{

            if (!validarDatos()) {
                return;
            }

            Toast.makeText(this, "Animal publicado ✔", Toast.LENGTH_SHORT).show();
            insertarAnimal();
            Intent i = new Intent(this, ActividadRefugio.class);
            startActivity(i);
            finish();
        });
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
                // 1. Obtener la URI de la imagen seleccionada
                android.net.Uri uriImagen = data.getData();

                // 2. Convertir la URI en un Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriImagen);

                // 3. Redimensionar la imagen (IMPORTANTE para que no pese mucho en la BD)
                // Esto la ajusta a un máximo de 500px de ancho manteniendo la proporción
                bitmap = redimensionarBitmap(bitmap, 500);

                // 4. Mostrar en el ImageView
                imgFoto.setVisibility(View.VISIBLE);
                placeholder.setVisibility(View.GONE);
                imgFoto.setImageBitmap(bitmap);

                // 5. Convertir a byte[] para la base de datos
                ByteArrayOutputStream flujo = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, flujo); // Calidad al 70% para ahorrar espacio
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

        String nombre = txtNombre.getText().toString().trim();
        String especie = actEspecie.getText().toString().trim();
        String raza = txtRaza.getText().toString().trim();
        String edad = actedad.getText().toString().trim();
        String tamano = actTamano.getText().toString().trim();
        String sexo = actsexo.getText().toString().trim();
        String temperamento = txtTemperamento.getText().toString().trim();
        String historia = txtHistoria.getText().toString().trim();
        String pesoStr = edtpeso.getText().toString().trim();

        // -------- NOMBRE --------
        if (nombre.isEmpty()) {
            txtNombre.setError("Ingrese el nombre");
            txtNombre.requestFocus();
            return false;
        }

        if (nombre.length() < 2) {
            txtNombre.setError("El nombre es muy corto");
            txtNombre.requestFocus();
            return false;
        }

        // -------- ESPECIE --------
        if (especie.isEmpty()) {
            actEspecie.setError("Seleccione una especie");
            actEspecie.requestFocus();
            return false;
        }

        // -------- RAZA --------
        if (raza.isEmpty()) {
            txtRaza.setError("Ingrese la raza");
            txtRaza.requestFocus();
            return false;
        }

        // -------- EDAD --------
        if (edad.isEmpty()) {
            actedad.setError("Seleccione la edad");
            actedad.requestFocus();
            return false;
        }

        // -------- TAMAÑO --------
        if (tamano.isEmpty()) {
            actTamano.setError("Seleccione el tamaño");
            actTamano.requestFocus();
            return false;
        }

        // -------- SEXO --------
        if (sexo.isEmpty()) {
            actsexo.setError("Seleccione el sexo");
            actsexo.requestFocus();
            return false;
        }

        // -------- TEMPERAMENTO (3 características separadas por coma) --------
        if (temperamento.isEmpty()) {
            txtTemperamento.setError("Ingrese 3 características separadas por comas");
            txtTemperamento.requestFocus();
            return false;
        }

        String[] partes = temperamento.split(",");

        if (partes.length != 3) {
            txtTemperamento.setError("Debe ingresar EXACTAMENTE 3 características separadas por comas");
            txtTemperamento.requestFocus();
            return false;
        }

        for (String p : partes) {

            String limpio = p.trim();

            if (limpio.isEmpty()) {
                txtTemperamento.setError("Las características no pueden estar vacías");
                txtTemperamento.requestFocus();
                return false;
            }

            if (limpio.length() < 3) {
                txtTemperamento.setError("Cada característica debe tener al menos 3 letras");
                txtTemperamento.requestFocus();
                return false;
            }

            // Solo letras y espacios
            if (!limpio.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
                txtTemperamento.setError("Solo se permiten letras en las características");
                txtTemperamento.requestFocus();
                return false;
            }
        }

        // -------- HISTORIA --------
        if (historia.isEmpty()) {
            txtHistoria.setError("Ingrese la historia");
            txtHistoria.requestFocus();
            return false;
        }

        if (historia.length() < 10) {
            txtHistoria.setError("La historia debe tener al menos 10 caracteres");
            txtHistoria.requestFocus();
            return false;
        }

        // -------- PESO --------
        if (pesoStr.isEmpty()) {
            edtpeso.setError("Ingrese el peso");
            edtpeso.requestFocus();
            return false;
        }

        try {
            double peso = Double.parseDouble(pesoStr);

            if (peso <= 0) {
                edtpeso.setError("El peso debe ser mayor a 0");
                edtpeso.requestFocus();
                return false;
            }

        } catch (NumberFormatException e) {
            edtpeso.setError("Peso inválido");
            edtpeso.requestFocus();
            return false;
        }

        // -------- FOTO --------
        if (foto == null) {
            Toast.makeText(this, "Debe seleccionar una imagen", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}