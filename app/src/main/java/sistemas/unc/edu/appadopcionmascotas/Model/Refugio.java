package sistemas.unc.edu.appadopcionmascotas.Model;

public class Refugio {
    private int idUsuario;
    private String nombre_refugio;
    private String desripcion;
    private String direccion;
    private String telefono;
    private double latitud;
    private double longitud;

    public Refugio(int idUsuario, String nombre_refugio, String desripcion, String direccion, String telefono, double latitud, double longitud) {
        this.idUsuario = idUsuario;
        this.nombre_refugio = nombre_refugio;
        this.desripcion = desripcion;
        this.direccion = direccion;
        this.telefono = telefono;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Refugio() {
    }


    public String getNombre_refugio() {
        return nombre_refugio;
    }

    public String getDesripcion() {
        return desripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setNombre_refugio(String nombre_refugio) {
        this.nombre_refugio = nombre_refugio;
    }

    public void setDesripcion(String desripcion) {
        this.desripcion = desripcion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}

