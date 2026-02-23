package sistemas.unc.edu.appadopcionmascotas.Firebase;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.Map;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;

public class DbRepositorioChat {

    private FirebaseFirestore firestore;
    private DAOAdopcion daoAdopcion;

    public interface MensajeCallback {
        void onSuccess();
        void onError(String mensaje);
    }

    public DbRepositorioChat(Context context) {
        firestore = FirebaseFirestore.getInstance();
        daoAdopcion = new DAOAdopcion((Activity) context);
    }

    // ===============================
    // 1. ENVIAR MENSAJE
    // ===============================
    public void enviarMensaje(int idChatLocal, String chatUID, int idEmisor, String texto, MensajeCallback callback) {
        // 1. Generamos un ID único para el mensaje
        String msgUID = firestore.collection("Mensajes").document().getId();

        // 2. Lo guardamos localmente en SQLite para que aparezca rápido en la pantalla
        boolean ok = daoAdopcion.insertarMensajeConUID(idChatLocal, idEmisor, texto, msgUID);
        if (!ok) {
            callback.onError("Error al guardar mensaje localmente");
            return;
        }

        // 3. Lo subimos a Firebase
        Map<String, Object> data = new HashMap<>();
        data.put("chatUID", chatUID);      // ID del grupo de chat (ej. "1_2_5")
        data.put("idEmisor", idEmisor);    // Quién lo envió
        data.put("mensaje", texto);        // El texto
        data.put("FirebaseUID", msgUID);   // ID único del mensaje
        data.put("timestamp", FieldValue.serverTimestamp()); // Hora exacta del servidor

        firestore.collection("Mensajes").document(msgUID)
                .set(data)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Error al enviar a la nube: " + e.getMessage()));
    }

    // ===============================
    // 2. ESCUCHAR MENSAJES EN TIEMPO REAL
    // ===============================
    public ListenerRegistration escucharMensajes(int idChatLocal, String chatUID, Runnable alRecibirNuevo) {
        // Este método se queda "pegado" a Firebase. Cada vez que alguien escriba, se activará solo.
        return firestore.collection("Mensajes")
                .whereEqualTo("chatUID", chatUID)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) {
                        return; // Hubo un error de conexión
                    }

                    boolean huboNuevos = false;

                    // Revisamos qué documentos (mensajes) son nuevos
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            String msgUID = dc.getDocument().getString("FirebaseUID");
                            int idEmisor = dc.getDocument().getLong("idEmisor") != null ? dc.getDocument().getLong("idEmisor").intValue() : -1;
                            String texto = dc.getDocument().getString("mensaje");

                            // Si el mensaje viene de la nube y NO lo tenemos en nuestro celular, lo guardamos
                            if (msgUID != null && !daoAdopcion.existeMensajeFirebase(msgUID)) {
                                daoAdopcion.insertarMensajeConUID(idChatLocal, idEmisor, texto, msgUID);
                                huboNuevos = true;
                            }
                        }
                    }

                    // Si descargamos mensajes nuevos, avisamos a la pantalla para que se actualice
                    if (huboNuevos && alRecibirNuevo != null) {
                        new Handler(Looper.getMainLooper()).post(alRecibirNuevo);
                    }
                });
    }
}