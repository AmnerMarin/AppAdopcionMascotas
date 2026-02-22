package sistemas.unc.edu.appadopcionmascotas.Model;

public class Conversacion {
    private int idChat;
    private String nombre; // Nombre del Adoptante (si es refugio) o del Refugio (si es adoptante)
    private String ultimoMensaje;
    private String hora;
    private String nombreMascota;
    private int idMascota;

    public Conversacion() {
    }


    public Conversacion(int idChat, String nombre, String ultimoMensaje, String hora, String nombreMascota, int idMascota) {
        this.idChat = idChat;
        this.nombre = nombre;
        this.ultimoMensaje = ultimoMensaje;
        this.hora = hora;
        this.nombreMascota = nombreMascota;
        this.idMascota = idMascota;
    }

    public int getIdChat() {
        return idChat;
    }

    public void setIdChat(int idChat) {
        this.idChat = idChat;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public void setUltimoMensaje(String ultimoMensaje) {
        this.ultimoMensaje = ultimoMensaje;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public int getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(int idMascota) {
        this.idMascota = idMascota;
    }

}
