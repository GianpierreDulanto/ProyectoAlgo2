package Modelo;

import java.io.*;
import java.util.Scanner;

/**
 *
 * @author estef
 */
public class HabitacionArreglo {

    private static final String ARCHIVO_HABITACIONES = "habitaciones.txt";
    private static final int CAPACIDAD_INICIAL = 100;
    private int indice;
    private Habitacion[] arregloHabitacion;

    public HabitacionArreglo() {
        this.indice = 0;
        this.arregloHabitacion = new Habitacion[CAPACIDAD_INICIAL];
        cargarDesdeArchivo();
    }

    public void agregar(Habitacion habitacion) {
        if (habitacion == null) {
            throw new IllegalArgumentException("La habitación no puede ser nula");
        }

        Habitacion habitacionExistente = buscarHabitacion(habitacion.getId_habitacion());

        if (habitacionExistente != null) {
            actualizarHabitacion(habitacion);
            return;
        }

        if (indice == arregloHabitacion.length) {
            redimensionar();
        }
        arregloHabitacion[indice++] = habitacion;
        guardarEnArchivo();
    }

    public Habitacion buscarHabitacion(int id) {
        for (int i = 0; i < indice; i++) {
            if (arregloHabitacion[i].getId_habitacion() == id) {
                return arregloHabitacion[i];
            }
        }
        return null;
    }

    public void actualizarHabitacion(Habitacion habitacionActualizada) {
        if (habitacionActualizada == null) {
            throw new IllegalArgumentException("La habitación actualizada no puede ser nula");
        }
        boolean encontrada = false;
        for (int i = 0; i < indice; i++) {
            if (arregloHabitacion[i].getId_habitacion() == habitacionActualizada.getId_habitacion()) {
                arregloHabitacion[i] = habitacionActualizada;
                encontrada = true;
                break;
            }
        }
        if (!encontrada) {
            throw new IllegalArgumentException("No se encontró la habitación para actualizar");
        }
        guardarEnArchivo();
    }

    public void eliminar(int id) {
        int posicion = -1;
        for (int i = 0; i < indice; i++) {
            if (arregloHabitacion[i].getId_habitacion() == id) {
                posicion = i;
                break;
            }
        }
        if (posicion != -1) {
            System.arraycopy(arregloHabitacion, posicion + 1, arregloHabitacion, posicion, indice - posicion - 1);
            arregloHabitacion[--indice] = null;
            guardarEnArchivo();
        }
    }

    private void redimensionar() {
        Habitacion[] nuevoArreglo = new Habitacion[arregloHabitacion.length * 2];
        System.arraycopy(arregloHabitacion, 0, nuevoArreglo, 0, arregloHabitacion.length);
        arregloHabitacion = nuevoArreglo;
    }

    private void guardarEnArchivo() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_HABITACIONES))) {
            for (int i = 0; i < indice; i++) {
                if (arregloHabitacion[i] != null) {
                    writer.println(arregloHabitacion[i].toString());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al guardar en archivo: " + e.getMessage());
        }
    }

    public void cargarDesdeArchivo() {
        File archivo = new File(ARCHIVO_HABITACIONES);
        if (!archivo.exists()) {
            System.out.println("El archivo de habitaciones no existe.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length < 4) {
                    System.err.println("Línea ignorada por formato incorrecto: " + linea);
                    continue;
                }

                try {
                    int id = Integer.parseInt(datos[0].trim());
                    String tipoNombre = datos[1].trim();
                    double precio = Double.parseDouble(datos[2].trim());
                    boolean estado = Boolean.parseBoolean(datos[3].trim());

                    TipoHabitacion tipo = new TipoHabitacion(tipoNombre, precio);
                    Habitacion habitacion = new Habitacion(id, tipo, estado);

                    if (datos.length > 4) {
                        String observaciones = datos[4].trim();
                        habitacion.setObservaciones(observaciones);
                    }

                    agregar(habitacion);
                    System.out.println("Habitación cargada: " + habitacion);  // Impresión para verificar

                } catch (NumberFormatException e) {
                    System.err.println("Error al procesar la línea: " + linea);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    public Habitacion[] getHabitaciones() {
        Habitacion[] resultado = new Habitacion[indice];
        System.arraycopy(arregloHabitacion, 0, resultado, 0, indice);
        return resultado;
    }

    public int getIndice() {
        return indice;
    }
}
