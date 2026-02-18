package sistemas.unc.edu.appadopcionmascotas.Model;

public class Animal {

    private int idRefugio;
    private String nombre;
    private String especie;
    private String raza;
    private double peso;
    private String edad;
    private String sexo;

    private String temperamento;
    private String historia;
    private String estado;
    private String tamano;
    private byte[] foto;


    public Animal(int idRefugio, String nombre, String especie, String raza, double peso, String edad, String sexo, String temperamento, String historia, String estado, String tamano, byte[] foto) {
        this.idRefugio = idRefugio;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.peso = peso;
        this.edad = edad;
        this.sexo = sexo;
        this.temperamento = temperamento;
        this.historia = historia;
        this.estado = estado;
        this.tamano = tamano;
        this.foto = foto;
    }

    public Animal(){
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

    public int getIdRefugio() {
        return idRefugio;
    }

    public String getTamano() {
        return tamano;
    }
}
