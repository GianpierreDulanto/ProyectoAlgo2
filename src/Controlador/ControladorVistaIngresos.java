package Controlador;

import Modelo.*;
import Vista.*;
import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ControladorVistaIngresos {

    private VistaIngresos vista;
    private ControladorAdm controladorAdm;
    private ArregloPedido pedidos;
    private ArregloReserva reservas;
    private List<DatoBoleta> datosBoletas;

    // Clase interna actualizada para manejar los datos de las boletas
    private static class DatoBoleta {

        String dniCliente;
        String metodoPago;
        Date fecha;
        double montoReservas;
        double montoConsumos;

        public DatoBoleta(String dniCliente, String metodoPago, Date fecha,
                double montoReservas, double montoConsumos) {
            this.dniCliente = dniCliente;
            this.metodoPago = metodoPago;
            this.fecha = fecha;
            this.montoReservas = montoReservas;
            this.montoConsumos = montoConsumos;
        }
    }

    public ControladorVistaIngresos(VistaIngresos vista, ControladorAdm controladorAdm,
            ArregloPedido pedidos, ArregloReserva reservas) {
        this.vista = vista;
        this.controladorAdm = controladorAdm;
        this.pedidos = pedidos;
        this.reservas = reservas;
        this.datosBoletas = new ArrayList<>();

        inicializarComboBoxes();
        cargarDatosBoletas();
        configurarEventos();
    }

    private void inicializarComboBoxes() {
        // Inicializar años (desde 2020 hasta el año actual)
        int yearActual = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = 2020; year <= yearActual; year++) {
            vista.getjComboBoxAnio().addItem(String.valueOf(year));
        }

        // Inicializar meses
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        for (String mes : meses) {
            vista.jComboBoxMes().addItem(mes);
        }

        // Inicializar métodos de pago
        vista.getjComboBoxMetodoPago().addItem("Todos");
        vista.getjComboBoxMetodoPago().addItem("Efectivo");
        vista.getjComboBoxMetodoPago().addItem("Tarjeta");
    }

    private void configurarEventos() {
        vista.getVolver().addActionListener(e -> volver());
        vista.getjComboBoxAnio().addActionListener(e -> actualizarEstadisticas());
        vista.jComboBoxMes().addActionListener(e -> actualizarEstadisticas());
        vista.getjComboBoxMetodoPago().addActionListener(e -> actualizarEstadisticas());
    }

    private void procesarBoleta(File archivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            String dniCliente = "";
            String metodoPago = "";
            Date fecha = null;
            double montoReservas = 0.0;
            double montoConsumos = 0.0;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            boolean enSeccionReservas = false;
            boolean enSeccionConsumos = false;

            while ((linea = reader.readLine()) != null) {
                if (linea.contains("DNI Cliente")) {
                    dniCliente = linea.split(":")[1].trim();
                } else if (linea.contains("Método Pago")) {
                    metodoPago = linea.split(":")[1].trim();
                } else if (linea.contains("Fecha Pago")) {
                    String fechaStr = linea.split(":")[1].trim();
                    fecha = dateFormat.parse(fechaStr);
                } else if (linea.contains("Subtotal Reservas: S/.")) {
                    String montoStr = linea.replace("Subtotal Reservas: S/.", "").trim();
                    montoReservas = Double.parseDouble(montoStr);
                } else if (linea.contains("Subtotal Consumos: S/.")) {
                    String montoStr = linea.replace("Subtotal Consumos: S/.", "").trim();
                    montoConsumos = Double.parseDouble(montoStr);
                }
            }

            if (!dniCliente.isEmpty() && fecha != null) {
                datosBoletas.add(new DatoBoleta(dniCliente, metodoPago, fecha,
                        montoReservas, montoConsumos));
            }
        } catch (IOException | ParseException e) {
            System.err.println("Error al procesar boleta " + archivo.getName() + ": " + e.getMessage());
        }
    }

    private void cargarDatosBoletas() {
        try {
            String carpetaBase = System.getProperty("user.dir");
            String carpetaBoletas = carpetaBase + File.separator + "Boletas";
            File directorio = new File(carpetaBoletas);

            if (!directorio.exists()) {
                return;
            }

            File[] archivos = directorio.listFiles((dir, name) -> name.startsWith("boleta_") && name.endsWith(".txt"));
            if (archivos != null) {
                for (File archivo : archivos) {
                    procesarBoleta(archivo);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al cargar las boletas: " + e.getMessage());
        }
    }

    private void actualizarEstadisticas() {
        String anioSeleccionado = (String) vista.getjComboBoxAnio().getSelectedItem();
        String mesSeleccionado = (String) vista.jComboBoxMes().getSelectedItem();
        String metodoPagoSeleccionado = (String) vista.getjComboBoxMetodoPago().getSelectedItem();

        double totalIngresosVentas = 0.0;
        double totalIngresosConsumibles = 0.0;

        Calendar cal = Calendar.getInstance();
        int mesNumero = obtenerNumeroMes(mesSeleccionado);

        for (DatoBoleta dato : datosBoletas) {
            cal.setTime(dato.fecha);
            int anioBoleta = cal.get(Calendar.YEAR);
            int mesBoleta = cal.get(Calendar.MONTH);

            if (String.valueOf(anioBoleta).equals(anioSeleccionado)
                    && mesBoleta == mesNumero
                    && (metodoPagoSeleccionado.equals("Todos")
                    || dato.metodoPago.equals(metodoPagoSeleccionado))) {

                totalIngresosVentas += dato.montoReservas;
                totalIngresosConsumibles += dato.montoConsumos;
            }
        }

        // Actualizar las etiquetas en la vista
        vista.getInfoIngVentas().setText(String.format("S/. %.2f", totalIngresosVentas));
        vista.getiInfoIngConsumibles().setText(String.format("S/. %.2f", totalIngresosConsumibles));
    }

    private int obtenerNumeroMes(String nombreMes) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        for (int i = 0; i < meses.length; i++) {
            if (meses[i].equals(nombreMes)) {
                return i;
            }
        }
        return 0;
    }

    private void volver() {
        try {
            vista.dispose();
            if (controladorAdm != null) {
                controladorAdm.mostrarMenuAdm();
            } else {
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
}
