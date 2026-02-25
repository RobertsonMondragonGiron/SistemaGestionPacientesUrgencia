package modelo;

public class Paciente {
    private String id;
    private String nombre;
    private String categoriaTriage;
    private int edad;
    private int prioridad;
    private int tiempoEspera;
    private double riesgo;
    private double umbralAlerta;

    public Paciente(String id, String nombre, String categoriaTriage, int edad,
                    int prioridad, int tiempoEspera, double riesgo, double umbralAlerta) {
        this.id = id;
        this.nombre = nombre;
        this.categoriaTriage = categoriaTriage;
        this.edad = edad;
        this.prioridad = prioridad;
        this.tiempoEspera = tiempoEspera;
        this.riesgo = riesgo;
        this.umbralAlerta = umbralAlerta;
    }

    public boolean enAlerta() {
        return riesgo >= umbralAlerta;
    }

    public String estadoAtencion() {
        return enAlerta() ? "CRÍTICO" : "ESTABLE";
    }

    // Getters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCategoriaTriage() { return categoriaTriage; }
    public int getEdad() { return edad; }
    public int getPrioridad() { return prioridad; }
    public int getTiempoEspera() { return tiempoEspera; }
    public double getRiesgo() { return riesgo; }
    public double getUmbralAlerta() { return umbralAlerta; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCategoriaTriage(String categoriaTriage) { this.categoriaTriage = categoriaTriage; }
    public void setEdad(int edad) { this.edad = edad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }
    public void setTiempoEspera(int tiempoEspera) { this.tiempoEspera = tiempoEspera; }
    public void setRiesgo(double riesgo) { this.riesgo = riesgo; }
    public void setUmbralAlerta(double umbralAlerta) { this.umbralAlerta = umbralAlerta; }

    @Override
    public String toString() {
        return id + " - " + nombre;
    }
}
