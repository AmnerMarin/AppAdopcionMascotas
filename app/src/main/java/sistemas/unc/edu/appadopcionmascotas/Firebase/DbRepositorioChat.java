package sistemas.unc.edu.appadopcionmascotas.Firebase;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
        String msgUID = firestore.collection("Mensajes").document().getId();

        boolean ok = daoAdopcion.insertarMensajeConUID(idChatLocal, idEmisor, texto, msgUID);
        if (!ok) {
            callback.onError("Error al guardar mensaje localmente");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("chatUID", chatUID);
        data.put("idEmisor", idEmisor);
        data.put("mensaje", texto);
        data.put("FirebaseUID", msgUID);
        data.put("timestamp", FieldValue.serverTimestamp());

        // 🔥 TRUCO: Desarmamos el chatUID (ej. "1_2_5") para guardarlo como campos sueltos
        // Así Firebase sabrá a quién le pertenece este mensaje.
        String[] partes = chatUID.split("_");
        if (partes.length == 3) {
            data.put("idAdoptante", Integer.parseInt(partes[0]));
            data.put("idRefugio", Integer.parseInt(partes[1]));
            data.put("idMascota", Integer.parseInt(partes[2]));
        }

        firestore.collection("Mensajes").document(msgUID)
                .set(data)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Error al enviar a la nube: " + e.getMessage()));
    }

    // ===============================
    // 2. SINCRONIZADOR GLOBAL (Para la bandeja de entrada)
    // ===============================
    public ListenerRegistration escucharMensajesGlobales(int idBuscado, String rol, Runnable alRecibirNuevo) {
        // Si soy Refugio busco mensajes para mi refugio. Si soy adoptante, para mí.
        String campoBusqueda = rol.equalsIgnoreCase("Refugio") ? "idRefugio" : "idAdoptante";

        return firestore.collection("Mensajes")
                .whereEqualTo(campoBusqueda, idBuscado)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    boolean huboNuevos = false;

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            String msgUID = dc.getDocument().getId();

                            // Si el mensaje no existe en mi SQLite, lo proceso
                            if (!daoAdopcion.existeMensajeFirebase(msgUID)) {

                                String chatUIDString = dc.getDocument().getString("chatUID");
                                Long idEmisorL = dc.getDocument().getLong("idEmisor");
                                String texto = dc.getDocument().getString("mensaje");

                                // Sacamos los IDs cortando el texto "1_2_5" (Adoptante_Refugio_Mascota)
                                // Esto funciona INCLUSO con los mensajes viejos que ya tenías en Firebase
                                if (chatUIDString != null && idEmisorL != null) {
                                    String[] partes = chatUIDString.split("_");
                                    if (partes.length == 3) {
                                        int idAdopt = Integer.parseInt(partes[0]);
                                        int idRef = Integer.parseInt(partes[1]);
                                        int idMasc = Integer.parseInt(partes[2]);

                                        // Me aseguro de que el chat exista localmente, o lo creo
                                        int idChat = daoAdopcion.obtenerOCrearChat(idAdopt, idRef, idMasc);

                                        // Inserto el mensaje
                                        daoAdopcion.insertarMensajeConUID(idChat, idEmisorL.intValue(), texto, msgUID);
                                        huboNuevos = true;
                                    }
                                }
                            }
                        }
                    }

                    // Aviso a la pantalla que la lista de chats se debe recargar
                    if (huboNuevos && alRecibirNuevo != null) {
                        new Handler(Looper.getMainLooper()).post(alRecibirNuevo);
                    }
                });
    }

    // ===============================
    // 3. ESCUCHAR MENSAJES DE 1 SOLO CHAT (Para ActividadChat)
    // ===============================
    public ListenerRegistration escucharMensajes(int idChatLocal, String chatUID, Runnable alRecibirNuevo) {
        return firestore.collection("Mensajes")
                .whereEqualTo("chatUID", chatUID)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    boolean huboNuevos = false;
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            String msgUID = dc.getDocument().getString("FirebaseUID");
                            Long idEmisorObj = dc.getDocument().getLong("idEmisor");
                            int idEmisor = idEmisorObj != null ? idEmisorObj.intValue() : -1;
                            String texto = dc.getDocument().getString("mensaje");

                            if (msgUID != null && !daoAdopcion.existeMensajeFirebase(msgUID) && idEmisor != -1) {
                                daoAdopcion.insertarMensajeConUID(idChatLocal, idEmisor, texto, msgUID);
                                huboNuevos = true;
                            }
                        }
                    }

                    if (huboNuevos && alRecibirNuevo != null) {
                        new Handler(Looper.getMainLooper()).post(alRecibirNuevo);
                    }
                });
    }
}