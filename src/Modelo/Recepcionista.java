package Modelo;

/**
 *
 * @author estefano
 */
public class Recepcionista extends Personal {

    public Recepcionista(String nom, String ape, String direccion, String dni, String telefono) {
        super(nom, ape, direccion, dni, telefono, "Recepcionista");
    }

    public Recepcionista(String dni, String contraseña) {
        super(dni, contraseña);
    }

    public void verPersonal() {
        PersonalArreglo arregloP = new PersonalArreglo();
        arregloP.mostrar();
    }

    public boolean verificarHabitacion(int id) {
        HabitacionArreglo arregloH = new HabitacionArreglo();
        Habitacion h = arregloH.buscarHabitacion(id);
        return h.isEstado();
    }

    public void verHabitacion() {
        HabitacionArreglo arregloH = new HabitacionArreglo();
        arregloH.getHabitaciones();
    }

    public Habitacion verHabitacion(int idHabitacion) {
        HabitacionArreglo arregloH = new HabitacionArreglo();
        return arregloH.buscarHabitacion(idHabitacion);
    }
}
