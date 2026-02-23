package sistemas.unc.edu.appadopcionmascotas.Model;

public class Solicitud {
    private int idAdopcion;
    private int idMascota;
    private String nombreMascota;
    private String nombreAdoptante;
    private String fecha;
    private String estado;
    private byte[] fotoMascota;

    private String FirebaseUID;

    public Solicitud() {
    }

    public Solicitud(int idAdopcion, String nombreMascota,
                     String nombreAdoptante, String fecha, String estado) {
        this.idAdopcion = idAdopcion;
        this.nombreMascota = nombreMascota;
        this.nombreAdoptante = nombreAdoptante;
        this.fecha = fecha;
        this.estado = estado;
    }

    public int getIdAdopcion() { return idAdopcion; }
    public void setIdAdopcion(int idAdopcion) { this.idAdopcion = idAdopcion; }
    public String getNombreMascota() { return nombreMascota; }
    public void setNombreMascota(String nombreMascota) { this.nombreMascota = nombreMascota; }
    public String getNombreAdoptante() { return nombreAdoptante; }
    public void setNombreAdoptante(String nombreAdoptante) { this.nombreAdoptante = nombreAdoptante; }
    public String getFecha(){ return fecha;}
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public byte[] getFotoMascota() { return fotoMascota; }
    public void setFotoMascota(byte[] fotoMascota) { this.fotoMascota = fotoMascota; }
    public int getIdMascota() { return idMascota; }
    public void setIdMascota(int idMascota) { this.idMascota = idMascota; }

    public String getFirebaseUID() {
        return FirebaseUID;
    }

    public void setFirebaseUID(String firebaseUID) {
        FirebaseUID = firebaseUID;
    }
}
