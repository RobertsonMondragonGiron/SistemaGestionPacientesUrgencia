package modelo;

public class ResumenTriage {
    private String categoria;
    private int cantidad;
    private double riesgoPromedio;

    public ResumenTriage(String categoria, int cantidad, double riesgoPromedio) {
        this.categoria = categoria;
        this.cantidad = cantidad;
        this.riesgoPromedio = riesgoPromedio;
    }

    public String getCategoria() { return categoria; }
    public int getCantidad() { return cantidad; }
    public double getRiesgoPromedio() { return riesgoPromedio; }

    @Override
    public String toString() {
        return String.format("%-10s | Pacientes: %d | Riesgo Prom: %.2f", categoria, cantidad, riesgoPromedio);
    }
}
