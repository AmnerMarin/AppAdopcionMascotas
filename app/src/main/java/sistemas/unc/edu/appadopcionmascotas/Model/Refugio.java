package sistemas.unc.edu.appadopcionmascotas.Model;

public class Refugio {

    private String nombre_refugio;
    private String desripcion;
    private String direccion;
    private String telefono;

    public Refugio(String nombre_refugio, String desripcion, String direccion, String telefono) {
        this.nombre_refugio = nombre_refugio;
        this.desripcion = desripcion;
        this.direccion = direccion;
        this.telefono = telefono;
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
}
