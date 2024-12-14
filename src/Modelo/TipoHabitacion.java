package Modelo;

public class TipoHabitacion {

    private String tipo;
    private double precio;
    private String descripcion;
    private int capacidad;

    public TipoHabitacion(String tipo, double precio) {
        this.tipo = tipo;
        this.precio = precio;
        this.capacidad = 2;
    }

    public TipoHabitacion(String tipo, double precio, String descripcion, int capacidad) {
        this.tipo = tipo;
        this.precio = precio;
        this.descripcion = descripcion;
        this.capacidad = capacidad;
    }

    public double obtenerPrecio(String tipo) {
        return this.tipo.equals(tipo) ? this.precio : -1;
    }

    public void cambiarPrecio(String tipo, double nuevoPrecio) {
        if (this.tipo.equals(tipo) && nuevoPrecio > 0) {
            this.precio = nuevoPrecio;
        }
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    @Override
    public String toString() {
        return tipo + "," + precio + "," + descripcion + "," + capacidad;
    }
}
