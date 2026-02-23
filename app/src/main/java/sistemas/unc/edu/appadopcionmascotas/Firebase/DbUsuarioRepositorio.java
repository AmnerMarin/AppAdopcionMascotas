package sistemas.unc.edu.appadopcionmascotas.Firebase;

import android.app.Activity;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import sistemas.unc.edu.appadopcionmascotas.Data.DAOAdopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Adoptante;
import sistemas.unc.edu.appadopcionmascotas.Model.Refugio;
import sistemas.unc.edu.appadopcionmascotas.Model.Usuario;

public class DbUsuarioRepositorio {
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private DAOAdopcion daoAdopcion;

    public interface RegistroCallback {
        void onSuccess();
        void onError(String mensaje);
    }

    public DbUsuarioRepositorio(Context context) {
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        daoAdopcion = new DAOAdopcion((Activity) context);
    }

    // ===============================
    // REGISTRAR REFUGIO
    // ===============================
    public void registrarRefugio(
            Usuario user,
            Refugio refugio,
            RegistroCallback callback
    ) {
        mAuth.createUserWithEmailAndPassword(user.getCorreo(), user.getContrasena())
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        callback.onError(task.getException().getMessage());
                        return;
                    }

                    String firebaseUID = mAuth.getCurrentUser().getUid();

                    // 1. Asignamos UIDs y enmascaramos la contraseña
                    String contrasenaOriginal = user.getContrasena(); // Guardamos temporalmente
                    user.setContrasena("********");
                    user.setFirebaseUID(firebaseUID);
                    refugio.setFirebaseUID(firebaseUID);

                    // 2. PRIMERO guardamos en SQLite para generar los IDs
                    user.setContrasena(contrasenaOriginal); // En local sí guardamos la real (o hasheada)
                    long idUsuarioLocal = daoAdopcion.insertarUsuario(user);

                    if (idUsuarioLocal == -1) {
                        callback.onError("Error al generar ID de Usuario en SQLite");
                        return;
                    }

                    // Asignamos el id de usuario al refugio antes de insertarlo en SQLite
                    refugio.setIdUsuario((int) idUsuarioLocal);
                    long idRefugioLocal = daoAdopcion.insertarRefugio(refugio);

                    if (idRefugioLocal == -1) {
                        callback.onError("Error al generar ID de Refugio en SQLite");
                        return;
                    }

                    // 3. Agregamos los IDs generados a los objetos para Firestore
                    user.setIdusuario((int) idUsuarioLocal);
                    user.setContrasena("********"); // En la nube no subimos la contraseña real

                    refugio.setId_refugio((int) idRefugioLocal);
                    // IMPORTANTE: Asegúrate de que tu modelo Refugio tenga el método setId_refugio(int id)
                    // Si tu método se llama distinto (ej. setIdRefugio), cámbialo en la línea de arriba.

                    // 4. LUEGO guardamos en Firestore
                    firestore.collection("Usuarios")
                            .document(firebaseUID)
                            .set(user);

