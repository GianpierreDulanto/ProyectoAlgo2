package Modelo;

import java.util.Date;

public class Pedido {

    private int dni;
    private int habitacion;
    private Date fechaPedido;
    private double montoTotal;
    private Consumible[] consumibles;
    private int indiceConsumibles;

    public Pedido(int id, int habitacion, Date fechaPedido) {
        this.dni = id;
        this.habitacion = habitacion;
        this.fechaPedido = fechaPedido;
        this.montoTotal = calcularTotal();
        this.consumibles = new Consumible[10];
        this.indiceConsumibles = 0;
    }

    public Pedido(int id, int habitacion, Date fechaPedido, double montoTotal) {
        this.dni = id;
        this.habitacion = habitacion;
        this.fechaPedido = fechaPedido;
        this.montoTotal = montoTotal;
        this.consumibles = new Consumible[10];
        this.indiceConsumibles = 0;
    }

    public int getId() {
        return dni;
    }

    public Date getFechaPedido() {
        return fechaPedido;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void agregarConsumible(Consumible consumible) {
        if (indiceConsumibles == consumibles.length) {
            redimensionarConsumibles();
        }
        if (consumible != null) {
            consumibles[indiceConsumibles] = consumible;
            indiceConsumibles++;
            calcularTotal();
        }
    }

    public void seleccionarBebida(Bebida bebida) {
        agregarConsumible(bebida);
    }

    public void seleccionarComida(Comida comida) {
        agregarConsumible(comida);
    }

    public double calcularTotal() {
        montoTotal = 0.0;
        for (int i = 0; i < indiceConsumibles; i++) {
            montoTotal += consumibles[i].getPrecio();
        }
        return montoTotal;
    }

    private void redimensionarConsumibles() {
        Consumible[] nuevoArreglo = new Consumible[consumibles.length * 2];
        System.arraycopy(consumibles, 0, nuevoArreglo, 0, consumibles.length);
        consumibles = nuevoArreglo;
    }

    public Consumible buscarConsumiblePorNombre(String nombreProducto) {
        for (int i = 0; i < indiceConsumibles; i++) {
            if (consumibles[i].getNombreConsumible().equalsIgnoreCase(nombreProducto)) {
                return consumibles[i];
            }
        }
        return null;
    }

    public void mostrarConsumibles() {
        System.out.println("Pedido ID: " + dni + ", Fecha: " + fechaPedido + ", Monto Total: " + montoTotal);
        for (int i = 0; i < indiceConsumibles; i++) {
            System.out.println("- " + consumibles[i].getNombreConsumible() + ": " + consumibles[i].getPrecio());
        }
    }

    public void limpiarConsumibles() {
        for (int i = 0; i < consumibles.length; i++) {
            consumibles[i] = null;
        }
        indiceConsumibles = 0;
        montoTotal = 0.0;
    }

    public Consumible[] getConsumibles() {
        return consumibles;
    }

    public int getDni() {
        return dni;
    }

    public int getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(int habitacion) {
        this.habitacion = habitacion;
    }

}
