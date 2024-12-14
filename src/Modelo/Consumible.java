package Modelo;

public abstract class Consumible {

    protected String nombreConsumible;
    protected double precio;
    protected String tipoConsumible;

    public Consumible(String tipoConsumible, String nombreConsumible, double precio) {
        this.tipoConsumible = tipoConsumible;
        this.nombreConsumible = nombreConsumible;
        this.precio = precio;
    }

    public String getNombreConsumible() {
        return nombreConsumible;
    }

    public double getPrecio() {
        return precio;
    }
}
