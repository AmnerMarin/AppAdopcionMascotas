package sistemas.unc.edu.appadopcionmascotas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Firebase.DbRepositorioChat;
import sistemas.unc.edu.appadopcionmascotas.Model.Mensaje;
import sistemas.unc.edu.appadopcionmascotas.UI.AdaptadorChat;

public class ActividadChat extends AppCompatActivity {

    private RecyclerView rvChat;
    private AdaptadorChat adaptadorChat;
    private EditText etMensaje;
    private ImageButton btnEnviar;
    private TextView tvNombreDestino;

    private int idChatLocal;
    private int miIdUsuario;
    private String chatUID; // Aquí guardaremos la llave única de la sala (Ej: "1_2_5")

    private DAOAdopcion dao;
    // NOTA: Usé DbRepositorioChat (el nombre que creamos antes). Si el tuyo se llama
    // DbRepositorioMensajeria, cámbiale el nombre aquí y en la inicialización.
    private DbRepositorioChat repoMensajeria;

    private ListenerRegistration listenerMensajes; // Para detener la escucha de mensajes al salir

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_chat);

        tvNombreDestino = findViewById(R.id.tvNombreChat);
        rvChat = findViewById(R.id.recyclerMensajes);
        etMensaje = findViewById(R.id.etMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);

        dao = new DAOAdopcion(this);
        repoMensajeria = new DbRepositorioChat(this);

        // Obtener datos del Intent
        idChatLocal = getIntent().getIntExtra("ID_CHAT", -1);
        String nombreDestino = getIntent().getStringExtra("NOMBRE_DESTINO");
        tvNombreDestino.setText(nombreDestino);

        // Obtener mi ID de usuario logueado
        SharedPreferences prefs = getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        miIdUsuario = prefs.getInt("id_usuario", -1);

        // 1. Obtenemos el FirebaseUID de la base de datos local usando el método que creamos
        chatUID = dao.obtenerFirebaseUIDChat(idChatLocal);

        if (chatUID == null || chatUID.isEmpty()) {
            Toast.makeText(this, "Error de sincronización con la sala de chat", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        configurarRecyclerView();

        // 2. Nos suscribimos a Firebase usando el "chatUID" en lugar de los 3 IDs sueltos
        listenerMensajes = repoMensajeria.escucharMensajes(idChatLocal, chatUID, () -> {
            // Esto se ejecuta mágicamente cada vez que alguien envía un mensaje a esta sala
            cargarMensajes();
        });

        // 3. Botón de Enviar
        btnEnviar.setOnClickListener(v -> {
            String texto = etMensaje.getText().toString().trim();
            if (!texto.isEmpty()) {
                etMensaje.setText(""); // Limpiar caja rápido para buena experiencia de usuario

                // Enviar a la nube
                repoMensajeria.enviarMensaje(idChatLocal, chatUID, miIdUsuario, texto, new DbRepositorioChat.MensajeCallback() {
                    @Override
                    public void onSuccess() {
                        // Refrescamos nuestra pantalla inmediatamente al mandarlo
                        cargarMensajes();
                    }

                    @Override
                    public void onError(String mensaje) {
                        Toast.makeText(ActividadChat.this, "Error al enviar: " + mensaje, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void configurarRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Para que empiece desde el mensaje más reciente abajo
        rvChat.setLayoutManager(layoutManager);
        cargarMensajes();
    }

    private void cargarMensajes() {
        List<Mensaje> listaMensajes = dao.obtenerMensajesDeChat(idChatLocal);
        adaptadorChat = new AdaptadorChat(listaMensajes, miIdUsuario);
        rvChat.setAdapter(adaptadorChat);

        if (!listaMensajes.isEmpty()) {
            rvChat.scrollToPosition(listaMensajes.size() - 1); // Hacer scroll hasta abajo
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ¡SÚPER IMPORTANTE! Desconectarse de Firebase al salir del chat
        // para que no siga descargando datos de fondo y no gaste batería.
        if (listenerMensajes != null) {
            listenerMensajes.remove();
        }
    }
}