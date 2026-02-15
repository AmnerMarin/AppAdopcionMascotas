package sistemas.unc.edu.appadopcionmascotas.Model;

public class Favorito {
    private int idAdoptante;
    private int idAnimal;

    public Favorito(int idAdoptante, int idAnimal) {
        this.idAdoptante = idAdoptante;
        this.idAnimal = idAnimal;
    }

    public int getIdAdoptante() {
        return idAdoptante;
    }

    public int getIdAnimal() {
        return idAnimal;
    }
}
