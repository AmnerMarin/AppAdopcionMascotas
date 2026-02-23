package sistemas.unc.edu.appadopcionmascotas.Firebase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Adoptante;
import sistemas.unc.edu.appadopcionmascotas.Model.Animal;
import sistemas.unc.edu.appadopcionmascotas.Model.Refugio;
import sistemas.unc.edu.appadopcionmascotas.Model.Usuario;

public class DbAnimalRepositorio {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private DAOAdopcion daoAdopcion;

    public interface AnimalCallback {
        void onSuccess();
        void onError(String mensaje);
    }

    public DbAnimalRepositorio(Context context) {
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        daoAdopcion = new DAOAdopcion((Activity) context);
    }

    // ===============================
    // PUBLICAR ANIMAL (INSERTAR)
    // ===============================
    public void publicarAnimal(Animal animal, AnimalCallback callback) {
        String firebaseUID = firestore.collection("Mascotas").document().getId();
        animal.setFirebaseUID(firebaseUID);

        // 1. PRIMERO guardamos en SQLite para que se genere el id_mascota local
        long idGenerado = daoAdopcion.insertarMascota(animal);

        if (idGenerado == -1) {
            callback.onError("Error al guardar la mascota en SQLite local.");
            return;
        }

        // 2. Subimos la foto a Storage
        StorageReference storageRef = storage.getReference().child("fotos_mascotas/" + firebaseUID + ".jpg");
        storageRef.putBytes(animal.getFoto())
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String urlFoto = uri.toString();

                        // 3. Pasamos el "idGenerado" al método que arma los datos para Firebase
                        Map<String, Object> mascotaMap = crearMascotaMap((int) idGenerado, animal, urlFoto);

                        firestore.collection("Mascotas").document(firebaseUID)
                                .set(mascotaMap)
                                .addOnSuccessListener(aVoid -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onError("Error al guardar en Firestore: " + e.getMessage()));

                    }).addOnFailureListener(e -> callback.onError("Error al obtener URL de la imagen: " + e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onError("Error al subir la imagen a Storage: " + e.getMessage()));
    }

    // ===============================
    // EDITAR ANIMAL (ACTUALIZAR)
    // ===============================
    public void editarAnimal(int idMascotaLocal, Animal animal, boolean fotoCambiada, AnimalCallback callback) {
        if (animal.getFirebaseUID() == null || animal.getFirebaseUID().isEmpty()) {
            callback.onError("Error crítico: La mascota no tiene un FirebaseUID asociado.");
            return;
        }

        if (fotoCambiada) {
            StorageReference storageRef = storage.getReference().child("fotos_mascotas/" + animal.getFirebaseUID() + ".jpg");
            storageRef.putBytes(animal.getFoto())
                    .addOnSuccessListener(taskSnapshot -> {
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String urlFoto = uri.toString();
                            actualizarEnAmbasBD(idMascotaLocal, animal, urlFoto, callback);
                        });
                    })
                    .addOnFailureListener(e -> callback.onError("Error al actualizar la imagen: " + e.getMessage()));
        } else {
            actualizarEnAmbasBD(idMascotaLocal, animal, null, callback);
        }
    }

    private void actualizarEnAmbasBD(int idMascotaLocal, Animal animal, String urlFoto, AnimalCallback callback) {
        Map<String, Object> mascotaMap = crearMascotaMap(idMascotaLocal, animal, urlFoto);

        if (urlFoto == null) {
            mascotaMap.remove("urlFoto");
        }

        firestore.collection("Mascotas").document(animal.getFirebaseUID())
                .update(mascotaMap)
                .addOnSuccessListener(aVoid -> {
                    boolean actualizado = daoAdopcion.actualizarMascota(idMascotaLocal, animal);
                    if (actualizado) callback.onSuccess();
                    else callback.onError("Error al actualizar en SQLite.");
                })
                .addOnFailureListener(e -> callback.onError("Error al actualizar en Firestore: " + e.getMessage()));
    }

    // ===============================
    // ELIMINAR ANIMAL
    // ===============================
    public void eliminarAnimal(int idMascotaLocal, String firebaseUID, AnimalCallback callback) {
        if (firebaseUID == null || firebaseUID.isEmpty()) {
            boolean eliminadoLocal = daoAdopcion.eliminarAnimal(idMascotaLocal);
            if(eliminadoLocal) callback.onSuccess();
            else callback.onError("Error al eliminar localmente.");
            return;
        }

        firestore.collection("Mascotas").document(firebaseUID).delete()
                .addOnSuccessListener(aVoid -> {
                    StorageReference storageRef = storage.getReference().child("fotos_mascotas/" + firebaseUID + ".jpg");
                    storageRef.delete().addOnCompleteListener(task -> {
                        boolean eliminadoLocal = daoAdopcion.eliminarAnimal(idMascotaLocal);
                        if (eliminadoLocal) {
                            callback.onSuccess();
                        } else {
                            callback.onError("Se eliminó de la nube, pero hubo un error en SQLite.");
                        }
                    });
                })
                .addOnFailureListener(e -> callback.onError("Error al eliminar de Firestore: " + e.getMessage()));
    }

    // ===============================
    // SINCRONIZAR DESDE FIREBASE (BAJAR A SQLITE)
    // ===============================
    public void sincronizarNuevosDesdeFirebase(Runnable alTerminar) {
        firestore.collection("Mascotas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                new Thread(() -> {
                    boolean seAgregaronNuevos = false;

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String firebaseUID = document.getString("FirebaseUID");

                        if (firebaseUID != null && !daoAdopcion.existeAnimalFirebaseUID(firebaseUID)) {

                            int idRefugio = document.getLong("idRefugio") != null ? document.getLong("idRefugio").intValue() : 0;
                            String nombre = document.getString("nombre");
                            String especie = document.getString("especie");
                            String raza = document.getString("raza");
                            double peso = document.getDouble("peso") != null ? document.getDouble("peso") : 0.0;
                            String edad = document.getString("edad");
                            String sexo = document.getString("sexo");
                            String temperamento = document.getString("temperamento");
                            String historia = document.getString("historia");
                            String estado = document.getString("estado");
                            String tamano = document.getString("tamano");
                            String urlFoto = document.getString("urlFoto");

                            byte[] fotoBytes = null;
                            if (urlFoto != null && !urlFoto.isEmpty()) {
                                try {
                                    URL url = new URL(urlFoto);
                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                    connection.setDoInput(true);
                                    connection.connect();
                                    InputStream input = connection.getInputStream();
                                    Bitmap bitmap = BitmapFactory.decodeStream(input);

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                                    fotoBytes = baos.toByteArray();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            Animal nuevoAnimal = new Animal(
                                    idRefugio, nombre, especie, raza, peso, edad, sexo,
                                    temperamento, historia, estado, tamano, fotoBytes
                            );
                            nuevoAnimal.setFirebaseUID(firebaseUID);

                            daoAdopcion.insertarMascota(nuevoAnimal);
                            seAgregaronNuevos = true;
                        }
                    }

                    if (seAgregaronNuevos && alTerminar != null) {
                        new Handler(Looper.getMainLooper()).post(alTerminar);
                    }

                }).start();
            }
        });
    }

    // Helper para estructurar datos y agregar el id_mascota
    private Map<String, Object> crearMascotaMap(int idMascotaLocal, Animal animal, String urlFoto) {
        Map<String, Object> map = new HashMap<>();

        map.put("id_mascota", idMascotaLocal); // <-- ¡AQUÍ SE GUARDA EL ID EN FIREBASE!

        map.put("idRefugio", animal.getIdRefugio());
        map.put("nombre", animal.getNombre());
        map.put("especie", animal.getEspecie());
        map.put("raza", animal.getRaza());
        map.put("peso", animal.getPeso());
        map.put("edad", animal.getEdad());
        map.put("sexo", animal.getSexo());
        map.put("temperamento", animal.getTemperamento());
        map.put("historia", animal.getHistoria());
        map.put("estado", animal.getEstado());
        map.put("tamano", animal.getTamano());
        map.put("FirebaseUID", animal.getFirebaseUID());

        if (urlFoto != null) {
            map.put("urlFoto", urlFoto);
        }
        return map;
    }


}