/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

/**
 *
 * @author rodri
 */
import Vista.VistaPedido;
import Modelo.*;
import Vista.*;
import javax.swing.*;

public class ControladorRecep {

    private MenuRecep vista;
    private PersonalArreglo personalArreglo;
    private ArregloReserva arregloReserva;
    private HabitacionArreglo habitaciones;
    private ClienteArreglo clientes;
    private ArregloPedido arregloP;
    private ArregloConsumibles arregloC;

    // Constructor principal
    public ControladorRecep(MenuRecep vista, PersonalArreglo personalArreglo, String nombreUsuario,
            HabitacionArreglo habitaciones, ArregloPedido arregloP,
            ArregloConsumibles arregloC, ArregloReserva arregloReserva) {
        this.vista = vista;
        this.personalArreglo = personalArreglo;
        this.habitaciones = habitaciones;
        this.arregloC = arregloC;
        this.arregloP = arregloP;
        this.arregloReserva = arregloReserva;

        inicializarComponentes();
        mostrarMensajeBienvenida(nombreUsuario);
    }

    // Constructor secundario
    public ControladorRecep(MenuRecep vista, PersonalArreglo personalArreglo,
            HabitacionArreglo habitaciones, ArregloPedido arregloP,
            ArregloConsumibles arregloC, ArregloReserva arregloReserva) {
        this(vista, personalArreglo, "", habitaciones, arregloP, arregloC, arregloReserva);
    }

    private void inicializarComponentes() {
        this.vista.getBotonvolver().addActionListener(e -> volver());
        this.vista.getBotonReservaciones().addActionListener(e -> abrirReservaciones());
        this.vista.getBotonPedidos().addActionListener(e -> abrirPedidos());
        this.vista.getbotonPagos().addActionListener(e -> abrirPagos());
        this.vista.getbotonBoletas().addActionListener(e -> abrirBoletas());
    }

    private void mostrarMensajeBienvenida(String nombreUsuario) {
        vista.getMenajeUser().setText("Bienvenido " + nombreUsuario);
    }

    private void volver() {
        InicioSesion vistaIS = new InicioSesion();
        new ControladorInicio(vistaIS, personalArreglo, habitaciones, arregloP, arregloC, arregloReserva);
        vista.setVisible(false);
        vistaIS.setLocationRelativeTo(null);
        vistaIS.setVisible(true);
    }

    private void abrirReservaciones() {
        try {
            if (arregloReserva == null || arregloReserva.getIndice() == 0) {
                JOptionPane.showMessageDialog(vista, "No hay reservas disponibles.",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                VistaReservacion vistaR = new VistaReservacion();
                new ControladorReservacion(vistaR, arregloReserva, habitaciones, clientes, this);
                vista.setVisible(false);
                vistaR.setLocationRelativeTo(null);
                vistaR.setVisible(true);
            }

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            VistaReservacion vistaR = new VistaReservacion();
            new ControladorReservacion(vistaR, arregloReserva, habitaciones, clientes, this);
            vista.setVisible(false);
            vistaR.setLocationRelativeTo(null);
            vistaR.setVisible(true);
        } catch (Exception e) {
            mostrarError("Error al abrir reservaciones", e);
        }
    }

    private void abrirPedidos() {
        try {
            VistaPedido vistaPedido = new VistaPedido();
            new ControladorVistaPedido(vistaPedido, arregloP, arregloC, arregloReserva);
            vista.setVisible(false);
            vistaPedido.setLocationRelativeTo(null);
            vistaPedido.setVisible(true);
        } catch (Exception e) {
            mostrarError("Error al abrir pedidos", e);
        }
    }

    private void abrirPagos() {
        try {
            if (arregloReserva == null || arregloReserva.getIndice() == 0) {
                JOptionPane.showMessageDialog(vista, "No hay pagos disponibles.",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            VistaPago vistaPago = new VistaPago();
            new ControladorPago(vistaPago, arregloReserva, arregloP, this);
            vista.setVisible(false);
            vistaPago.setLocationRelativeTo(null);
            vistaPago.setVisible(true);
        } catch (Exception e) {
            mostrarError("Error al abrir pagos", e);
        }
    }

    private void abrirBoletas() {
        try {
            GestionBoletas vistaBoletas = new GestionBoletas();
            new ControladorGestionBoletas(vistaBoletas);
            vista.setVisible(false);
            vistaBoletas.setLocationRelativeTo(null);
            vistaBoletas.setVisible(true);
        } catch (Exception e) {
            mostrarError("Error al abrir boletas", e);
        }
    }

    private void mostrarError(String mensaje, Exception e) {
        JOptionPane.showMessageDialog(vista, mensaje + ": " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarMenuRecepcionista() {
        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
    }
}
