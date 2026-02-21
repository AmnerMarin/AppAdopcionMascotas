package sistemas.unc.edu.appadopcionmascotas.Data;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import sistemas.unc.edu.appadopcionmascotas.Model.Adopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Adoptante;
import sistemas.unc.edu.appadopcionmascotas.Model.Animal;
import sistemas.unc.edu.appadopcionmascotas.Model.Favorito;
import sistemas.unc.edu.appadopcionmascotas.Model.Mensaje;
import sistemas.unc.edu.appadopcionmascotas.Model.Refugio;
import sistemas.unc.edu.appadopcionmascotas.Model.Solicitud;
import sistemas.unc.edu.appadopcionmascotas.Model.Usuario;

public class DAOAdopcion {
    private String nombreDB;
    private int version;

    private Activity contexto;

    public DAOAdopcion(Activity contexto) {
        nombreDB = "DBAdoptaPet";
        version = 3;
        this.contexto = contexto;
    }

    // =========================
    // INSERTAR USUARIO
    // =========================
    public long insertarUsuario(Usuario u) {

        long rpta = -1;
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);

        SQLiteDatabase db = helper.getWritableDatabase();

        if (db != null) {

            ContentValues oColumna = new ContentValues();
            oColumna.put("correo", u.getCorreo());
            oColumna.put("contrasena", u.getContrasena());
            oColumna.put("rol", u.getRol());

            rpta = db.insert("Usuario", null, oColumna);

            db.close();
        }

        return rpta; //Es el id
    }

    // =========================
    // INSERTAR ADOPTANTE
    // =========================
    public boolean insertarAdoptante(Adoptante a) {

        boolean rpta = false;
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);

        SQLiteDatabase db = helper.getWritableDatabase();

        if (db != null) {

            ContentValues cv = new ContentValues();
            cv.put("id_usuario", a.getId_usuario());
            cv.put("nombres", a.getNombres());
            cv.put("apellidos", a.getApellidos());
            cv.put("telefono", a.getTelefono());
            cv.put("direccion", a.getDireccion());

            long fila = db.insert("Adoptante", null, cv);

            if (fila > 0) rpta = true;

            db.close();
        }

        return rpta;
    }

    // =========================
    // INSERTAR REFUGIO
    // =========================
    public boolean insertarRefugio(Refugio r) {

        boolean rpta = false;
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);

        SQLiteDatabase db = helper.getWritableDatabase();

        if (db != null) {

            ContentValues cv = new ContentValues();
            cv.put("id_usuario", r.getIdUsuario());
            cv.put("nombre_refugio", r.getNombre_refugio());
            cv.put("direccion", r.getDireccion());
            cv.put("telefono", r.getTelefono());
            cv.put("descripcion", r.getDesripcion());
            cv.put("latitud", r.getLatitud());
            cv.put("longitud", r.getLongitud());

            long fila = db.insert("Refugio", null, cv);

            if (fila > 0) rpta = true;

            db.close();
        }

        return rpta;
    }


    // =========================
    // INSERTAR MASCOTA
    // =========================
    public boolean insertarMascota(Animal m) {

        boolean rpta = false;
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);

        SQLiteDatabase db = helper.getWritableDatabase();

        if (db != null) {

            ContentValues cv = new ContentValues();
            cv.put("id_refugio", m.getIdRefugio());
            cv.put("nombre", m.getNombre());
            cv.put("especie", m.getEspecie());
            cv.put("raza", m.getRaza());
            cv.put("peso", m.getPeso());
            cv.put("edad", m.getEdad());
            cv.put("sexo", m.getSexo());
            cv.put("temperamento", m.getTemperamento());
            cv.put("historia", m.getHistoria());
            cv.put("estado", m.getEstado());
            cv.put("tamano", m.getTamano());
            cv.put("foto", m.getFoto());

            long fila = db.insert("Mascota", null, cv);

            if (fila > 0) rpta = true;

            db.close();
        }

        return rpta;
    }


    // ==============================
    // INSERTAR MENSAJE
    // ==============================
    public boolean insertar(Mensaje m) {

        boolean rpta = false;

        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getWritableDatabase();

        if (db != null) {

            ContentValues valores = new ContentValues();

            valores.put("id_refugio", m.getIdRefugio());
            valores.put("id_adoptante", m.getIdAdoptante());
            valores.put("id_mascota", m.getIdAnimal());
            valores.put("fecha", m.getFecha());
            valores.put("contenido_mensaje", m.getContenidoMensaje());

            long fila = db.insert("Mensaje", null, valores);

            if (fila > 0) rpta = true;

            db.close();
        }
        return rpta;
    }

    //Para el login
    public Usuario login(String correo, String contra) {
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);

        SQLiteDatabase db = helper.getReadableDatabase(); // Solo lectura para buscar
        Usuario usuarioLogueado = null;

        // Consulta: buscamos por correo y contraseña
        Cursor cursor = db.rawQuery(
                "SELECT * FROM Usuario WHERE correo = ? AND contrasena = ?",
                new String[]{correo, contra}
        );

        if (cursor.moveToFirst()) {
            int idusuario = cursor.getInt(0);
            String correo1 = cursor.getString(1);
            String contraseña = cursor.getString(2);
            String rol = cursor.getString(3);
            usuarioLogueado = new Usuario(idusuario, correo1, contraseña, rol);
        }

        cursor.close();
        db.close();
        return usuarioLogueado;
    }

    //MÉTODOS PARA LISTAR

