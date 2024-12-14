/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import Controlador.*;
import Modelo.*;
import Vista.*;

/**
 *
 * @author rodri
 */
public class Main {

    public static void main(String[] args) {
        PersonalArreglo personalArreglo = new PersonalArreglo();
        HabitacionArreglo habitaciones = new HabitacionArreglo();
        ArregloReserva reservas = new ArregloReserva(habitaciones);
        ArregloPedido arregloP = new ArregloPedido();
        ArregloConsumibles arregloC = new ArregloConsumibles();
        ArregloReserva arregloR = new ArregloReserva();

        // Cargar datos desde los archivos
        reservas.cargarDesdeArchivo();
        habitaciones.cargarDesdeArchivo();

        // Mostrar las reservas en consola
        Reserva[] todasLasReservas = reservas.getReservas();

        // Verificar si el arreglo de reservas tiene elementos
        if (todasLasReservas == null || todasLasReservas.length == 0) {
            System.out.println("No se encontraron reservas.");
        } else {
            // Iterar y mostrar las reservas válidas
            for (Reserva reserva : todasLasReservas) {
                if (reserva != null) {
                    System.out.println(reserva); // Asegúrate de que la reserva se imprima correctamente
                }
            }
        }
        InicioSesion vistaIS = new InicioSesion();
        new ControladorInicio(vistaIS, personalArreglo, habitaciones, arregloP, arregloC, arregloR);
        vistaIS.setLocationRelativeTo(null);
        vistaIS.setVisible(true);
    }
}
