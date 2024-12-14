package Modelo;

import java.io.*;
import java.util.Scanner;

public class ArregloConsumibles {

    private int indice;
    private Consumible[] arregloConsumibles;

    public ArregloConsumibles() {
        this.indice = 0;
        this.arregloConsumibles = new Consumible[100];
        cargarDesdeArchivo();
    }

    public void agregarConsumible(Consumible consumible) {
        if (indice == arregloConsumibles.length) {
            redimensionar();
        }
        arregloConsumibles[indice] = consumible;
        indice++;
        guardarEnArchivo();
    }

    public void eliminarConsumible(String nombre) {
        for (int i = 0; i < indice; i++) {
            if (arregloConsumibles[i].getNombreConsumible().equals(nombre)) {
                for (int j = i; j < indice - 1; j++) {
                    arregloConsumibles[j] = arregloConsumibles[j + 1];
                }
                arregloConsumibles[--indice] = null;
                guardarEnArchivo();
                break;
            }
        }
    }

    public void modificarConsumible(String nombre, Consumible nuevoConsumible) {
        for (int i = 0; i < indice; i++) {
            if (arregloConsumibles[i].getNombreConsumible().equals(nombre)) {
                arregloConsumibles[i] = nuevoConsumible;
                guardarEnArchivo();
                break;
            }
        }
    }

    private void guardarEnArchivo() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("consumibles.txt"))) {
            for (int i = 0; i < indice; i++) {
                Consumible consumible = arregloConsumibles[i];
                if (consumible instanceof Bebida) {
                    Bebida bebida = (Bebida) consumible;
                    writer.println("Bebida," + bebida.getNombreConsumible() + "," + bebida.getPrecio() + "," + bebida.getTipoBebida());
                } else if (consumible instanceof Comida) {
                    Comida comida = (Comida) consumible;
                    writer.println("Comida," + comida.getNombreConsumible() + "," + comida.getPrecio() + "," + comida.getTipoComida());
                }
            }
        } catch (IOException e) {
            System.out.println("Error al guardar en archivo: " + e.getMessage());
        }
    }

    private void cargarDesdeArchivo() {
        try (Scanner scanner = new Scanner(new File("consumibles.txt"))) {
            while (scanner.hasNextLine()) {
                String[] datos = scanner.nextLine().split(",");
                if (datos[0].equals("Bebida")) {
                    Bebida bebida = new Bebida(datos[1], Double.parseDouble(datos[2]), datos[3]);
                    agregarConsumible(bebida);
                } else if (datos[0].equals("Comida")) {
                    Comida comida = new Comida(datos[1], Double.parseDouble(datos[2]), datos[3]);
                    agregarConsumible(comida);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado. Creando nuevo archivo.");
        }
    }

    public void mostrarConsumibles() {
        for (int i = 0; i < indice; i++) {
            System.out.println("Nombre: " + arregloConsumibles[i].getNombreConsumible()
                    + ", Precio: " + arregloConsumibles[i].getPrecio()
                    + (arregloConsumibles[i] instanceof Bebida ? ", Tipo de Bebida: " + ((Bebida) arregloConsumibles[i]).getTipoBebida()
                            : ", Tipo de Comida: " + ((Comida) arregloConsumibles[i]).getTipoComida()));
        }
    }

    private void redimensionar() {
        Consumible[] nuevoArreglo = new Consumible[arregloConsumibles.length * 2];
        System.arraycopy(arregloConsumibles, 0, nuevoArreglo, 0, arregloConsumibles.length);
        arregloConsumibles = nuevoArreglo;
    }

    public Consumible buscarConsumiblePorNombre(String nombreProducto) {
        for (int i = 0; i < indice; i++) {
            if (arregloConsumibles[i].getNombreConsumible().equalsIgnoreCase(nombreProducto)) {
                return arregloConsumibles[i];
            }
        }
        return null;
    }

    public int getIndice() {
        return indice;
    }

    public Consumible[] getArregloConsumibles() {
        return arregloConsumibles;
    }

}
