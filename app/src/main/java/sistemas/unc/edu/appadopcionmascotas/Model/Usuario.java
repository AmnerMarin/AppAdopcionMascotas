package sistemas.unc.edu.appadopcionmascotas.Model;

public class Usuario {
    private int idusuario;
    private String correo;
    private String contrasena;
    private String rol;
    private String FirebaseUID;

    public Usuario(){

    }

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

    public String getFirebaseUID() {
        return FirebaseUID;
    }

    public void setFirebaseUID(String firebaseUID) {
        FirebaseUID = firebaseUID;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setIdusuario(int idusuario) {
        this.idusuario = idusuario;
    }
}
