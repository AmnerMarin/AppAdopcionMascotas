package sistemas.unc.edu.appadopcionmascotas.Model;

public class Usuario {
    private String correo;
    private String contrasena;
    private String rol;


    public Usuario(String correo, String contrasena, String rol) {
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public String getCorreo() {
        return correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getRol() {
        return rol;
    }

}
