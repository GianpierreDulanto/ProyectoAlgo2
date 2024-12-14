package Modelo;

import java.io.*;

public class ClienteArreglo {
    private static final String ARCHIVO_CLIENTES = "cliente.txt";
    private static final int CAPACIDAD_INICIAL = 100;
    private int indice;
    private Cliente[] arregloCliente;

    public ClienteArreglo() {
        this.indice = 0;
        this.arregloCliente = new Cliente[CAPACIDAD_INICIAL];
        cargarDesdeArchivo();
    }

    public void agregar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }
        if (buscarCliente(cliente.getDni()) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con ese DNI");
        }
        if (indice == arregloCliente.length) {
            redimensionar();
        }
        arregloCliente[indice++] = cliente;
        guardarEnArchivo();
    }

    public Cliente buscarCliente(String dni) {
        for (int i = 0; i < indice; i++) {
            if (arregloCliente[i].getDni().equals(dni)) {
                return arregloCliente[i];
            }
        }
        return null;
    }

    public Cliente buscarClientePorHabitacion(int idHabitacion) {
        for (int i = 0; i < indice; i++) {
            if (arregloCliente[i].getHabitacionAsignada() != null &&
                arregloCliente[i].getHabitacionAsignada().getId_habitacion() == idHabitacion) {
                return arregloCliente[i];
            }
        }
        return null;
    }

    public void actualizarCliente(Cliente clienteActualizado) {
        if (clienteActualizado == null) {
            throw new IllegalArgumentException("El cliente actualizado no puede ser nulo");
        }
        boolean encontrado = false;
        for (int i = 0; i < indice; i++) {
            if (arregloCliente[i].getDni().equals(clienteActualizado.getDni())) {
                arregloCliente[i] = clienteActualizado;
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            throw new IllegalArgumentException("No se encontró el cliente para actualizar");
        }
        guardarEnArchivo();
    }

    public void eliminar(String dni) {
        int posicion = -1;
        for (int i = 0; i < indice; i++) {
            if (arregloCliente[i].getDni().equals(dni)) {
                posicion = i;
                break;
            }
        }
        if (posicion != -1) {
            System.arraycopy(arregloCliente, posicion + 1, arregloCliente, posicion, indice - posicion - 1);
            arregloCliente[--indice] = null;
            guardarEnArchivo();
        }
    }

    private void guardarEnArchivo() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_CLIENTES))) {
            for (int i = 0; i < indice; i++) {
                writer.println(arregloCliente[i].toString());
            }
        } catch (IOException e) {
            System.err.println("Error al guardar en archivo: " + e.getMessage());
        }
    }

    private void cargarDesdeArchivo() {
        File archivo = new File(ARCHIVO_CLIENTES);
        if (!archivo.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length >= 1) {
                    Cliente cliente = new Cliente(datos[1]);  // Solo se toma el DNI
                    agregar(cliente);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar el archivo: " + e.getMessage());
        }
    }

    public void mostrar() {
        for (int i = 0; i < indice; i++) {
            Cliente c = arregloCliente[i];
            System.out.printf("ID: %d, DNI: %s, Habitación: %s%n",
                c.getId(),
                c.getDni(),
                (c.getHabitacionAsignada() != null ? c.getHabitacionAsignada().getId_habitacion() : "No asignada"));
        }
    }

    private void redimensionar() {
        Cliente[] nuevoArreglo = new Cliente[arregloCliente.length * 2];
        System.arraycopy(arregloCliente, 0, nuevoArreglo, 0, arregloCliente.length);
        arregloCliente = nuevoArreglo;
    }

    public int getIndice() {
        return indice;
    }

    public Cliente[] getClientes() {
        Cliente[] resultado = new Cliente[indice];
        System.arraycopy(arregloCliente, 0, resultado, 0, indice);
        return resultado;
    }
}