// =====================================

    public List<Refugio> listarRefugio() {

        List<Refugio> lista = new ArrayList<>();
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);

        SQLiteDatabase db = helper.getReadableDatabase();

        if (db != null) {

            String sql = "SELECT * FROM Refugio";
            Cursor reg = db.rawQuery(sql, null);

            if (reg.moveToFirst()) {
                do {
                    int idUsuario = reg.getInt(1);
                    String nombre = reg.getString(2);
                    String direccion = reg.getString(3);
                    String telefono = reg.getString(4);
                    String descripcion = reg.getString(5);
                    double latitud = reg.getDouble(6);
                    double longitud = reg.getDouble(7);

                    Refugio r = new Refugio(idUsuario, nombre, direccion, telefono, descripcion, latitud, longitud);
                    lista.add(r);

                } while (reg.moveToNext());
            }
            reg.close();
            db.close();
        }
        return lista;
    }

// =====================================

    public List<Animal> listarMascota() {
        List<Animal> lista = new ArrayList<>();
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor reg = db.rawQuery("SELECT * FROM Mascota WHERE estado = 'Disponible'", null);

        while (reg.moveToNext()) {
            Animal m = new Animal();
            m.setIdMascota(reg.getInt(0));   // id_mascota
            m.setIdRefugio(reg.getInt(1));  // id_refugio
            m.setNombre(reg.getString(2));   // nombre
            m.setEspecie(reg.getString(3));  // especie
            m.setRaza(reg.getString(4));     // raza
            m.setPeso(reg.getDouble(5));     // peso
            m.setEdad(reg.getString(6));     // edad
            m.setSexo(reg.getString(7));     // sexo
            m.setTemperamento(reg.getString(8)); // temperamento
            m.setHistoria(reg.getString(9)); // historia
            m.setEstado(reg.getString(10));  // estado
            m.setTamano(reg.getString(11));  // tamano
            m.setFoto(reg.getBlob(12));      // foto

            lista.add(m);
        }

        reg.close();
        db.close();
        return lista;
    }

    // =====================================
    public List<Adopcion> listarAdopcion() {

        List<Adopcion> lista = new ArrayList<>();
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);

        SQLiteDatabase db = helper.getReadableDatabase();

        if (db != null) {

            String sql = "SELECT * FROM Adopcion";
            Cursor reg = db.rawQuery(sql, null);

            if (reg.moveToFirst()) {
                do {

                    int idAdoptante = reg.getInt(2);
                    int idMascota = reg.getInt(3);
                    String detalles = reg.getString(5);
                    Adopcion a = new Adopcion(idAdoptante, idMascota, detalles);
                    lista.add(a);
                } while (reg.moveToNext());
            }
            reg.close();
            db.close();
        }
        return lista;
    }

    public List<Favorito> listarFavorito() {

        List<Favorito> lista = new ArrayList<>();
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);

        SQLiteDatabase db = helper.getReadableDatabase();

        if (db != null) {

            String sql = "SELECT * FROM Favorito_Mascota";
            Cursor reg = db.rawQuery(sql, null);

            if (reg.moveToFirst()) {
                do {

                    int idAdoptante = reg.getInt(1);
                    int idMascota = reg.getInt(2);

                    Favorito f = new Favorito(idAdoptante, idMascota);
                    lista.add(f);

                } while (reg.moveToNext());
            }
            reg.close();
            db.close();
        }
        return lista;
    }

    public List<Mensaje> listarMensaje() {

        List<Mensaje> lista = new ArrayList<>();

        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);

        SQLiteDatabase db = helper.getReadableDatabase();

        if (db != null) {

            String sql = "SELECT * FROM Mensaje";

            Cursor registro = db.rawQuery(sql, null);

            if (registro.moveToFirst()) {

                do {

                    int idRefugio = registro.getInt(1);
                    int idAdoptante = registro.getInt(2);
                    int idMascota = registro.getInt(3);
                    String fecha = registro.getString(4);
                    String contenido = registro.getString(5);

                    Mensaje m = new Mensaje(
                            idRefugio,
                            idAdoptante,
                            idMascota,
                            fecha,
                            contenido
                    );

                    lista.add(m);

                } while (registro.moveToNext());
            }

            registro.close();
            db.close();
        }
        return lista;
    }

    //OBETNER IDs
    public int obtenerIdRefugioPorUsuario(int idUsuario) {
        int idRefugio = -1;
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id_refugio FROM Refugio WHERE id_usuario = ?",
                new String[]{String.valueOf(idUsuario)}
        );

        if (cursor.moveToFirst()) {
            idRefugio = cursor.getInt(0);
        }

        cursor.close();
        return idRefugio;
    }

    //OBTENER ID DE ADOPTANTE POR USUARIO
    public int obtenerIdAdoptantePorUsuario(int idUsuario) {
        int idAdoptante = -1;
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id_adoptante FROM Adoptante WHERE id_usuario = ?",
                new String[]{String.valueOf(idUsuario)}
        );

        if (cursor.moveToFirst()) {
            idAdoptante = cursor.getInt(0);
        }

        cursor.close();
        return idAdoptante;
    }

    public List<Animal> listarAnimalesPorRefugio(int idRefugio) {
        List<Animal> lista = new ArrayList<>();
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.rawQuery(
                "SELECT * FROM Mascota WHERE id_refugio=?",
                new String[]{String.valueOf(idRefugio)}
        );

        while (c.moveToNext()) {
            Animal a = new Animal();
            a.setIdMascota(c.getInt(0));    // id_mascota
            a.setIdRefugio(c.getInt(1));    // id_refugio
            a.setNombre(c.getString(2));    // nombre
            a.setEspecie(c.getString(3));   // especie
            a.setRaza(c.getString(4));      // raza
            a.setPeso(c.getDouble(5));      // peso
            a.setEdad(c.getString(6));      // edad
            a.setSexo(c.getString(7));      // sexo
            a.setTemperamento(c.getString(8)); // temperamento
            a.setHistoria(c.getString(9));  // historia
            a.setEstado(c.getString(10));   // estado
            a.setTamano(c.getString(11));   // tamano
            a.setFoto(c.getBlob(12));       // foto

            lista.add(a);
        }

        c.close();
        db.close();
        return lista;
    }

    // =============================
