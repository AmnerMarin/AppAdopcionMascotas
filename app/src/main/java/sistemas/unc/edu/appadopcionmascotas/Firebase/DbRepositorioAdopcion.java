package sistemas.unc.edu.appadopcionmascotas.Firebase;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;

public class DbRepositorioAdopcion {

    private FirebaseFirestore firestore;
    private DAOAdopcion daoAdopcion;

    public interface AdopcionCallback {
        void onSuccess();
        void onError(String mensaje);
    }

    public DbRepositorioAdopcion(Context context) {
        firestore = FirebaseFirestore.getInstance();
        daoAdopcion = new DAOAdopcion((Activity) context);
    }

    // ===============================
    // 1. REGISTRAR SOLICITUD (Para el Adoptante)
    // ===============================
    public void registrarSolicitud(int idAdoptante, int idMascota, int idRefugio, AdopcionCallback callback) {
        String docId = idAdoptante + "_" + idMascota; // ID único para evitar múltiples solicitudes iguales
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        // 1. Guardar localmente
        boolean ok = daoAdopcion.insertarSolicitudConUID(idAdoptante, idMascota, fecha, docId);
        if (!ok) {
            callback.onError("Error al guardar la solicitud en el teléfono.");
            return;
        }

        // 2. Guardar en Firebase
        Map<String, Object> data = new HashMap<>();
        data.put("idAdoptante", idAdoptante);
        data.put("idMascota", idMascota);
        data.put("idRefugio", idRefugio); // Importante para que el refugio lo encuentre rápido
        data.put("fecha_adopcion", fecha);
        data.put("estado", "Pendiente");
        data.put("FirebaseUID", docId);

        firestore.collection("Adopciones").document(docId)
                .set(data)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Error en la nube: " + e.getMessage()));
    }

    // ===============================
    // 2. RESPONDER SOLICITUD (Para el Refugio - Aceptar/Rechazar)
    // ===============================
    public void responderSolicitud(int idAdopcionLocal, String docIdAdopcion, int idMascotaLocal, String docIdMascota, boolean aprobar, AdopcionCallback callback) {
        String nuevoEstado = aprobar ? "Aprobada" : "Rechazada";

        // 1. Actualizar el estado en la colección "Adopciones"
        firestore.collection("Adopciones").document(docIdAdopcion)
                .update("estado", nuevoEstado)
                .addOnSuccessListener(aVoid -> {

                    // 2. Si se aprueba, la mascota debe pasar a ser "Adoptado" en todo el sistema
                    if (aprobar && docIdMascota != null && !docIdMascota.isEmpty()) {
                        firestore.collection("Mascotas").document(docIdMascota)
                                .update("estado", "Adoptado")
                                .addOnSuccessListener(aVoid2 -> {
                                    daoAdopcion.aprobarSolicitud(idAdopcionLocal, idMascotaLocal);
                                    callback.onSuccess();
                                })
                                .addOnFailureListener(e -> callback.onError("Mascota no actualizada: " + e.getMessage()));
                    } else {
                        // Si se rechaza, solo se actualiza la solicitud localmente
                        daoAdopcion.rechazarSolicitud(idAdopcionLocal);
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(e -> callback.onError("Error actualizando solicitud: " + e.getMessage()));
    }

    // ===============================
    // 3. SINCRONIZADORES SILENCIOSOS (Para cuando inicie la app)
    // ===============================
    public void sincronizarSolicitudesAdoptante(int idAdoptante, Runnable alTerminar) {
        firestore.collection("Adopciones").whereEqualTo("idAdoptante", idAdoptante).get()
                .addOnCompleteListener(task -> procesarSincronizacion(task, alTerminar));
    }

    public void sincronizarSolicitudesRefugio(int idRefugio, Runnable alTerminar) {
        firestore.collection("Adopciones").whereEqualTo("idRefugio", idRefugio).get()
                .addOnCompleteListener(task -> procesarSincronizacion(task, alTerminar));
    }

    private void procesarSincronizacion(com.google.android.gms.tasks.Task<com.google.firebase.firestore.QuerySnapshot> task, Runnable alTerminar) {
        if (task.isSuccessful() && task.getResult() != null) {
            new Thread(() -> {
                boolean cambios = false;
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    int idAdopt = doc.getLong("idAdoptante") != null ? doc.getLong("idAdoptante").intValue() : -1;
                    int idMasc = doc.getLong("idMascota") != null ? doc.getLong("idMascota").intValue() : -1;
                    String estadoFB = doc.getString("estado");
                    String fechaFB = doc.getString("fecha_adopcion");
                    String uid = doc.getId();

                    if (idAdopt != -1 && idMasc != -1) {
                        // Insertar si no existía
                        if (!daoAdopcion.existeSolicitud(idAdopt, idMasc)) {
                            daoAdopcion.insertarSolicitudConUID(idAdopt, idMasc, fechaFB, uid);
                            cambios = true;
                        }

                        // Forzar el estado de Firebase a nuestro SQLite (por si nos aprobaron/rechazaron)
                        daoAdopcion.actualizarEstadoSolicitudPorUID(uid, estadoFB);

                        if ("Aprobada".equals(estadoFB)) {
                            daoAdopcion.actualizarEstadoMascota(idMasc, "Adoptado");
                            cambios = true;
                        }
                    }
                }
                if (cambios && alTerminar != null) {
                    new Handler(Looper.getMainLooper()).post(alTerminar);
                }
            }).start();
        }
    }
}