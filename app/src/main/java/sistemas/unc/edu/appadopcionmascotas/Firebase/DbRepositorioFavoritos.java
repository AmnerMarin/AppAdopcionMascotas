package sistemas.unc.edu.appadopcionmascotas.Firebase;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Animal;

public class DbRepositorioFavoritos {

    private FirebaseFirestore firestore;
    private DAOAdopcion daoAdopcion;

    public interface FavoritoCallback {
        void onSuccess();
        void onError(String mensaje);
    }

    public DbRepositorioFavoritos(Context context) {
        firestore = FirebaseFirestore.getInstance();
        daoAdopcion = new DAOAdopcion((Activity) context);
    }

    // ===============================
    // AGREGAR FAVORITO
    // ===============================
    public void agregarFavorito(int idAdoptante, int idMascota, FavoritoCallback callback) {
        // 1. Guardar localmente
        boolean ok = daoAdopcion.agregarFavorito(idAdoptante, idMascota);
        if (!ok) {
            callback.onError("Error al guardar localmente en SQLite");
            return;
        }

        // 2. Guardar en Firebase (ID = idAdoptante_idMascota para evitar duplicados y facilitar borrado)
        String docId = idAdoptante + "_" + idMascota;
        Map<String, Object> favData = new HashMap<>();
        favData.put("idAdoptante", idAdoptante);
        favData.put("idMascota", idMascota);
        favData.put("FirebaseUID", docId);

        firestore.collection("Favoritos").document(docId)
                .set(favData)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Error en la nube: " + e.getMessage()));
    }

    // ===============================
    // ELIMINAR FAVORITO
    // ===============================
    public void eliminarFavorito(int idAdoptante, int idMascota, FavoritoCallback callback) {
        // 1. Eliminar localmente
        boolean ok = daoAdopcion.eliminarFavorito(idAdoptante, idMascota);
        if (!ok) {
            callback.onError("Error al eliminar localmente");
            return;
        }

        // 2. Eliminar de Firebase
        String docId = idAdoptante + "_" + idMascota;
        firestore.collection("Favoritos").document(docId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Error en la nube: " + e.getMessage()));
    }

    // ===============================
    // SINCRONIZAR DESDE FIREBASE
    // ===============================
    public void sincronizarFavoritos(int idAdoptante, Runnable alTerminar) {
        firestore.collection("Favoritos")
                .whereEqualTo("idAdoptante", idAdoptante)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        new Thread(() -> {
                            boolean huboCambios = false;

                            List<Integer> favoritosFirebase = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                int idMascota = document.getLong("idMascota") != null ? document.getLong("idMascota").intValue() : -1;

                                if (idMascota != -1) {
                                    favoritosFirebase.add(idMascota);

                                    if (!daoAdopcion.esFavorito(idAdoptante, idMascota)) {
                                        daoAdopcion.agregarFavorito(idAdoptante, idMascota);
                                        huboCambios = true;
                                    }
                                }
                            }

                            List<Animal> favoritosLocales = daoAdopcion.obtenerFavoritosPorAdoptante(idAdoptante);
                            for (Animal animalLocal : favoritosLocales) {
                                if (!favoritosFirebase.contains(animalLocal.getIdMascota())) {
                                    daoAdopcion.eliminarFavorito(idAdoptante, animalLocal.getIdMascota());
                                    huboCambios = true;
                                }
                            }

                            if (huboCambios && alTerminar != null) {
                                new Handler(Looper.getMainLooper()).post(alTerminar);
                            }
                        }).start();
                    }
                })
                // ¡AGREGA ESTO PARA SABER SI HAY ERROR EN LA NUBE!
                .addOnFailureListener(e -> {
                    System.out.println("ERROR AL DESCARGAR FAVORITOS: " + e.getMessage());
                });
    }
}