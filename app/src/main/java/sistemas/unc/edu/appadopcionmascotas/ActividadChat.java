package sistemas.unc.edu.appadopcionmascotas;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Mensaje;
import sistemas.unc.edu.appadopcionmascotas.UI.AdaptadorChat;

public class ActividadChat extends AppCompatActivity {

    private RecyclerView rv;
    private AdaptadorChat adaptador;
    private List<Mensaje> listaMensajes;
    private TextView tvNombreCabecera;
    private EditText etMensaje;
    private ImageButton btnEnviar;
    private DAOAdopcion dao;
    private int idChat, idUsuarioActual;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_chat);

        dao = new DAOAdopcion(this);
        etMensaje = findViewById(R.id.etMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        rv = findViewById(R.id.recyclerMensajes);
        tvNombreCabecera = findViewById(R.id.tvNombreChat);


        //===TOOLBAR====
        Toolbar toolbar = findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbar);


        // Habilitar la flecha de retroceso
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            // Quitar el título por defecto del Toolbar si solo quieres ver tu LinearLayout
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // --- CAMBIAR COLOR DE LA FLECHA EN JAVA ---
        if (toolbar.getNavigationIcon() != null) {
            // Esto cambia el color de la flecha a negro (puedes usar cualquier color de tus resources)
            toolbar.getNavigationIcon().setTint(ContextCompat.getColor(this, R.color.black));
        }

        // Configurar la acción de la flecha
        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });


        String nombreChat = getIntent().getStringExtra("NOMBRE_DESTINO");
        tvNombreCabecera.setText(nombreChat);

        // 1. Obtener datos de la sesión y del Intent
        SharedPreferences prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        idUsuarioActual = prefs.getInt("id_usuario", -1);
        idChat = getIntent().getIntExtra("ID_CHAT", -1);

        // 2. Configurar RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Empieza desde abajo
        rv.setLayoutManager(layoutManager);

        cargarChat();

        // 3. Botón Enviar
        btnEnviar.setOnClickListener(v -> {
            String texto = etMensaje.getText().toString().trim();
            if (!texto.isEmpty()) {
                if (dao.insertarMensaje(idChat, idUsuarioActual, texto)) {
                    etMensaje.setText("");
                    cargarChat(); // Refresca la lista
                }
            }
        });

        // Hacer que el RecyclerView se desplace al último mensaje cuando se abra el teclado
        rv.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) { // Si el fondo actual es menor al anterior, el teclado subió
                if (adaptador.getItemCount() > 0) {
                    rv.postDelayed(() -> {
                        rv.smoothScrollToPosition(adaptador.getItemCount() - 1);
                    }, 100);
                }
            }
        });
    }

    private void cargarChat() {
        listaMensajes = dao.obtenerMensajesDeChat(idChat);
        adaptador = new AdaptadorChat(listaMensajes, idUsuarioActual);
        rv.setAdapter(adaptador);
        // Hacer scroll al último mensaje
        if (listaMensajes.size() > 0) {
            rv.smoothScrollToPosition(listaMensajes.size() - 1);
        }
    }

}