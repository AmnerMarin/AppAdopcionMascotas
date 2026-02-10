package sistemas.unc.edu.appadopcionmascotas.Model;

public class Mensaje {
    private String nombreUsuario;
    private String emailUsuario;
    private String fecha;
    private String mascotaInteres;
    private String contenidoMensaje;

    public Mensaje(String nombreUsuario, String emailUsuario, String fecha, String mascotaInteres, String contenidoMensaje) {
        this.nombreUsuario = nombreUsuario;
        this.emailUsuario = emailUsuario;
        this.fecha = fecha;
        this.mascotaInteres = mascotaInteres;
        this.contenidoMensaje = contenidoMensaje;
    }

    public String getUsuario() { return nombreUsuario; }
    public String getEmail() { return emailUsuario; }
    public String getFecha() { return fecha; }
    public String getMascotaInteres() { return mascotaInteres; }
    public String getContenido() { return contenidoMensaje; }
}
