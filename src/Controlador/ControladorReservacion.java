package Controlador;

import Modelo.*;
import Vista.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class ControladorReservacion {

    private VistaReservacion vista;
    private ArregloReserva modeloReservas;
    private HabitacionArreglo habitaciones;
    private ClienteArreglo clientes;
    private final ControladorRecep controladorRecep;
    private static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public ControladorReservacion(VistaReservacion vista, ArregloReserva modelo, HabitacionArreglo habitaciones, ClienteArreglo clientes, ControladorRecep controladorRecep) {
        this.vista = vista;
        this.modeloReservas = modelo;
        this.habitaciones = habitaciones;
        this.clientes = clientes;
        this.controladorRecep = controladorRecep;
        inicializarEventos();
        cargarReservasEnTabla();
    }

    private void inicializarEventos() {
        vista.getReservacionAgregar().addActionListener(e -> agregarReserva());
        vista.getReservacionSalir().addActionListener(e -> volver());
    }

    private void volver() {
        try {
            vista.dispose(); // Cerrar la ventana actual de pagos

            // Si tenemos una referencia al controlador anterior, usamos su método
            if (controladorRecep != null) {
                controladorRecep.mostrarMenuRecepcionista();
            } else {
                // Si no tenemos la referencia, creamos una nueva instancia del menú
                MenuRecep nuevaVista = new MenuRecep();
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

    private void cargarReservasEnTabla() {
        Reserva[] reservasArray = modeloReservas.getReservas();
        DefaultTableModel model = (DefaultTableModel) vista.getReservacionTabla().getModel();
        model.setRowCount(0);

        for (int i = 0; i < modeloReservas.getIndice(); i++) {
            Reserva reserva = modeloReservas.getReservas()[i];
            if (reserva != null) {
                model.addRow(new Object[]{
                    reserva.getCliente().getDni(),
                    reserva.getHabitacion().getId_habitacion(),
                    reserva.getInicioReserva().format(FECHA_FORMATTER),
                    reserva.getFinReserva().format(FECHA_FORMATTER),
                    reserva.getMontoTotal()
                });
            }
        }
    }

    private boolean validarDatosReserva() {
        if (vista.getReservacionDNI().getText().trim().isEmpty()
                || vista.getReservacionHabitacion().getText().trim().isEmpty()
                || vista.getReservacionFechaInicio().getText().trim().isEmpty()
                || vista.getReservacionFechaFin().getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(vista,
                    "Todos los campos son obligatorios",
                    "Error de validación",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void limpiarCampos() {
        vista.getReservacionDNI().setText("");
        vista.getReservacionHabitacion().setText("");
        vista.getReservacionFechaInicio().setText("");
        vista.getReservacionFechaFin().setText("");
    }

    private void agregarReserva() {
        try {
            if (!validarDatosReserva()) {
                return;
            }

            int idReserva = generarNuevoIdReserva();
            int idHabitacion = Integer.parseInt(vista.getReservacionHabitacion().getText().trim());
            LocalDate inicio = LocalDate.parse(vista.getReservacionFechaInicio().getText().trim(), FECHA_FORMATTER);
            LocalDate fin = LocalDate.parse(vista.getReservacionFechaFin().getText().trim(), FECHA_FORMATTER);
            String dniCliente = vista.getReservacionDNI().getText().trim();

            if (!validarFechas(inicio, fin)) {
                return;
            }

            if (!validarDisponibilidadHabitacion(idHabitacion, inicio, fin)) {
                JOptionPane.showMessageDialog(vista,
                        "La habitación no está disponible para las fechas seleccionadas",
                        "Error de disponibilidad",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Habitacion habitacion = habitaciones.buscarHabitacion(idHabitacion);
            Cliente cliente = new Cliente(dniCliente);
            Reserva nuevaReserva = new Reserva(idReserva, habitacion, cliente, inicio, fin);

            modeloReservas.agregar(nuevaReserva);

            JOptionPane.showMessageDialog(vista, "Reserva agregada exitosamente.");
            cargarReservasEnTabla();
            limpiarCampos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista,
                    "El ID de habitación debe ser un número válido",
                    "Error de formato",
                    JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(vista,
                    "Las fechas deben estar en formato YYYY-MM-DD",
                    "Error de formato",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,
                    "Error al agregar la reserva: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarReserva() {
        try {
            if (!validarDatosReserva()) {
                return;
            }

            int idReserva = Integer.parseInt(vista.getReservacionDNI().getText().trim());
            Reserva reservaExistente = modeloReservas.buscarReserva(idReserva);

            if (reservaExistente == null) {
                JOptionPane.showMessageDialog(vista,
                        "Reserva no encontrada",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate inicio = LocalDate.parse(vista.getReservacionFechaInicio().getText().trim(), FECHA_FORMATTER);
            LocalDate fin = LocalDate.parse(vista.getReservacionFechaFin().getText().trim(), FECHA_FORMATTER);

            if (!validarFechas(inicio, fin)) {
                return;
            }

            reservaExistente.setInicioReserva(inicio);
            reservaExistente.setFinReserva(fin);
            modeloReservas.actualizarReserva(reservaExistente);

            JOptionPane.showMessageDialog(vista, "Reserva actualizada exitosamente.");
            cargarReservasEnTabla();
            limpiarCampos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,
                    "Error al editar la reserva: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarReserva() {
        try {
            String idText = vista.getReservacionDNI().getText().trim();
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(vista,
                        "Por favor, ingrese el ID de la reserva a eliminar",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int idReserva = Integer.parseInt(idText);
            int confirmacion = JOptionPane.showConfirmDialog(vista,
                    "¿Está seguro de eliminar la reserva?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                modeloReservas.eliminar(idReserva);
                JOptionPane.showMessageDialog(vista, "Reserva eliminada exitosamente.");
                cargarReservasEnTabla();
                limpiarCampos();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista,
                    "El ID de reserva debe ser un número válido",
                    "Error de formato",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista,
                    "Error al eliminar la reserva: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarReserva() {
        try {
            int idReserva = Integer.parseInt(vista.getReservacionDNI().getText().trim());
            Reserva reserva = modeloReservas.buscarReserva(idReserva);

            if (reserva != null) {
                vista.getReservacionDNI().setText(reserva.getCliente().getDni());
                vista.getReservacionHabitacion().setText(String.valueOf(reserva.getHabitacion().getId_habitacion()));
                vista.getReservacionFechaInicio().setText(reserva.getInicioReserva().toString());
                vista.getReservacionFechaFin().setText(reserva.getFinReserva().toString());
            } else {
                JOptionPane.showMessageDialog(vista, "Reserva no encontrada.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al buscar la reserva: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarFechas(LocalDate inicio, LocalDate fin) {
        if (inicio.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(vista, "La fecha de inicio no puede ser anterior a la fecha actual.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (fin.isBefore(inicio)) {
            JOptionPane.showMessageDialog(vista, "La fecha de fin no puede ser anterior a la de inicio.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean validarDisponibilidadHabitacion(int idHabitacion, LocalDate inicio, LocalDate fin) {
        Habitacion habitacion = habitaciones.buscarHabitacion(idHabitacion);
        if (habitacion == null) {
            JOptionPane.showMessageDialog(vista, "Habitación no encontrada.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        for (Reserva reserva : modeloReservas.getReservas()) {
            if (reserva != null && reserva.getHabitacion().getId_habitacion() == idHabitacion) {
                if ((inicio.isBefore(reserva.getFinReserva()) && fin.isAfter(reserva.getInicioReserva()))
                        || inicio.equals(reserva.getInicioReserva()) || fin.equals(reserva.getFinReserva())) {
                    return false;
                }
            }
        }
        return true;
    }

    private int generarNuevoIdReserva() {
        return modeloReservas.getIndice() + 1;
    }
}
