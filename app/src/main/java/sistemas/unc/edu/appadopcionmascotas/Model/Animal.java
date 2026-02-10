package sistemas.unc.edu.appadopcionmascotas.Model;

public class Animal {
    public String nombre;
    public String raza;
    public String especie;
    public int foto; //Referencia a R.drawable.perro_prueba

    public Animal(String nombre, String raza, String especie, int foto) {
        this.nombre = nombre;
        this.raza = raza;
        this.especie = especie;
        this.foto = foto;
    }

    public String getNombre() { return nombre; }
    public String getRaza() { return raza; }
    public String getEspecie() { return especie; }
    public int getImagenRes() { return foto; }
}
