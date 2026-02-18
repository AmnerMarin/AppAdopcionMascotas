package sistemas.unc.edu.appadopcionmascotas.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBConstruir extends SQLiteOpenHelper {

    // TABLA USUARIO
    String tabla_usuario = "CREATE TABLE Usuario(" +
            "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT," +
            "correo VARCHAR(100)," +
            "contrasena VARCHAR(255)," +
            "rol VARCHAR(20)," +
            "fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ")";

    // TABLA ADOPTANTE
    String tabla_adoptante = "CREATE TABLE Adoptante(" +
            "id_adoptante INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id_usuario INTEGER NOT NULL," +
            "nombres VARCHAR(100)," +
            "apellidos VARCHAR(100)," +
            "telefono VARCHAR(9)," +
            "direccion VARCHAR(200)," +
            "FOREIGN KEY(id_usuario) REFERENCES Usuario(id_usuario)" +
            ")";

    // TABLA REFUGIO
    String tabla_refugio = "CREATE TABLE Refugio(" +
            "id_refugio INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id_usuario INTEGER NOT NULL," +
            "nombre_refugio VARCHAR(150)," +
            "direccion VARCHAR(200)," +
            "telefono VARCHAR(20)," +
            "descripcion VARCHAR(4000),"+
            "latitud REAL," +
            "longitud REAL," +
            "FOREIGN KEY(id_usuario) REFERENCES Usuario(id_usuario)" +
            ")";

    // TABLA MASCOTA
    String tabla_mascota = "CREATE TABLE Mascota(" +
            "id_mascota INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id_refugio INTEGER NOT NULL," +
            "nombre VARCHAR(100)," +
            "especie VARCHAR(50)," +
            "raza VARCHAR(100)," +
            "peso REAL," +
            "edad VARCHAR(50)," +
            "sexo VARCHAR(10)," +
            "temperamento VARCHAR(150)," +
            "historia VARCHAR(2000)," +
            "estado VARCHAR(15)," +
            "tamano VARCHAR(10)," +
            "foto BLOB," +
            "FOREIGN KEY(id_refugio) REFERENCES Refugio(id_refugio)" +
            ")";

    // TABLA ADOPCION
    String tabla_adopcion = "CREATE TABLE Adopcion(" +
            "id_adopcion INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id_adoptante INTEGER NOT NULL," +
            "id_mascota INTEGER NOT NULL," +
            "fecha_adopcion DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "detalles VARCHAR(1000),"+
            "FOREIGN KEY(id_adoptante) REFERENCES Adoptante(id_adoptante)," +
            "FOREIGN KEY(id_mascota) REFERENCES Mascota(id_mascota)" +
            ")";

    // TABLA FAVORITO
    String tabla_favorito = "CREATE TABLE Favorito_Mascota(" +
            "id_favorito INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id_adoptante INTEGER NOT NULL," +
            "id_mascota INTEGER NOT NULL," +
            "FOREIGN KEY(id_adoptante) REFERENCES Adoptante(id_adoptante)," +
            "FOREIGN KEY(id_mascota) REFERENCES Mascota(id_mascota)" +
            ")";

    String tabla_mensaje = "CREATE TABLE Mensaje(" +
            "id_mensaje INTEGER PRIMARY KEY AUTOINCREMENT," +
            "id_refugio INTEGER NOT NULL," +
            "id_adoptante INTEGER NOT NULL," +
            "id_mascota INTEGER NOT NULL," +
            "fecha DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "contenido_mensaje VARCHAR(4000))";

    public DBConstruir(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tabla_usuario);
        db.execSQL(tabla_adoptante);
        db.execSQL(tabla_refugio);
        db.execSQL(tabla_mascota);
        db.execSQL(tabla_adopcion);
        db.execSQL(tabla_favorito);
        db.execSQL(tabla_mensaje);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Mensaje");
        db.execSQL("DROP TABLE IF EXISTS Favorito_Mascota");
        db.execSQL("DROP TABLE IF EXISTS Adopcion");
        db.execSQL("DROP TABLE IF EXISTS Mascota");
        db.execSQL("DROP TABLE IF EXISTS Refugio");
        db.execSQL("DROP TABLE IF EXISTS Adoptante");
        db.execSQL("DROP TABLE IF EXISTS Usuario"); //TABLA PADRE (Para la eliminación)

        db.execSQL(tabla_usuario);
        db.execSQL(tabla_adoptante);
        db.execSQL(tabla_refugio);
        db.execSQL(tabla_mascota);
        db.execSQL(tabla_adopcion);
        db.execSQL(tabla_favorito);
        db.execSQL(tabla_mensaje);
    }
}
