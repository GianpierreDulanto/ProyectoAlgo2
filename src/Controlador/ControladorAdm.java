/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

/**
 *
 * @author rodri
 */
import Modelo.*;
import Vista.*;
import javax.swing.JOptionPane;

public class ControladorAdm {

    private MenuAdm vista;
    private PersonalArreglo personalArreglo;
    private ClienteArreglo clientes;
    private HabitacionArreglo habitaciones;
    private GestionReservas vistaReservacion;
    private ArregloReserva reservas;
    private Personal personal;
    private ArregloPedido arregloP;
    private ArregloConsumibles arregloC;

    public ControladorAdm(MenuAdm vista, PersonalArreglo personalArreglo, Personal personal, ClienteArreglo clientes,
            HabitacionArreglo habitaciones, ArregloPedido arregloP, ArregloConsumibles arregloC) {
        this.vista = vista;
        this.personalArreglo = personalArreglo;
        this.personal = personal;
        this.clientes = clientes;
        this.habitaciones = habitaciones;
        this.arregloP = arregloP;
        this.arregloC = arregloC;
        mostrarMensajeBienvenida(personal.getNombre());

        this.vista.getBotonvolver().addActionListener(e -> volver());
        this.vista.getBotonGestionarPersonal().addActionListener(e -> gestionarPersonal(personal));
        this.vista.getBotonGestionarReservas().addActionListener(e -> gestionarReservas(reservas, habitaciones, clientes));
        this.vista.getBotonVerIngresos().addActionListener(e -> verIngresos(arregloP, reservas));
    }

    private void mostrarMensajeBienvenida(String nombreUsuario) {
        vista.getMenajeUser().setText("Bienvenido " + nombreUsuario);
    }

    private void volver() {
        InicioSesion vistaIS = new InicioSesion();
        new ControladorInicio(vistaIS, personalArreglo, habitaciones, arregloP, arregloC, reservas);
        vista.setVisible(false);
        vistaIS.setLocationRelativeTo(null);
        vistaIS.setVisible(true);
    }

    private void verIngresos(ArregloPedido pedidos, ArregloReserva reservas) {
        try {
            VistaIngresos vistaI = new VistaIngresos();
            new ControladorVistaIngresos(vistaI, this, pedidos, reservas);
            vista.setVisible(false);
            vistaI.setLocationRelativeTo(null);
            vistaI.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al mostrar ingresos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gestionarPersonal(Personal personal) {
        try {
            GestionPersonal vistaGP = new GestionPersonal();
            new ControladorGestionPersonal(vistaGP, personalArreglo, this);
            vista.setVisible(false);
            vistaGP.setLocationRelativeTo(null);
            vistaGP.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al gestionar personal: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gestionarReservas(ArregloReserva arregloReserva, HabitacionArreglo habitaciones, ClienteArreglo clientes) {
        try {
            if (arregloReserva == null) {
                arregloReserva = new ArregloReserva();
            }

            if (arregloReserva.getIndice() == 0) {
                JOptionPane.showMessageDialog(vista, "No hay reservas disponibles.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            GestionReservas vistaReservas = new GestionReservas();
            new ControladorGestionReservas(vistaReservas, arregloReserva, habitaciones, clientes, this);
            vista.setVisible(false);
            vistaReservas.setLocationRelativeTo(null);
            vistaReservas.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al gestionar reservas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void mostrarMenuAdm() {
        vista.setLocationRelativeTo(null);
        vista.setVisible(true);
    }
}
