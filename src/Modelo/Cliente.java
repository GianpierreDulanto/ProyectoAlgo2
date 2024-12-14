package Modelo;

import java.util.ArrayList;
import java.util.List;

public class Cliente {

    private static int contadorGlobal = 0;
    private int id;
    private String dni;  // Única información personal
    private Habitacion habitacionAsignada;
    private Pedido pedidoActual;
    private List<Reserva> reservas;

    // Constructor con DNI
    public Cliente(String dni) {
        this.id = ++contadorGlobal;
        this.dni = dni;
        this.pedidoActual = null;
        this.reservas = new ArrayList<>();
    }

    // Métodos para gestión de reservas
    public void agregarReserva(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("La reserva no puede ser nula");
        }
        this.reservas.add(reserva);
    }

    public List<Reserva> getReservas() {
        return new ArrayList<>(reservas);
    }

    public boolean tieneReservaActiva() {
        if (reservas == null || reservas.isEmpty()) {
            return false;
        }
        return reservas.stream()
                .anyMatch(r -> !r.getFinReserva().isBefore(java.time.LocalDate.now()));
    }

    // Métodos para gestión de habitación
    public void asignarHabitacion(Habitacion habitacion) {
        if (habitacion == null) {
            throw new IllegalArgumentException("La habitación no puede ser nula");
        }
        this.habitacionAsignada = habitacion;
    }

    public void liberarHabitacion() {
        this.habitacionAsignada = null;
    }

    // Métodos para gestión de pedidos
    public void hacerPedido() {
        if (habitacionAsignada == null) {
            throw new IllegalStateException("El cliente no tiene habitación asignada");
        }
        if (pedidoActual == null) {
            pedidoActual = new Pedido(id, habitacionAsignada.getId_habitacion(), new java.util.Date());
            System.out.println("Pedido creado para la habitación " + habitacionAsignada.getId_habitacion());
        } else {
            throw new IllegalStateException("Ya existe un pedido en curso para la habitación " + habitacionAsignada.getId_habitacion());
        }
    }

    public void agregarConsumibleAPedido(Consumible consumible) {
        if (pedidoActual == null) {
            throw new IllegalStateException("No hay un pedido activo. Debe crear un pedido primero.");
        }
        if (consumible == null) {
            throw new IllegalArgumentException("El consumible no puede ser nulo");
        }
        pedidoActual.agregarConsumible(consumible);
        System.out.println("Consumible agregado al pedido de la habitación " + habitacionAsignada.getId_habitacion());
    }

    public void finalizarPedido() {
        if (pedidoActual == null) {
            throw new IllegalStateException("No hay un pedido activo para finalizar");
        }
        pedidoActual.calcularTotal();
        pedidoActual = null;
        System.out.println("Pedido finalizado y cargado a la habitación " + habitacionAsignada.getId_habitacion());
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede ser nulo o vacío");
        }
        this.dni = dni.trim();
    }

    public Habitacion getHabitacionAsignada() {
        return habitacionAsignada;
    }

    public Pedido getPedidoActual() {
        return pedidoActual;
    }

    @Override
    public String toString() {
        return String.format("%d,%s",
                id,
                dni);
    }
}
