package sistemas.unc.edu.appadopcionmascotas.Model;

public class Animal {
    public String nombre;
    public String raza;
    public String especie;
    public String edad;
    public String sexo;
    public byte[] foto;
    public boolean favorito;


    public Animal(String nombre, String raza, String especie, String edad, String sexo, byte[] foto, boolean favorito) {
        this.nombre = nombre;
        this.raza = raza;
        this.especie = especie;
        this.edad = edad;
        this.sexo = sexo;
        this.foto = null;
        this.favorito = favorito;
    }


    public String getNombre() { return nombre; }
    public String getRaza() { return raza; }
    public String getEspecie() { return especie; }

    public byte[] getFoto() {
        return foto;
    }

    public String getEdad() {
        return edad;
    }

    public String getSexo() {
        return sexo;
    }
    public boolean isFavorito() { return favorito; }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }
}
