package sistemas.unc.edu.appadopcionmascotas.Model;

public class Adopcion {

    private int idAdoptante;
    private int idMascota;
    private String detalles;

    public Adopcion(int idAdoptante, int idMascota, String detalles) {
        this.idAdoptante = idAdoptante;
        this.idMascota = idMascota;
        this.detalles = detalles;
    }

    public String getDetalles() {
        return detalles;
    }

    public int getIdAdoptante() {
        return idAdoptante;
    }

    public int getIdMascota() {
        return idMascota;
    }
}