// OBTENER CANTIDAD DE ANIMALES POR REFUGIO
// =============================
    public int obtenerCantidadAnimalesPorRefugio(int idRefugio) {
        int cantidad = 0;

        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Mascota WHERE id_refugio = ?",
                new String[]{String.valueOf(idRefugio)}
        );

        if (cursor.moveToFirst()) {
            cantidad = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return cantidad;
    }

    //---PARA VER LOS DETALLES DE CADA ANIMAL , USUARIO ADOPTANTE ----
    public Animal obtenerDetalleAnimalConRefugio(int idAnimal) {
        Animal animal = null;
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getReadableDatabase();

        String query = "SELECT " +
                "M.id_mascota, M.id_refugio, M.nombre, M.especie, M.raza, M.peso, M.edad, M.sexo, M.temperamento, " +
                "M.historia, M.estado, M.tamano, M.foto, " +
                "R.nombre_refugio, R.direccion, R.telefono " +
                "FROM Mascota M " +
                "LEFT JOIN Refugio R ON M.id_refugio = R.id_refugio " +
                "WHERE M.id_mascota = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(idAnimal)});

        if (cursor.moveToFirst()) {
            animal = new Animal();
            animal.setIdMascota(cursor.getInt(0));
            animal.setIdRefugio(cursor.getInt(1));
            animal.setNombre(cursor.getString(2));
            animal.setEspecie(cursor.getString(3));
            animal.setRaza(cursor.getString(4));
            animal.setPeso(cursor.getDouble(5));
            animal.setEdad(cursor.getString(6));
            animal.setSexo(cursor.getString(7));
            animal.setTemperamento(cursor.getString(8)); // "Juguetón,Activo,Amigable"
            animal.setHistoria(cursor.getString(9));
            animal.setEstado(cursor.getString(10));
            animal.setTamano(cursor.getString(11));
            animal.setFoto(cursor.getBlob(12));
            animal.setNombreRefugio(cursor.getString(13));
            animal.setDireccionRefugio(cursor.getString(14));
            animal.setTelefonoRefugio(cursor.getString(15));
        }

        cursor.close();
        db.close();
        return animal;
    }

    // FAVORTIOS GUARFDAR EN B ASE DE DAROS

    public boolean esFavorito(int idAdoptante, int idMascota) {
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT 1 FROM Favorito_Mascota WHERE id_adoptante = ? AND id_mascota = ?",
                new String[]{String.valueOf(idAdoptante), String.valueOf(idMascota)});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return existe;
    }

    // Agrega a favoritos
    public boolean agregarFavorito(int idAdoptante, int idMascota) {
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_adoptante", idAdoptante);
        values.put("id_mascota", idMascota);
        long res = db.insert("Favorito_Mascota", null, values);
        db.close();
        return res != -1;
    }

    // Elimina de favoritos
    public boolean eliminarFavorito(int idAdoptante, int idMascota) {
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getWritableDatabase();

        int res = db.delete("Favorito_Mascota", "id_adoptante = ? AND id_mascota = ?",
                new String[]{String.valueOf(idAdoptante), String.valueOf(idMascota)});
        db.close();
        return res > 0;
    }

    // ==============================