                    firestore.collection("Refugios")
                            .document(firebaseUID)
                            .set(refugio)
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onError("Error al subir a Firestore: " + e.getMessage()));
                });
    }

    // ===============================
    // REGISTRAR ADOPTANTE
    // ===============================
    public void registrarAdoptante(
            Usuario user,
            Adoptante adoptante,
            RegistroCallback callback
    ) {
        mAuth.createUserWithEmailAndPassword(user.getCorreo(), user.getContrasena())
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {
                        callback.onError(task.getException().getMessage());
                        return;
                    }

                    String firebaseUID = mAuth.getCurrentUser().getUid();

                    // 1. Asignamos UIDs
                    String contrasenaOriginal = user.getContrasena();
                    user.setContrasena("********");
                    user.setFirebaseUID(firebaseUID);
                    adoptante.setFirebaseUID(firebaseUID);

                    // 2. PRIMERO guardamos en SQLite
                    user.setContrasena(contrasenaOriginal);
                    long idUsuarioLocal = daoAdopcion.insertarUsuario(user);

                    if (idUsuarioLocal == -1) {
                        callback.onError("Error al generar ID de Usuario en SQLite");
                        return;
                    }

                    adoptante.setId_usuario((int) idUsuarioLocal);
                    long idAdoptanteLocal = daoAdopcion.insertarAdoptante(adoptante);

                    if (idAdoptanteLocal == -1) {
                        callback.onError("Error al generar ID de Adoptante en SQLite");
                        return;
                    }

                    // 3. Agregamos los IDs al objeto para Firestore
                    user.setIdusuario((int) idUsuarioLocal);
                    user.setContrasena("********");

                    adoptante.setId_adoptante((int) idAdoptanteLocal);
                    // IMPORTANTE: Asegúrate de que tu modelo Adoptante tenga el método setId_adoptante(int id)

                    // 4. LUEGO guardamos en Firestore
                    firestore.collection("Usuarios")
                            .document(firebaseUID)
                            .set(user);

                    firestore.collection("Adoptantes")
                            .document(firebaseUID)
                            .set(adoptante)
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onError("Error al subir a Firestore: " + e.getMessage()));
                });
    } // ========================================================
    // DESCARGAR USUARIO DESDE FIREBASE (Para el Login)
    // ========================================================
    public void descargarUsuarioDeFirebase(String firebaseUID, String correo, String contrasenaReal, DbUsuarioRepositorio.RegistroCallback callback) {
        firestore.collection("Usuarios").document(firebaseUID).get().addOnSuccessListener(docUser -> {
            if (docUser.exists()) {

                // 1. Recrear el objeto Usuario
                Usuario user = new Usuario();
                user.setCorreo(docUser.getString("correo"));
                user.setContrasena(contrasenaReal); // Guardamos la real en el celular
                user.setRol(docUser.getString("rol"));
                user.setFirebaseUID(firebaseUID);

                // Insertar en SQLite
                long idUsuarioLocal = daoAdopcion.insertarUsuario(user);

                if (idUsuarioLocal != -1) {
                    String rol = user.getRol();

                    // 2. Si es Adoptante, bajamos sus datos
                    if ("Adoptante".equalsIgnoreCase(rol)) {
                        firestore.collection("Adoptantes").document(firebaseUID).get().addOnSuccessListener(docAdop -> {
                            if (docAdop.exists()) {
                                Adoptante adoptante = new Adoptante();
                                adoptante.setId_usuario((int) idUsuarioLocal);
                                adoptante.setNombres(docAdop.getString("nombres"));
                                adoptante.setApellidos(docAdop.getString("apellidos"));
                                adoptante.setDireccion(docAdop.getString("direccion"));
                                adoptante.setTelefono(docAdop.getString("telefono"));
                                adoptante.setFirebaseUID(firebaseUID);

                                daoAdopcion.insertarAdoptante(adoptante);
                                callback.onSuccess();
                            }
                        });
                    }
                    // 3. Si es Refugio, bajamos sus datos
                    else if ("Refugio".equalsIgnoreCase(rol)) {
                        firestore.collection("Refugios").document(firebaseUID).get().addOnSuccessListener(docRef -> {
                            if (docRef.exists()) {
                                Refugio refugio = new Refugio();
                                refugio.setIdUsuario((int) idUsuarioLocal);
                                refugio.setNombre_refugio(docRef.getString("nombre_refugio"));
                                refugio.setDesripcion(docRef.getString("descripcion"));
                                refugio.setDireccion(docRef.getString("direccion"));
                                refugio.setTelefono(docRef.getString("telefono"));
                                refugio.setLatitud(docRef.getDouble("latitud") != null ? docRef.getDouble("latitud") : 0);
                                refugio.setLongitud(docRef.getDouble("longitud") != null ? docRef.getDouble("longitud") : 0);
                                refugio.setFirebaseUID(firebaseUID);

                                daoAdopcion.insertarRefugio(refugio);
                                callback.onSuccess();
                            }
                        });
                    }
                }
            } else {
                callback.onError("El usuario no existe en la base de datos.");
            }
        }).addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}