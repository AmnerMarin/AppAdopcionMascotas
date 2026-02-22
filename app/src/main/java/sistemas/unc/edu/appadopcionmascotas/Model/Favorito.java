package sistemas.unc.edu.appadopcionmascotas.Model;

public class Favorito {
    private int idAdoptante;
    private int idAnimal;
    private String FirebaseUID;

    public Favorito(){

    }
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

    public String getFirebaseUID() {
        return FirebaseUID;
    }

    public void setFirebaseUID(String firebaseUID) {
        FirebaseUID = firebaseUID;
    }
}
