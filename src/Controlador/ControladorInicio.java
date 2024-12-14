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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ControladorInicio {

    private InicioSesion vista;
    private PersonalArreglo personalArreglo;
    private ClienteArreglo clientes;
    private HabitacionArreglo habitaciones;
    private ArregloPedido arregloP;
    private ArregloConsumibles arregloC;
    private ArregloReserva arregloR;

    public ControladorInicio(InicioSesion vista, PersonalArreglo personalArreglo, HabitacionArreglo habitaciones, ArregloPedido arregloP, ArregloConsumibles arregloC, ArregloReserva arregloR) {
        this.vista = vista;
        this.personalArreglo = personalArreglo;
        this.habitaciones = habitaciones;
        this.arregloC = arregloC;
        this.arregloP = arregloP;
        this.arregloR = arregloR;
        this.vista.getBotonLogin().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                autenticarUsuario();
            }
        });
    }

    private void autenticarUsuario() {
        String dni = vista.getUsername().getText();
        String contrasena = vista.getPassword().getText();
        Personal personal = buscarPersonalPorDNI(dni);

        if (personal != null && personal.getContraseña().equals(contrasena)) {
            if (personal instanceof Recepcionista) {
                iniciarVistaRecep(personal);
            } else if (personal instanceof Administrador) {
                iniciarVistaAdm(personal);
            }
        } else {
            vista.getMensajeError().setText("DNI o contraseña incorrecta.");
        }
    }

    private Personal buscarPersonalPorDNI(String dni) {
        for (int i = 0; i < personalArreglo.getIndice(); i++) {
            Personal personal = personalArreglo.getPersonal()[i];
            if (personal != null && personal.getDNI().equals(dni)) {
                return personal;
            }
        }
        return null;
    }

    private void iniciarVistaRecep(Personal personal) {
        MenuRecep vistaRecep = new MenuRecep();
        ControladorRecep controladorRecep = new ControladorRecep(vistaRecep, personalArreglo, personal.getNombre(), habitaciones, arregloP, arregloC, arregloR);
        vistaRecep.setLocationRelativeTo(null);
        vistaRecep.setVisible(true);
        vista.dispose();
    }

    private void iniciarVistaAdm(Personal personal) {
        MenuAdm vistaAdm = new MenuAdm();
        ControladorAdm controladorAdm = new ControladorAdm(vistaAdm, personalArreglo, personal, clientes, habitaciones, arregloP, arregloC);
        vistaAdm.setLocationRelativeTo(null);
        vistaAdm.setVisible(true);
        vista.dispose();
    }
}
