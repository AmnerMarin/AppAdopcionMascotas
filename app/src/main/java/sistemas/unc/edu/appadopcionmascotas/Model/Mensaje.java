package sistemas.unc.edu.appadopcionmascotas.Model;

public class Mensaje {
    private int idRefugio;
    private int idAdoptante;

    private int idAnimal;
    private String fecha;
    private String contenidoMensaje;

    public Mensaje(int idRefugio, int idAdoptante, int idAnimal, String fecha, String contenidoMensaje) {
        this.idRefugio = idRefugio;
        this.idAdoptante = idAdoptante;
        this.idAnimal = idAnimal;
        this.fecha = fecha;
        this.contenidoMensaje = contenidoMensaje;
    }

    public int getIdRefugio() {
        return idRefugio;
    }

    public int getIdAdoptante() {
        return idAdoptante;
    }

    public int getIdAnimal() {
        return idAnimal;
    }

    public String getFecha() {
        return fecha;
    }

    public String getContenidoMensaje() {
        return contenidoMensaje;
    }
}
