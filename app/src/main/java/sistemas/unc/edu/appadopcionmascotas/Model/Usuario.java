package sistemas.unc.edu.appadopcionmascotas.Model;

public class Usuario {
    private int idusuario;
    private String correo;
    private String contrasena;
    private String rol;


    public Usuario(int idusuario, String correo, String contrasena, String rol) {
        this.idusuario = idusuario;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

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

    public int getIdusuario() {
        return idusuario;
    }
}
