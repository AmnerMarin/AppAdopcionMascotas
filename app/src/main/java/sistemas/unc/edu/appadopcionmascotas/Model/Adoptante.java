package sistemas.unc.edu.appadopcionmascotas.Model;

public class Adoptante {
    private int id_adoptante;
    private int id_usuario;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String direccion;

    private String FirebaseUID;

    // Constructor vacío
    public Adoptante() {
    }

    // Constructor para el Registro (el que usaremos ahora)


    public Adoptante(int id_usuario, String nombres, String apellidos, String telefono, String direccion) {
        this.id_usuario = id_usuario;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.direccion = direccion;

    }

    // Getters y Setters
    public int getId_adoptante() { return id_adoptante; }
    public void setId_adoptante(int id_adoptante) { this.id_adoptante = id_adoptante; }

    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getFirebaseUID() {
        return FirebaseUID;
    }

    public void setFirebaseUID(String firebaseUID) {
        FirebaseUID = firebaseUID;
    }
}
