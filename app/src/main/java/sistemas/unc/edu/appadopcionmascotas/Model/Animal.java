package sistemas.unc.edu.appadopcionmascotas.Model;

public class Animal {
    private String nombre;
    private String especie;
    private String raza;
    private double peso;
    private String edad;
    private String sexo;

    private String temperamento;
    private String historia;
    private String estado;
    private byte[] foto;

    public Animal(byte[] foto, String estado, String historia, String temperamento, String edad, double peso, String raza, String especie, String nombre) {
        this.foto = foto;
        this.estado = estado;
        this.historia = historia;
        this.temperamento = temperamento;
        this.edad = edad;
        this.peso = peso;
        this.raza = raza;
        this.especie = especie;
        this.nombre = nombre;
    }

    public byte[] getFoto() {
        return foto;
    }

    public String getEstado() {
        return estado;
    }

    public String getHistoria() {
        return historia;
    }

    public String getTemperamento() {
        return temperamento;
    }

    public String getEdad() {
        return edad;
    }

    public double getPeso() {
        return peso;
    }

    public String getRaza() {
        return raza;
    }

    public String getEspecie() {
        return especie;
    }

    public String getNombre() {
        return nombre;
    }
    public String getSexo() {
        return sexo;
    }
}
