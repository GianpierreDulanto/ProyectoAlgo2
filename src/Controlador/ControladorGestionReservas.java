package Controlador;

import Modelo.*;
import Vista.*;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ControladorGestionReservas {

    private GestionReservas vista;
    private ArregloReserva modelo;
    private HabitacionArreglo habitaciones;
    private ClienteArreglo clientes;
    private ControladorAdm controladorAdm;

    public ControladorGestionReservas(GestionReservas vista, ArregloReserva modelo,
            HabitacionArreglo habitaciones, ClienteArreglo clientes, ControladorAdm controladorAdm) {
        this.vista = vista;
        this.modelo = modelo;
        this.habitaciones = habitaciones;
        this.clientes = clientes;
        this.controladorAdm = controladorAdm;

        this.vista.getBotonHacerReservacion().addActionListener(e -> hacerReservacion());
        this.vista.getBotonCancelarRserva().addActionListener(e -> cancelarReserva());
        this.vista.getBotonActualizarReserva().addActionListener(e -> actualizarReserva());
        this.vista.getBotonBuscarReserva().addActionListener(e -> buscarReserva());
        this.vista.getBotonSalir().addActionListener(e -> volver());
        cargarTabla();
        agregarListenerTabla();
    }

    private void cargarTabla() {
        Reserva[] reservasArray = modelo.getReservas();

        if (modelo.getIndice() == 0) {
            JOptionPane.showMessageDialog(vista, "No hay reservas para mostrar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[][] data = new String[modelo.getIndice()][6];

        for (int i = 0; i < modelo.getIndice(); i++) {
            Reserva reserva = reservasArray[i];
            if (reserva != null) {
                data[i][0] = String.valueOf(reserva.getIdReserva());
                data[i][1] = String.valueOf(reserva.getHabitacion().getId_habitacion());
                data[i][2] = reserva.getCliente().getDni();
                data[i][3] = reserva.getInicioReserva().toString();
                data[i][4] = reserva.getFinReserva().toString();
                data[i][5] = reserva.getEstado();
            }
        }
        vista.getTablaReservas().setModel(new DefaultTableModel(
                data,
                new String[]{"ID Reserva", "ID Habitación", "DNI Cliente", "Inicio", "Fin", "Estado"}
        ));
    }

    private void volver() {
        try {
            vista.dispose(); // Cerrar la ventana actual de pagos

            // Si tenemos una referencia al controlador anterior, usamos su método
            if (controladorAdm != null) {
                controladorAdm.mostrarMenuAdm();
            } else {
                // Si no tenemos la referencia, creamos una nueva instancia del menú
                MenuAdm nuevaVista = new MenuAdm();
                nuevaVista.setLocationRelativeTo(null);
                nuevaVista.setVisible(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,
                    "Error al volver al menú principal: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarListenerTabla() {
        DefaultTableModel tableModel = (DefaultTableModel) vista.getTablaReservas().getModel();
        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();

            if (e.getType() == TableModelEvent.UPDATE && column != -1) {
                try {
                    String valor = (String) tableModel.getValueAt(row, column);
                    Reserva reserva = modelo.getReservas()[row];

                    if (reserva == null) {
                        throw new IllegalArgumentException("La reserva no existe.");
                    }

                    switch (column) {
                        case 0 ->
                            reserva.setIdReserva(Integer.parseInt(valor));
                        case 1 ->
                            reserva.getHabitacion().setId_habitacion(Integer.parseInt(valor));
                        case 3 ->
                            reserva.setInicioReserva(parseFecha(valor));
                        case 4 ->
                            reserva.setFinReserva(parseFecha(valor));
                        case 5 ->
                            reserva.setEstado(valor);
                        default ->
                            throw new IllegalArgumentException("Columna no válida.");
                    }
                    modelo.actualizarReserva(reserva);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(vista, "Error al actualizar la tabla: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void hacerReservacion() {
        try {
            String dni = vista.getDniCliente().getText().trim();
            Cliente cliente = new Cliente(dni);
            if (habitaciones.getIndice() == 0) {
                throw new IllegalStateException("No hay habitaciones disponibles.");
            }

            int idHabitacion = Integer.parseInt(vista.getNumHabitacion().getText().trim());
            Habitacion habitacion = habitaciones.buscarHabitacion(idHabitacion);
            validarHabitacion(habitacion);

            LocalDate inicio = parseFecha(vista.getInicioReserva().getText().trim());
            LocalDate fin = parseFecha(vista.getFinReserva().getText().trim());
            validarFechas(inicio, fin);

            int nuevoIdReserva = modelo.generarNuevoIdReserva();

            Reserva nuevaReserva = new Reserva(
                    nuevoIdReserva,
                    habitacion,
                    cliente,
                    inicio,
                    fin
            );
            modelo.agregar(nuevaReserva);

            JOptionPane.showMessageDialog(vista, "Reservación realizada con éxito. ID: " + nuevoIdReserva, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al hacer la reservación: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarReserva() {
        try {
            int idReserva = Integer.parseInt(vista.getIdReserva().getText().trim());
            Reserva reserva = modelo.buscarReserva(idReserva);

            if (reserva == null) {
                throw new IllegalArgumentException("Reserva no encontrada.");
            }

            reserva.cancelarReserva();
            modelo.actualizarReserva(reserva);
            JOptionPane.showMessageDialog(vista, "Reservación cancelada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al cancelar la reservación: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarReserva() {
        try {
            int idReserva = Integer.parseInt(vista.getIdReserva().getText().trim());
            Reserva reserva = modelo.buscarReserva(idReserva);

            if (reserva == null) {
                throw new IllegalArgumentException("Reserva no encontrada.");
            }

            LocalDate nuevaFechaInicio = parseFecha(vista.getInicioReserva().getText().trim());
            LocalDate nuevaFechaFin = parseFecha(vista.getFinReserva().getText().trim());
            validarFechas(nuevaFechaInicio, nuevaFechaFin);

            reserva.setInicioReserva(nuevaFechaInicio);
            reserva.setFinReserva(nuevaFechaFin);
            modelo.actualizarReserva(reserva);
            JOptionPane.showMessageDialog(vista, "Reservación actualizada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al actualizar la reservación: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarReserva() {
        try {
            int idReserva = Integer.parseInt(vista.getIdReserva().getText().trim());
            Reserva reserva = modelo.buscarReserva(idReserva);
            if (reserva != null) {
                mostrarReserva(reserva);
            } else {
                JOptionPane.showMessageDialog(vista, "Reserva no encontrada.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al buscar la reservación: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private LocalDate parseFecha(String fecha) {
        return LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private void validarFechas(LocalDate inicio, LocalDate fin) {
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la de fin.");
        }
    }

    private void validarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente no encontrado.");
        }
    }

    private void validarHabitacion(Habitacion habitacion) {
        if (habitacion == null || !habitacion.isEstado()) {
            throw new IllegalArgumentException("Habitación no disponible.");
        }
    }

    private void validarReserva(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("Reserva no encontrada.");
        }
    }

    private void mostrarReserva(Reserva reserva) {
        vista.getNumHabitacion().setText(String.valueOf(reserva.getHabitacion().getId_habitacion()));
        vista.getInicioReserva().setText(reserva.getInicioReserva().toString());
        vista.getFinReserva().setText(reserva.getFinReserva().toString());
        vista.getComboBoxdeEstado().setSelectedItem(reserva.getEstado());
    }
}
