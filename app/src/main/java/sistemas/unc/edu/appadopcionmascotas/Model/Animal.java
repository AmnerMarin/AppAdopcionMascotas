    package sistemas.unc.edu.appadopcionmascotas.Model;

    public class Animal {

        private int IdMascota;
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
        private byte[] foto; //sqlite

        private String fotoUrl; //Firebase

        private String DireccionRefugio;
        private String NombreRefugio;
        private String TelefonoRefugio;

        private boolean Favorito;

        private String FirebaseUID;


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
            this.Favorito = false;
        }

        public Animal(){
            this.Favorito = false;
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

        // FAVORITO
        public boolean isFavorito() { return Favorito; }
        public void setFavorito(boolean favorito) { this.Favorito = favorito; }

        public int getIdMascota() {
            return IdMascota;
        }

        public void setIdMascota(int idMascota) {
            IdMascota = idMascota;
        }

        public void setIdRefugio(int idRefugio) {
            this.idRefugio = idRefugio;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public void setEspecie(String especie) {
            this.especie = especie;
        }

        public void setRaza(String raza) {
            this.raza = raza;
        }

        public void setPeso(double peso) {
            this.peso = peso;
        }

        public void setEdad(String edad) {
            this.edad = edad;
        }

        public void setSexo(String sexo) {
            this.sexo = sexo;
        }

        public void setTemperamento(String temperamento) {
            this.temperamento = temperamento;
        }

        public void setHistoria(String historia) {
            this.historia = historia;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }

        public void setTamano(String tamano) {
            this.tamano = tamano;
        }

        public void setFoto(byte[] foto) {
            this.foto = foto;
        }

        public String getTelefonoRefugio() {
            return TelefonoRefugio;
        }

        public void setTelefonoRefugio(String telefonoRefugio) {
            TelefonoRefugio = telefonoRefugio;
        }

        public String getNombreRefugio() {
            return NombreRefugio;
        }

        public void setNombreRefugio(String nombreRefugio) {
            NombreRefugio = nombreRefugio;
        }

        public String getDireccionRefugio() {
            return DireccionRefugio;
        }

        public void setDireccionRefugio(String direccionRefugio) {
            DireccionRefugio = direccionRefugio;
        }

        public String getFirebaseUID() {
            return FirebaseUID;
        }

        public void setFirebaseUID(String firebaseUID) {
            FirebaseUID = firebaseUID;
        }

        public String getFotoUrl() { return fotoUrl; }
        public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    }
