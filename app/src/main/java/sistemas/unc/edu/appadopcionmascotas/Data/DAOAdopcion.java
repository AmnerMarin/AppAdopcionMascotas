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
    public boolean insertarUsuario(Usuario u) {

        boolean rpta = false;
        DBConstruir helper = new DBConstruir(contexto, nombreDB, null, version);

        SQLiteDatabase db = helper.getWritableDatabase();

        if (db != null) {

            ContentValues oColumna = new ContentValues();
            oColumna.put("correo", u.getCorreo());
            oColumna.put("contrasena", u.getContrasena());
            oColumna.put("rol", u.getRol());

            long fila = db.insert("Usuario", null, oColumna);

            if (fila > 0) rpta = true;

            db.close();
        }

        return rpta;
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
            cv.put("nombre", m.getNombre());
            cv.put("especie", m.getEspecie());
            cv.put("raza", m.getRaza());
            cv.put("peso", m.getPeso());
            cv.put("edad", m.getEdad());
            cv.put("sexo", m.getSexo());
            cv.put("temperamento", m.getTemperamento());
            cv.put("historia", m.getHistoria());
            cv.put("estado", m.getEstado());
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
            cv.put("detalles", a.getDetalles());

            long fila = db.insert("Adopcion", null, cv);

            if (fila > 0) rpta = true;

            db.close();
        }

        return rpta;
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
                    String nombre = reg.getString(2);
                    String direccion = reg.getString(3);
                    String telefono = reg.getString(4);
                    String descripcion = reg.getString(5);

                    Refugio r = new Refugio(id,idUsuario,nombre,direccion,telefono,descripcion);
                    lista.add(r);

                }while(reg.moveToNext());
            }

            reg.close();
            db.close();
        }

        return lista;
    }

// =====================================

    public List<Mascota> listarMascota(){

        List<Mascota> lista = new ArrayList<>();
        BDConstruir helper = new BDConstruir(contexto,nombreBD,null,version);

        SQLiteDatabase db = helper.getReadableDatabase();

        if(db!=null){

            String sql = "SELECT * FROM Mascota";
            Cursor reg = db.rawQuery(sql,null);

            if(reg.moveToFirst()){
                do{

                    int id = reg.getInt(0);
                    int idRefugio = reg.getInt(1);
                    String nombre = reg.getString(2);
                    String especie = reg.getString(3);
                    String raza = reg.getString(4);
                    double peso = reg.getDouble(5);
                    String edad = reg.getString(6);
                    String temp = reg.getString(7);
                    String historia = reg.getString(8);
                    String estado = reg.getString(9);
                    byte[] foto = reg.getBlob(10);

                    Mascota m = new Mascota(id,idRefugio,nombre,especie,raza,peso,edad,temp,historia,estado,foto);
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
        BDConstruir helper = new BDConstruir(contexto,nombreBD,null,version);

        SQLiteDatabase db = helper.getReadableDatabase();

        if(db!=null){

            String sql = "SELECT * FROM Adopcion";
            Cursor reg = db.rawQuery(sql,null);

            if(reg.moveToFirst()){
                do{

                    int id = reg.getInt(0);
                    int idAdoptante = reg.getInt(1);
                    int idMascota = reg.getInt(2);
                    int idRefugio = reg.getInt(3);
                    String detalles = reg.getString(5);

                    Adopcion a = new Adopcion(id,idAdoptante,idMascota,idRefugio,detalles);
                    lista.add(a);

                }while(reg.moveToNext());
            }

            reg.close();
            db.close();
        }

        return lista;
    }

// =====================================

    public List<FavoritoMascota> listarFavorito(){

        List<FavoritoMascota> lista = new ArrayList<>();
        BDConstruir helper = new BDConstruir(contexto,nombreBD,null,version);

        SQLiteDatabase db = helper.getReadableDatabase();

        if(db!=null){

            String sql = "SELECT * FROM Favorito_Mascota";
            Cursor reg = db.rawQuery(sql,null);

            if(reg.moveToFirst()){
                do{

                    int id = reg.getInt(0);
                    int idAdoptante = reg.getInt(1);
                    int idMascota = reg.getInt(2);

                    FavoritoMascota f = new FavoritoMascota(id,idAdoptante,idMascota);
                    lista.add(f);

                }while(reg.moveToNext());
            }

            reg.close();
            db.close();
        }

        return lista;
    }

}

