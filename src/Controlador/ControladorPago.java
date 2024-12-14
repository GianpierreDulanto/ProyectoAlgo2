package Controlador;

import Modelo.*;
import Vista.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.io.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.Random;

public class ControladorPago implements ActionListener {

    private final VistaPago vista;
    private final ArregloReserva arregloReserva;
    private final ArregloPedido arregloPedido;
    private Pago[] pagos;
    private int indicePago;
    private final ControladorRecep controladorRecep;

    public ControladorPago(VistaPago vista, ArregloReserva arregloReserva, ArregloPedido arregloPedido, ControladorRecep controladorRecep) {
        this.vista = vista;
        this.arregloReserva = arregloReserva;
        this.arregloPedido = arregloPedido;
        this.controladorRecep = controladorRecep;
        this.pagos = new Pago[100];
        this.indicePago = 0;
        this.vista.getPagoAgregar().addActionListener(e -> agregarPago());
        this.vista.getPagoEditar().addActionListener(e -> editarPago());
        this.vista.getPagoEliminar().addActionListener(e -> eliminarPago());
        this.vista.getGenerarBoleta().addActionListener(e -> generarBoleta());
        this.vista.getPagoSalir().addActionListener(e -> volverAlMenuRecepcionista());
        actualizarTabla();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getPagoAgregar()) {
            agregarPago();
        } else if (e.getSource() == vista.getPagoEditar()) {
            editarPago();
        } else if (e.getSource() == vista.getPagoEliminar()) {
            eliminarPago();
        } else if (e.getSource() == vista.getPagoSalir()) {
            System.exit(0);
        }
    }

    private Pago buscarPagoPorCliente(String dniCliente) {
        for (int i = 0; i < indicePago; i++) {
            if (pagos[i] != null && pagos[i].getReserva().getCliente().getDni().equals(dniCliente)) {
                return pagos[i];
            }
        }
        return null; // Si no se encuentra ningún pago, devuelve null
    }

    private void agregarPago() {
        try {
            String dniTexto = vista.getPagoDniCliente().getText(); // Obtener el DNI del cliente
            int dniCliente = Integer.parseInt(dniTexto);

            // Calcular monto total automáticamente
            double monto = calcularMontoTotalPorDni(dniCliente);

            if (monto == 0) {
                JOptionPane.showMessageDialog(vista, "No se encontraron pedidos para el cliente con DNI: " + dniCliente);
                return;
            }

            String metodoPago = vista.getPagoMetodo().getSelectedItem().toString();
            String fechaTexto = vista.getPagoFecha().getText(); // Fecha ingresada en la vista
            SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
            Date fecha = formateador.parse(fechaTexto);

            // Asumimos que se buscará una reserva por DNI, puedes ajustar según tu lógica
            Reserva reserva = buscarReservaPorCliente(String.valueOf(dniCliente));
            if (reserva == null) {
                JOptionPane.showMessageDialog(vista, "No se encontró una reserva para el cliente con DNI: " + dniCliente);
                return;
            }

            int montoRedondeado = (int) Math.round(monto);
            Pago nuevoPago = new Pago(montoRedondeado, fecha, reserva, false);
            if (indicePago == pagos.length) {
                redimensionarPagos();
            }
            pagos[indicePago++] = nuevoPago;

            JOptionPane.showMessageDialog(vista, "Pago registrado con éxito. Monto calculado: S/" + monto);
            actualizarTabla();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "DNI inválido. Por favor, ingrese un número válido.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al registrar el pago: " + ex.getMessage());
        }
    }

    private void editarPago() {
        int filaSeleccionada = vista.getPagoTabla().getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un pago para editar.");
            return;
        }

        try {
            String dniCliente = (String) vista.getPagoTabla().getValueAt(filaSeleccionada, 0);
            String metodoPago = vista.getPagoMetodo().getSelectedItem().toString();
            Pago pago = buscarPagoPorCliente(dniCliente);

            if (pago != null) {
                // Método de pago no es atributo del modelo Pago, pero lo manejas en vista
                JOptionPane.showMessageDialog(vista, "Pago actualizado correctamente.");
                actualizarTabla(); // Actualizar la tabla con los cambios
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Monto inválido. Por favor, ingrese un número válido.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al editar el pago: " + ex.getMessage());
        }
    }

    private void eliminarPago() {
        int filaSeleccionada = vista.getPagoTabla().getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un pago para eliminar.");
            return;
        }

        String dniCliente = (String) vista.getPagoTabla().getValueAt(filaSeleccionada, 0);
        for (int i = 0; i < indicePago; i++) {
            if (pagos[i].getReserva().getCliente().getDni().equals(dniCliente)) {
                System.arraycopy(pagos, i + 1, pagos, i, indicePago - i - 1);
                pagos[--indicePago] = null;
                JOptionPane.showMessageDialog(vista, "Pago eliminado correctamente.");
                actualizarTabla(); // Actualizar la tabla con los datos restantes
                return;
            }
        }
        JOptionPane.showMessageDialog(vista, "No se encontró el pago para eliminar.");
    }

    private void actualizarTabla() {
        // Formateador para la fecha
        SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");

        // Obtener el modelo de la tabla
        DefaultTableModel modelo = (DefaultTableModel) vista.getPagoTabla().getModel();

        // Limpiar filas existentes
        modelo.setRowCount(0);

        // Verificar si la tabla tiene columnas, si no, las agregamos
        if (modelo.getColumnCount() == 0) {
            modelo.addColumn("ID Cliente");
            modelo.addColumn("Método de Pago");
            modelo.addColumn("Fecha");
            modelo.addColumn("Monto");
            modelo.addColumn("Número de habitación");
            modelo.addColumn("Fecha de ingreso");
            modelo.addColumn("Fecha de salida");

        }

        // Agregar los datos actualizados de pagos
        for (int i = 0; i < indicePago; i++) {
            Pago pago = pagos[i];
            if (pago != null) {
                modelo.addRow(new Object[]{
                    pago.getReserva().getCliente().getDni(), // ID Cliente
                    vista.getPagoMetodo().getSelectedItem(), // Método de Pago
                    formateador.format(pago.getFecha()), // Fecha formateada
                    pago.getMonto() // Monto
                });
            }
        }

        // Asignar el modelo actualizado a la tabla (opcional, si ya está asignado no es necesario)
        vista.getPagoTabla().setModel(modelo);
    }

    private Reserva buscarReservaPorCliente(String dniCliente) {
        for (Reserva reserva : arregloReserva.getReservas()) {
            if (reserva != null && reserva.getCliente().getDni().equals(dniCliente)) {
                return reserva;
            }
        }
        return null;
    }

    private Pago buscarPagoPorId(int idPago) {
        for (Pago pago : pagos) {
            if (pago != null && pago.getIdPago() == idPago) {
                return pago;
            }
        }
        return null;
    }

    private void redimensionarPagos() {
        Pago[] nuevoArreglo = new Pago[pagos.length * 2];
        System.arraycopy(pagos, 0, nuevoArreglo, 0, pagos.length);
        pagos = nuevoArreglo;
    }

    private void generarBoleta() {
        int filaSeleccionada = vista.getPagoTabla().getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un pago para generar la boleta.");
            return;
        }

        try {
            // Obtener datos de la tabla
            String dniCliente = (String) vista.getPagoTabla().getValueAt(filaSeleccionada, 0);
            String metodoPago = (String) vista.getPagoTabla().getValueAt(filaSeleccionada, 1);
            String fecha = (String) vista.getPagoTabla().getValueAt(filaSeleccionada, 2);
            String montoTotal = String.valueOf(vista.getPagoTabla().getValueAt(filaSeleccionada, 3));

            // Obtener detalles de reservas y pedidos
            StringBuilder detallesReservas = new StringBuilder();
            double totalReservas = 0.0;
            for (Reserva reserva : arregloReserva.getReservas()) {
                if (reserva != null && reserva.getCliente().getDni().equals(dniCliente)
                        && !"CANCELADA".equals(reserva.getEstado())) {
                    detallesReservas.append(String.format("Habitación %d: S/.%.2f\n",
                            reserva.getHabitacion().getId_habitacion(),
                            reserva.getMontoTotal()));
                    totalReservas += reserva.getMontoTotal();
                }
            }

            StringBuilder detallesPedidos = new StringBuilder();
            double totalPedidos = 0.0;
            for (Pedido pedido : arregloPedido.getArregloPedidos()) {
                if (pedido != null && String.valueOf(pedido.getDni()).equals(dniCliente)) {
                    detallesPedidos.append(String.format("Pedido #%d: S/.%.2f\n",
                            new Random().nextInt(900000) + 100000,
                            pedido.getMontoTotal()));
                    totalPedidos += pedido.getMontoTotal();
                }
            }

            // Formato del archivo TXT
            String contenidoBoleta
                    = "****************************************\n"
                    + "            ☆☆HOTEL OASIS☆☆             \n"
                    + "             Boleta de Pago               \n"
                    + "****************************************\n"
                    + "\n"
                    + "Cliente:\n"
                    + "----------------------------------------\n"
                    + "DNI Cliente  : " + dniCliente + "\n"
                    + "\n"
                    + "Detalles de la reserva:\n"
                    + "----------------------------------------\n"
                    + detallesReservas.toString()
                    + "Subtotal Reservas: S/." + String.format("%.2f", totalReservas) + "\n"
                    + "\n"
                    + "Detalles de consumos:\n"
                    + "----------------------------------------\n"
                    + detallesPedidos.toString()
                    + "Subtotal Consumos: S/." + String.format("%.2f", totalPedidos) + "\n"
                    + "\n"
                    + "Resumen del pago:\n"
                    + "----------------------------------------\n"
                    + "Método Pago  : " + metodoPago + "\n"
                    + "Fecha Pago   : " + fecha + "\n"
                    + "MONTO TOTAL  : S/." + montoTotal + " soles\n"
                    + "\n"
                    + "****************************************\n"
                    + "       ¡GRACIAS POR SU PREFERENCIA!     \n"
                    + "****************************************\n";

            // Definir la carpeta de destino
            String carpetaBase = System.getProperty("user.dir");
            String carpetaDestino = carpetaBase + File.separator + "Boletas";
            Files.createDirectories(Paths.get(carpetaDestino));

            // Generar la ruta del archivo dinámicamente
            String baseNombreArchivo = "boleta_" + dniCliente;
            String extension = ".txt";
            String rutaArchivo = carpetaDestino + File.separator + baseNombreArchivo + extension;

            // Verificar si ya existe un archivo con el mismo nombre y agregar un número al final si es necesario
            int contador = 1;
            while (Files.exists(Paths.get(rutaArchivo))) {
                rutaArchivo = carpetaDestino + File.separator + baseNombreArchivo + "_" + contador + extension;
                contador++;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo))) {
                writer.write(contenidoBoleta);
            }

            JOptionPane.showMessageDialog(vista, "Boleta generada con éxito: " + rutaArchivo);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(vista, "Error al generar la boleta: " + ex.getMessage());
        }

    }

    private void volverAlMenuRecepcionista() {
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

    private double calcularMontoTotalPorDni(int dniCliente) {
        double montoTotal = 0.0;
        for (Pedido pedido : arregloPedido.getArregloPedidos()) {
            if (pedido != null && pedido.getDni() == dniCliente) {
                montoTotal += pedido.getMontoTotal();
            }
        }
        for (Reserva reserva : arregloReserva.getReservas()) {
            if (reserva != null && reserva.getCliente().getDni().equals(String.valueOf(dniCliente))
                    && !"CANCELADA".equals(reserva.getEstado())) {
                montoTotal += reserva.getMontoTotal();
            }
        }
        return montoTotal;
    }

}