// OBTENER MASCOTAS FAVORITAS DE UN ADOPTANTE
// ==============================
    public List<Animal> obtenerFavoritosPorAdoptante(int idAdoptante) {
        List<Animal> lista = new ArrayList<>();
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getReadableDatabase();

        // Seleccionamos todos los datos de la mascota que estén en la tabla de favoritos
        String sql = "SELECT m.* FROM Mascota m " +
                "INNER JOIN Favorito_Mascota f ON m.id_mascota = f.id_mascota " +
                "WHERE f.id_adoptante = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(idAdoptante)});

        if (cursor.moveToFirst()) {
            do {
                Animal a = new Animal();
                a.setIdMascota(cursor.getInt(0));
                a.setIdRefugio(cursor.getInt(1));
                a.setNombre(cursor.getString(2));
                a.setEspecie(cursor.getString(3));
                a.setRaza(cursor.getString(4));
                a.setPeso(cursor.getDouble(5));
                a.setEdad(cursor.getString(6));
                a.setSexo(cursor.getString(7));
                a.setTemperamento(cursor.getString(8));
                a.setHistoria(cursor.getString(9));
                a.setEstado(cursor.getString(10));
                a.setTamano(cursor.getString(11));
                a.setFoto(cursor.getBlob(12));

                lista.add(a);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }

    //MÉTODOS PARA ELIMINAR
    public boolean eliminarAnimal(int id) {
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getWritableDatabase();
        int res = db.delete("Mascota", "id_mascota = ?", new String[]{String.valueOf(id)});
        db.close();
        return res > 0;
    }

    // ==============================
    // ACTUALIZAR MASCOTA
    // ==============================
    public boolean actualizarMascota(int idMascota, Animal m) {
        boolean rpta = false;
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getWritableDatabase();

        if (db != null) {
            ContentValues cv = new ContentValues();
            cv.put("nombre", m.getNombre());
            cv.put("especie", m.getEspecie());
            cv.put("raza", m.getRaza());
            cv.put("peso", m.getPeso());
            cv.put("edad", m.getEdad());
            cv.put("sexo", m.getSexo());
            cv.put("temperamento", m.getTemperamento());
            cv.put("historia", m.getHistoria());
            cv.put("estado", m.getEstado() != null ? m.getEstado() : "Disponible");
            cv.put("tamano", m.getTamano());
            cv.put("foto", m.getFoto());

            int filas = db.update("Mascota", cv, "id_mascota = ?", new String[]{String.valueOf(idMascota)});
            rpta = filas > 0;
            db.close();
        }
        return rpta;
    }

    //Metodo para obtener el perfil del refugio y mostrar sus datos
    public Refugio obtenerPerfilRefugio(int idUsuario) {
        Refugio refugio = null;
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getWritableDatabase();

        // Consulta uniendo (JOIN) Usuario y Refugio si necesitas datos de ambos
        Cursor cursor = db.rawQuery(
                "SELECT * FROM Refugio WHERE id_usuario = ?",
                new String[]{String.valueOf(idUsuario)}
        );

        if (cursor.moveToFirst()) {
            refugio = new Refugio();
            // Los índices dependen del orden en tu CREATE TABLE de DBHelper
            refugio.setNombre_refugio(cursor.getString(2));
            refugio.setDireccion(cursor.getString(3));
            refugio.setTelefono(cursor.getString(4));
            refugio.setDesripcion(cursor.getString(5));
        }

        cursor.close();
        return refugio;
    }

    //Metodo para obtener el perfil del adoptante y mostrar sus datos
    public Adoptante obtenerPerfilAdoptante(int idUsuario) {
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getWritableDatabase();
        Adoptante adoptante = null;

        // Consulta
        Cursor cursor = db.rawQuery(
                "SELECT * FROM Adoptante WHERE id_usuario = ?",
                new String[]{String.valueOf(idUsuario)}
        );

        if (cursor.moveToFirst()) {
            adoptante = new Adoptante();
            // Los índices dependen del orden en tu CREATE TABLE de DBHelper
            adoptante.setNombres(cursor.getString(2));
            adoptante.setApellidos(cursor.getString(3));
            adoptante.setTelefono(cursor.getString(4));
            adoptante.setDireccion(cursor.getString(5));
        }

        cursor.close();
        return adoptante;
    }

    //PARA MOSTRAR LAS ESTADISTICAS DEL DASHBOARD DEL REFUGIO
    public Map<String, Integer> obtenerEstadisticasDashboard(int idRefugio) {


        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);

        Map<String, Integer> stats = new HashMap<>();
        // Inicializar por defecto para evitar NullPointerException
        stats.put("publicados", 0);
        stats.put("adoptados", 0);
        stats.put("activos", 0);
        SQLiteDatabase db = helper.getReadableDatabase();

        // 1. Total Publicados
        Cursor c1 = db.rawQuery("SELECT COUNT(*) FROM Mascota WHERE id_refugio = ?", new String[]{String.valueOf(idRefugio)});
        if (c1.moveToFirst()) stats.put("publicados", c1.getInt(0));
        c1.close();

        // 2. Total Adoptados (Solicitudes Aprobadas)
        Cursor c2 = db.rawQuery("SELECT COUNT(*) FROM Adopcion a INNER JOIN Mascota m ON a.id_mascota = m.id_mascota " +
                "WHERE m.id_refugio = ? AND a.estado = 'Aprobada'", new String[]{String.valueOf(idRefugio)});
        if (c2.moveToFirst()) stats.put("adoptados", c2.getInt(0));
        c2.close();

        // 3. Activos (Mascotas que NO tienen ninguna solicitud de adopción aún)
        // Usamos un NOT IN para ver cuáles no están en la tabla Adopcion
        Cursor c3 = db.rawQuery("SELECT COUNT(*) FROM Mascota WHERE id_refugio = ? AND id_mascota NOT IN (SELECT id_mascota FROM Adopcion)",
                new String[]{String.valueOf(idRefugio)});
        if (c3.moveToFirst()) stats.put("activos", c3.getInt(0));
        c3.close();

        return stats;
    }


    //--SOLICITUDES ---
    //VERIFICAR SI EXISTE UNA SOLICITUD
    public boolean existeSolicitud(int idAdoptante, int idMascota) {

        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id_adopcion FROM Adopcion WHERE id_adoptante = ? AND id_mascota = ?",
                new String[]{String.valueOf(idAdoptante), String.valueOf(idMascota)}
        );

        boolean existe = cursor.moveToFirst();
        cursor.close();
        db.close();

        return existe;
    }

    //METODO PARA INSERTAR SOLICITUD
    public boolean insertarSolicitud(int idAdoptante, int idMascota) {

        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getWritableDatabase();
        // Obtener la fecha actual
        String fechaHoraFormateada = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        ContentValues values = new ContentValues();
        values.put("id_adoptante", idAdoptante);
        values.put("id_mascota", idMascota);
        values.put("fecha_adopcion", fechaHoraFormateada);
        values.put("estado", "Pendiente");

        long resultado = db.insert("Adopcion", null, values);

        db.close();

        return resultado != -1;
    }

    //METODO PARA APROBAR SOLICITUDES DE ADOPCION
    public void aprobarSolicitud(int idAdopcion, int idMascota) {
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getWritableDatabase();

        // Iniciamos una transacción para asegurarnos de que se cumplan ambas o ninguna
        db.beginTransaction();
        try {
            // 1. Cambiar estado de la solicitud de adopción a "Aprobada"
            ContentValues valuesAdopcion = new ContentValues();
            valuesAdopcion.put("estado", "Aprobada");
            db.update("Adopcion", valuesAdopcion, "id_adopcion = ?", new String[]{String.valueOf(idAdopcion)});

            // 2. Cambiar estado del animal a "Adoptado"
            ContentValues valuesMascota = new ContentValues();
            valuesMascota.put("estado", "Adoptado");
            db.update("Mascota", valuesMascota, "id_mascota = ?", new String[]{String.valueOf(idMascota)});

            db.setTransactionSuccessful(); // Marca la transacción como exitosa
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    //METODO PARA RECHAZAR ADOPCION
    public void rechazarSolicitud(int idAdopcion) {

        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("estado", "Rechazada");

        db.update("Adopcion", values,
                "id_adopcion = ?",
                new String[]{String.valueOf(idAdopcion)});

        db.close();
    }

    //METODO PARA LISTAR SOLICITUDES QUE LLE LLEGAN AL REFUGIO
    public List<Solicitud> obtenerSolicitudesDelRefugio(int idRefugio) {
        List<Solicitud> lista = new ArrayList<>();
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getReadableDatabase();

        // Agregamos ad.nombres y ad.apellidos a la consulta
        String sql = "SELECT a.id_adopcion, m.nombre, a.fecha_adopcion, (ad.nombres || ' ' || ad.apellidos) as nombre_completo, a.estado " +
                "FROM Adopcion a " +
                "INNER JOIN Mascota m ON a.id_mascota = m.id_mascota " +
                "INNER JOIN Adoptante ad ON a.id_adoptante = ad.id_adoptante " +
                "WHERE m.id_refugio = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(idRefugio)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String mascota = cursor.getString(1);
                String fecha = cursor.getString(2);
                String adoptante = cursor.getString(3);
                String estado = cursor.getString(4);

                // ORDEN CORRECTO SEGÚN TU MODELO:
                // idAdopcion, nombreMascota, nombreAdoptante, fecha, estado
                lista.add(new Solicitud(id, mascota, adoptante, fecha, estado));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    //METODO PARA OBTENER SOLICITUDES QUE ENVIA EL ADOPTANTE
    public List<Solicitud> obtenerMisSolicitudes(int idAdoptante) {
        List<Solicitud> lista = new ArrayList<>();
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);
        SQLiteDatabase db = helper.getReadableDatabase();

        // Consulta que une Adopcion con Mascota (para foto/nombre) y Refugio (para el nombre del refugio)
        String sql = "SELECT a.id_adopcion, m.nombre AS nombre_mascota, r.nombre_refugio, " +
                "a.fecha_adopcion, a.estado, m.foto " +
                "FROM Adopcion a " +
                "INNER JOIN Mascota m ON a.id_mascota = m.id_mascota " +
                "INNER JOIN Refugio r ON m.id_refugio = r.id_refugio " +
                "WHERE a.id_adoptante = ? " +
                "ORDER BY a.id_adopcion DESC";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(idAdoptante)});

        if (cursor.moveToFirst()) {
            do {
                Solicitud s = new Solicitud();
                s.setIdAdopcion(cursor.getInt(0));
                s.setNombreMascota(cursor.getString(1));
                s.setNombreAdoptante(cursor.getString(2)); // Aquí usamos este campo para el nombre del Refugio
                s.setFecha(cursor.getString(3));
                s.setEstado(cursor.getString(4));
                s.setFotoMascota(cursor.getBlob(5)); // Asegúrate de tener este campo en tu clase Solicitud
                lista.add(s);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
}

