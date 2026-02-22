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
            daoAdopcion = new DAOAdopcion((Activity)context);
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

                        // 🔹 Guardar en Firestore
                        user.setContrasena("********");
                        user.setFirebaseUID(firebaseUID);
                        refugio.setFirebaseUID(firebaseUID);

                        firestore.collection("Usuarios")
                                .document(firebaseUID)
                                .set(user);

                        firestore.collection("Refugios")
                                .document(firebaseUID)
                                .set(refugio);

                        // 🔹 Guardar en SQLite
                        user.setContrasena(user.getContrasena()); // si quieres mantenerla local

                        long idLocal = daoAdopcion.insertarUsuario(user);
                        boolean idRegufio = daoAdopcion.insertarRefugio(refugio);

                        if (idLocal != -1 && idRegufio==true) {
                            callback.onSuccess();
                        } else {
                            callback.onError("Error al guardar en SQLite");
                        }
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

                        user.setContrasena("********");
                        user.setFirebaseUID(firebaseUID);
                        adoptante.setFirebaseUID(firebaseUID);

                        firestore.collection("Usuarios")
                                .document(firebaseUID)
                                .set(user);

                        firestore.collection("Adoptantes")
                                .document(firebaseUID)
                                .set(adoptante);

                        long idLocal = daoAdopcion.insertarUsuario(user);
                        boolean idadoptante = daoAdopcion.insertarAdoptante(adoptante);

                        if (idLocal != -1 && idadoptante ==true) {
                            callback.onSuccess();
                        } else {
                            callback.onError("Error al guardar en SQLite");
                        }
                    });
        }
    }
