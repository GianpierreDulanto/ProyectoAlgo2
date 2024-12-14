/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author rodri
 */
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ArregloReserva {

    private static final String ARCHIVO_RESERVAS = "reservas.txt";
    private static final int CAPACIDAD_INICIAL = 100;
    private int indice;
    private Reserva[] arregloReserva;
    private HabitacionArreglo habitaciones;
    private int ultimoIdReserva = 0;

    public ArregloReserva(HabitacionArreglo habitaciones) {
        this.indice = 0;
        this.arregloReserva = new Reserva[CAPACIDAD_INICIAL];
        this.habitaciones = habitaciones;
        cargarDesdeArchivo();
    }

    public ArregloReserva() {
        this.indice = 0;
        this.arregloReserva = new Reserva[CAPACIDAD_INICIAL];
        this.habitaciones = new HabitacionArreglo(); // Puedes inicializar con un objeto vacío si es necesario
        cargarDesdeArchivo();
    }

    public void agregar(Reserva reserva) {
        if (reserva == null) {
            throw new IllegalArgumentException("La reserva no puede ser nula");
        }
        if (buscarReserva(reserva.getIdReserva()) != null) {
            throw new IllegalArgumentException("Ya existe una reserva con ese ID");
        }
        if (indice == arregloReserva.length) {
            redimensionar();
        }
        if (!verificarDisponibilidad(reserva)) {
            throw new IllegalStateException("La habitación no está disponible para las fechas seleccionadas");
        }
        arregloReserva[indice++] = reserva;
        guardarEnArchivo();
    }

    private boolean verificarDisponibilidad(Reserva nuevaReserva) {
        if (indice == 0) {
            return true;
        }

        for (int i = 0; i < indice; i++) {
            Reserva reservaExistente = arregloReserva[i];
            if (reservaExistente.getHabitacion().getId_habitacion() == nuevaReserva.getHabitacion().getId_habitacion()
                    && !reservaExistente.getEstado().equals("CANCELADA")) {
                if (!(nuevaReserva.getFinReserva().isBefore(reservaExistente.getInicioReserva())
                        || nuevaReserva.getInicioReserva().isAfter(reservaExistente.getFinReserva()))) {
                    return false;
                }
            }
        }
        return true;
    }

    public Reserva buscarReserva(int id) {
        if (indice == 0) {
            return null;
        }

        for (int i = 0; i < indice; i++) {
            if (arregloReserva[i].getIdReserva() == id) {
                return arregloReserva[i];
            }
        }
        return null;
    }

    public int generarNuevoIdReserva() {
        if (indice == 0) {
            return 1;
        }
        return arregloReserva[indice - 1].getIdReserva() + 1;
    }

    public void actualizarReserva(Reserva reservaActualizada) {
        if (reservaActualizada == null) {
            throw new IllegalArgumentException("La reserva actualizada no puede ser nula");
        }
        boolean encontrada = false;
        for (int i = 0; i < indice; i++) {
            if (arregloReserva[i].getIdReserva() == reservaActualizada.getIdReserva()) {
                arregloReserva[i] = reservaActualizada;
                encontrada = true;
                break;
            }
        }
        if (!encontrada) {
            throw new IllegalArgumentException("No se encontró la reserva para actualizar");
        }
        guardarEnArchivo();
    }

    public void eliminar(int id) {
        int posicion = -1;
        for (int i = 0; i < indice; i++) {
            if (arregloReserva[i].getIdReserva() == id) {
                posicion = i;
                break;
            }
        }
        if (posicion != -1) {
            System.arraycopy(arregloReserva, posicion + 1, arregloReserva, posicion, indice - posicion - 1);
            arregloReserva[--indice] = null;
            guardarEnArchivo();
        }
    }

    private void redimensionar() {
        Reserva[] nuevoArreglo = new Reserva[arregloReserva.length * 2];
        System.arraycopy(arregloReserva, 0, nuevoArreglo, 0, arregloReserva.length);
        arregloReserva = nuevoArreglo;
    }

    public void guardarEnArchivo() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_RESERVAS))) {
            for (int i = 0; i < indice; i++) {
                if (arregloReserva[i] != null) {
                    writer.println(arregloReserva[i].toString());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al guardar en archivo: " + e.getMessage());
        }
    }

    public void cargarDesdeArchivo() {
        File archivo = new File(ARCHIVO_RESERVAS);
        if (!archivo.exists()) {
            System.out.println("El archivo de reservas no existe.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                System.out.println("Leyendo línea: " + linea); // Verifica la línea leída

                String[] datos = linea.split(",");
                if (datos.length == 7) {
                    try {
                        if (datos[0].isEmpty() || datos[1].isEmpty() || datos[2].isEmpty()
                                || datos[3].isEmpty() || datos[4].isEmpty() || datos[5].isEmpty()
                                || datos[6].isEmpty()) {
                            System.out.println("Error: Línea con datos vacíos, se omite: " + linea);
                            continue;
                        }

                        int idReserva = Integer.parseInt(datos[0]);
                        int idHabitacion = Integer.parseInt(datos[1]);
                        String dniCliente = datos[2];
                        LocalDate fechaInicio = LocalDate.parse(datos[3], DateTimeFormatter.ISO_LOCAL_DATE);
                        LocalDate fechaFin = LocalDate.parse(datos[4], DateTimeFormatter.ISO_LOCAL_DATE);
                        double montoTotal = Double.parseDouble(datos[6]);

                        String estado = datos[5];

                        Habitacion habitacion = habitaciones.buscarHabitacion(idHabitacion);
                        if (habitacion != null) {
                            Cliente cliente = new Cliente(dniCliente);

                            Reserva reserva = new Reserva(idReserva, habitacion, cliente, fechaInicio, fechaFin);
                            reserva.setEstado(estado);
                            reserva.setMontoTotal(montoTotal);

                            if (buscarReserva(idReserva) == null) {
                                agregar(reserva);
                            } else {
                                System.out.println("Reserva con ID " + idReserva + " ya existe. No se agregará.");
                            }
                        } else {
                            System.out.println("Habitación no encontrada para la reserva con ID: " + idHabitacion);
                        }
                    } catch (NumberFormatException | DateTimeParseException e) {
                        System.out.println("Error al procesar la línea: " + linea + ". Formato incorrecto.");
                    }
                } else {
                    System.out.println("Línea mal formada (esperados 7 campos): " + linea);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    public Reserva[] getReservas() {
        return arregloReserva;
    }

    public int getIndice() {
        return indice;
    }
}
