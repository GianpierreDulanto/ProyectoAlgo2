package Modelo;

/**
 *
 * @author estef
 */
public class Habitacion {

    private int id_habitacion;
    private TipoHabitacion tipo;
    private boolean estado;
    private String observaciones;

    public Habitacion(int id_habitacion, TipoHabitacion tipo, boolean estado) {
        this.id_habitacion = id_habitacion;
        this.tipo = tipo;
        this.estado = estado;
    }

    public void cambiarEstado() {
        this.estado = !this.estado;
    }

    // Getters y Setters
    public int getId_habitacion() {
        return id_habitacion;
    }

    public void setId_habitacion(int id_habitacion) {
        this.id_habitacion = id_habitacion;
    }

    public TipoHabitacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoHabitacion tipo) {
        this.tipo = tipo;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return id_habitacion + "," + tipo.getTipo() + "," + tipo.getPrecio() + "," + estado + "," + observaciones;
    }
}
