package sistemas.unc.edu.appadopcionmascotas.Model;

public class Mensaje {
    private int idMensaje;
    private int idChat;
    private int idEmisor; // Este es el id_usuario
    private String texto;
    private String fechaEnvio;

    public Mensaje() {
    }


    public Mensaje(int idEmisor, String texto) {
        this.idEmisor = idEmisor;
        this.texto = texto;
    }

    // Getters
    public int getIdEmisor() { return idEmisor; }
    public String getTexto() { return texto; }
    public String getFechaEnvio() { return fechaEnvio; }

    // Setters
    public void setIdEmisor(int idEmisor) { this.idEmisor = idEmisor; }
    public void setTexto(String texto) { this.texto = texto; }
    public void setFechaEnvio(String fechaEnvio) { this.fechaEnvio = fechaEnvio; }
}
