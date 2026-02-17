package sistemas.unc.edu.appadopcionmascotas;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import java.io.ByteArrayOutputStream;
import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Animal;

public class ActividadPublicarAnimal extends AppCompatActivity {

    private final String[] especies = {"Perro","Gato","Conejo","Hámster","Ave"};
    private final String[] tamanos = {"Pequeño","Mediano","Grande"};


    private ImageView imgFoto;
    private TextInputEditText txtNombre;
    private AutoCompleteTextView actEspecie, actTamano, edtsexo;
    private TextInputEditText txtRaza;
    private TextInputEditText txtEdad;
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
        String nombre = txtNombre.getText().toString();
        String especie = actEspecie.getText().toString();
        String raza = txtRaza.getText().toString();
        String edad = txtEdad.getText().toString();
        String tamano = actTamano.getText().toString();
        double peso = Double.parseDouble(edtpeso.getText().toString());
        String sexo = edtsexo.getText().toString();
        String temperamento = txtTemperamento.getText().toString();
        String historia = txtHistoria.getText().toString();


        DAOAdopcion dao = new DAOAdopcion(this);

        // Obtener ID del refugio desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt("id_usuario", -1);

        int idRefugio = dao.obtenerIdRefugioPorUsuario(idUsuario);

        if(idRefugio != -1){
            Animal animal = new Animal(idRefugio,nombre,especie,raza,peso,edad,sexo,temperamento,historia,"Disponible",tamano,foto);
            dao.insertarMascota(animal);
        }
    }


    private void inicializarVistas() {

        imgFoto = findViewById(R.id.imgAnimal);
        txtNombre = findViewById(R.id.edtNombrePublicar);
        actEspecie = findViewById(R.id.actEspeciePublicar);
        txtRaza = findViewById(R.id.edtRazaPublicar);
        txtEdad = findViewById(R.id.edtEdadPublicar);
        actTamano = findViewById(R.id.actTamanoPublicar);
        txtTemperamento = findViewById(R.id.edtTemperamentoPublicar);
        txtHistoria = findViewById(R.id.edtHistoriaPublicar);
        btnCancelar = findViewById(R.id.btnCancelarPublicar);
        btnPublicar = findViewById(R.id.btnPublicar);
        edtpeso = findViewById(R.id.edtPesoPublicar);
        edtsexo = findViewById(R.id.actSexoPublicar);

        cardFoto = findViewById(R.id.cardFotoPublicar);
        placeholder = findViewById(R.id.layoutPlaceholder);

        // estado inicial
        imgFoto.setVisibility(View.GONE);
        placeholder.setVisibility(View.VISIBLE);
    }

    private void configurarDropdowns() {

        actEspecie.setAdapter(
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1,
                        especies));

        actTamano.setAdapter(
                new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1,
                        tamanos));
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
                Toast.makeText(this, "Animal publicado ✔", Toast.LENGTH_SHORT).show();
                insertarAnimal();
                Intent i = new Intent(this, ActividadRefugio.class);
                startActivity(i);
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

        if (requestCode == REQUEST_GALERIA && resultCode == RESULT_OK && data != null) {
            imgFoto.setVisibility(View.VISIBLE);
            imgFoto.setImageURI(data.getData());

            imgFoto.buildDrawingCache();
            Bitmap oImagen = imgFoto.getDrawingCache();
            ByteArrayOutputStream flujo = new ByteArrayOutputStream();
            oImagen.compress(Bitmap.CompressFormat.JPEG, 0, flujo);
            foto = flujo.toByteArray();
        }
    }


}
