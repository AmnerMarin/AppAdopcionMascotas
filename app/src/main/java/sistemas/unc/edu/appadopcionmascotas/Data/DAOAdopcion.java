package sistemas.unc.edu.appadopcionmascotas.Data;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import sistemas.unc.edu.appadopcionmascotas.Model.Adopcion;
import sistemas.unc.edu.appadopcionmascotas.Model.Adoptante;
import sistemas.unc.edu.appadopcionmascotas.Model.Animal;
import sistemas.unc.edu.appadopcionmascotas.Model.Favorito;
import sistemas.unc.edu.appadopcionmascotas.Model.Mensaje;
import sistemas.unc.edu.appadopcionmascotas.Model.Refugio;
import sistemas.unc.edu.appadopcionmascotas.Model.Usuario;

public class DAOAdopcion {
    private String nombreDB;
    private int version;

    private Activity contexto;

    public DAOAdopcion(Activity contexto) {
        nombreDB = "DBAdoptaPet";
        version = 1;
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

        return rpta ; //Es el id
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
            cv.put("tamano",m.getTamano());
            cv.put("foto", m.getFoto());

            long fila = db.insert("Mascota", null, cv);

            if (fila > 0) rpta = true;

            db.close();
        }

        return rpta;
    }

    // =========================
    // INSERTAR ADOPCION
    // =========================
    public boolean insertarAdopcion(Adopcion a) {

        boolean rpta = false;
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);

        SQLiteDatabase db = helper.getWritableDatabase();

        if (db != null) {

            ContentValues cv = new ContentValues();
            cv.put("id_adoptante", a.getIdAdoptante());
            cv.put("id_mascota", a.getIdMascota());
            cv.put("detalles", a.getDetalles());

            long fila = db.insert("Adopcion", null, cv);
            if (fila > 0) rpta = true;

            db.close();
        }

        return rpta;
    }

    // =========================
    // INSERTAR FAVORITO
    // =========================
    public boolean insertarFavorito(Favorito f) {

        boolean rpta = false;
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);

        SQLiteDatabase db = helper.getWritableDatabase();

        if (db != null) {

            ContentValues cv = new ContentValues();
            cv.put("id_adoptante", f.getIdAdoptante());
            cv.put("id_mascota", f.getIdAnimal());

            long fila = db.insert("Favorito_Mascota", null, cv);

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

        SQLiteDatabase db = helper.getWritableDatabase();
        db = helper.getReadableDatabase(); // Solo lectura para buscar
        Usuario usuarioLogueado = null;

        // Consulta: buscamos por correo y contraseña
        Cursor cursor = db.rawQuery(
                "SELECT * FROM Usuario WHERE correo = ? AND contrasena = ?",
                new String[]{correo, contra}
        );

        if (cursor.moveToFirst()) {
            int idusuario=cursor.getInt(0);
            String correo1= cursor.getString(1);
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

    public List<Refugio> listarRefugio(){

        List<Refugio> lista = new ArrayList<>();
        DBConstruir helper = new DBConstruir(contexto,nombreDB,null,version);

        SQLiteDatabase db = helper.getReadableDatabase();

        if(db!=null){

            String sql = "SELECT * FROM Refugio";
            Cursor reg = db.rawQuery(sql,null);

            if(reg.moveToFirst()){
                do{
                    int idUsuario = reg.getInt(1);
                    String nombre = reg.getString(2);
                    String direccion = reg.getString(3);
                    String telefono = reg.getString(4);
                    String descripcion = reg.getString(5);
                    double latitud = reg.getDouble(6);
                    double longitud = reg.getDouble(7);

                    Refugio r = new Refugio(idUsuario,nombre,direccion,telefono,descripcion,latitud,longitud);
                    lista.add(r);

                }while(reg.moveToNext());
            }
            reg.close();
            db.close();
        }
        return lista;
    }

// =====================================

    public List<Animal> listarMascota(){

        List<Animal> lista = new ArrayList<>();
        DBConstruir helper = new DBConstruir(contexto,nombreDB,null,version);

        SQLiteDatabase db = helper.getReadableDatabase();

        if(db!=null){

            String sql = "SELECT * FROM Mascota";
            Cursor reg = db.rawQuery(sql,null);

            if(reg.moveToFirst()){
                do{
                    int idRefugio = reg.getInt(1);
                    String nombre = reg.getString(2);
                    String especie = reg.getString(3);
                    String raza = reg.getString(4);
                    double peso = reg.getDouble(5);
                    String edad = reg.getString(6);
                    String sexo = reg.getString(7);
                    String temp = reg.getString(8);
                    String historia = reg.getString(9);
                    String estado = reg.getString(10);
                    String tamano = reg.getString(11);
                    byte[] foto = reg.getBlob(12);

                    Animal m = new Animal(idRefugio,nombre,especie,raza,peso,edad,sexo,temp,historia,estado,tamano,foto);
                    lista.add(m);

                }while(reg.moveToNext());
            }
            reg.close();
            db.close();
        }

        return lista;
    }
// =====================================
    public List<Adopcion> listarAdopcion(){

        List<Adopcion> lista = new ArrayList<>();
        DBConstruir helper = new DBConstruir(contexto,nombreDB,null,version);

        SQLiteDatabase db = helper.getReadableDatabase();

        if(db!=null){

            String sql = "SELECT * FROM Adopcion";
            Cursor reg = db.rawQuery(sql,null);

            if(reg.moveToFirst()){
                do{

                    int idAdoptante = reg.getInt(2);
                    int idMascota = reg.getInt(3);
                    String detalles = reg.getString(5);
                    Adopcion a = new Adopcion(idAdoptante,idMascota,detalles);
                    lista.add(a);
                }while(reg.moveToNext());
            }
            reg.close();
            db.close();
        }
        return lista;
    }

    public List<Favorito> listarFavorito(){

        List<Favorito> lista = new ArrayList<>();
        DBConstruir helper = new DBConstruir(contexto,nombreDB,null,version);

        SQLiteDatabase db = helper.getReadableDatabase();

        if(db!=null){

            String sql = "SELECT * FROM Favorito_Mascota";
            Cursor reg = db.rawQuery(sql,null);

            if(reg.moveToFirst()){
                do{

                    int idAdoptante = reg.getInt(1);
                    int idMascota = reg.getInt(2);

                    Favorito f = new Favorito(idAdoptante,idMascota);
                    lista.add(f);

                }while(reg.moveToNext());
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
}

