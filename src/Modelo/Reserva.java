package Modelo;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Reserva {

    private int idReserva;
    private Habitacion habitacion;
    private Cliente cliente;
    private LocalDate inicioReserva;
    private LocalDate finReserva;
    private String estado;
    private Servicio[] servicios;
    private int indiceServicios;
    private double montoTotal;
    private static final int MAX_SERVICIOS = 10;

    public Reserva(int idReserva, Habitacion habitacion, Cliente cliente, LocalDate inicioReserva, LocalDate finReserva) {
        if (inicioReserva.isAfter(finReserva)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        this.idReserva = idReserva;
        this.habitacion = habitacion;
        this.cliente = cliente;
        this.inicioReserva = inicioReserva;
        this.finReserva = finReserva;
        this.estado = "PENDIENTE";
        this.servicios = new Servicio[MAX_SERVICIOS];
        this.indiceServicios = 0;
        calcularMontoTotal();
    }

    public void confirmarReserva() {
        if (!"PENDIENTE".equals(this.estado)) {
            throw new IllegalStateException("La reserva no está en estado pendiente");
        }
        this.estado = "CONFIRMADA";
        this.habitacion.setEstado(false); // Habitación no disponible
    }

    public void cancelarReserva() {
        if ("CANCELADA".equals(this.estado)) {
            throw new IllegalStateException("La reserva ya está cancelada");
        }
        this.estado = "CANCELADA";
        this.habitacion.setEstado(true); // Habitación disponible
    }

    public void agregarServicio(Servicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio no puede ser nulo");
        }
        if (indiceServicios >= MAX_SERVICIOS) {
            throw new IllegalStateException("Se ha alcanzado el máximo de servicios permitidos");
        }
        servicios[indiceServicios++] = servicio;
        calcularMontoTotal();
    }

    void calcularMontoTotal() {
        double subtotalHabitacion = habitacion.getTipo().getPrecio();
        double subtotalServicios = 0;
        for (int i = 0; i < indiceServicios; i++) {
            if (servicios[i] != null) {
                subtotalServicios += servicios[i].getPrecio();
            }
        }
        this.montoTotal = subtotalHabitacion + subtotalServicios;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDate getInicioReserva() {
        return inicioReserva;
    }

    public void setInicioReserva(LocalDate inicioReserva) {
        this.inicioReserva = inicioReserva;
    }

    public LocalDate getFinReserva() {
        return finReserva;
    }

    public void setFinReserva(LocalDate finReserva) {
        this.finReserva = finReserva;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public Servicio[] getServicios() {
        return servicios;
    }

    @Override
    public String toString() {
        return String.format("%d,%d,%s,%s,%s,%s,%.2f",
                idReserva, habitacion.getId_habitacion(), cliente.getDni(),
                inicioReserva, finReserva, estado, montoTotal);
    }
}
