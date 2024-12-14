package Modelo;

import java.io.*;
import java.util.Scanner;

/**
 *
 * @author estefano
 */
public class PersonalArreglo {

    private int indice;
    private Personal[] arregloPersonal;

    public PersonalArreglo() {
        this.indice = 0;
        this.arregloPersonal = new Personal[100];
        cargarDesdeArchivo();
    }

    public void agregar(Personal personal) {
        for (int i = 0; i < indice; i++) {
            if (arregloPersonal[i].getDNI().equals(personal.getDNI())) {
                System.out.println("Ya existe un personal con el DNI " + personal.getDNI() + ". No se agrego de nuevo.");
                return;
            }
        }
        if (indice == arregloPersonal.length) {
            redimensionar();
        }
        arregloPersonal[indice] = personal;
        indice++;
        guardarEnArchivo();
    }

    public Personal[] getPersonal() {
        return arregloPersonal;
    }

    public int getIndice() {
        return indice;
    }

    public Personal buscarPersonal(int id) {
        for (int i = 0; i < indice; i++) {
            if (arregloPersonal[i].getId() == id) {
                return arregloPersonal[i];
            }
        }
        return null;
    }

    public void actualizarPersonal(Personal personalActualizado) {
        for (int i = 0; i < indice; i++) {
            if (arregloPersonal[i].getId() == personalActualizado.getId()) {
                arregloPersonal[i] = personalActualizado;
                guardarEnArchivo();
                break;
            }
        }
    }

    public void guardarEnArchivo() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("personal.txt"))) {
            for (int i = 0; i < indice; i++) {
                writer.println(arregloPersonal[i].toString());
            }
        } catch (IOException e) {
            System.out.println("Error al guardar en archivo: " + e.getMessage());
        }
    }

    private void cargarDesdeArchivo() {
        try (Scanner scanner = new Scanner(new File("personal.txt"))) {
            int maxId = 0;
            while (scanner.hasNextLine()) {
                String[] datos = scanner.nextLine().trim().split(",");
                String rol = datos[0];
                int id = Integer.parseInt(datos[1]);
                maxId = Math.max(maxId, id);

                Personal p;
                if ("Administrador".equalsIgnoreCase(rol)) {
                    p = new Administrador(datos[4], datos[5], datos[8], datos[6], datos[7]);
                } else {
                    p = new Recepcionista(datos[4], datos[5], datos[8], datos[6], datos[7]);
                }

                p.setId(id);
                p.setUsuario(datos[2]);
                p.setContraseña(datos[3]);
                agregar(p);
            }
            Personal.setUltimoId(maxId);
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado. Creando nuevo archivo.");
        }
    }

    public Personal buscarPersonalPorDNI(String dni) {
        for (int i = 0; i < indice; i++) {
            if (arregloPersonal[i].getDNI().equals(dni)) {
                return arregloPersonal[i];
            }
        }
        return null;
    }

    public void eliminar(int id) {
        for (int i = 0; i < indice; i++) {
            if (arregloPersonal[i].getId() == id) {
                for (int j = i; j < indice - 1; j++) {
                    arregloPersonal[j] = arregloPersonal[j + 1];
                }
                arregloPersonal[--indice] = null;
                guardarEnArchivo();
                break;
            }
        }
    }

    public void mostrar() {
        for (int i = 0; i < indice; i++) {
            System.out.println("ID: " + arregloPersonal[i].getId()
                    + ", Rol: " + arregloPersonal[i].getRol()
                    + ", Usuario: " + arregloPersonal[i].getUsuario()
                    + ", Contrasenia: " + arregloPersonal[i].getContraseña()
                    + ", Nombre: " + arregloPersonal[i].getNombre()
                    + ", Apellido: " + arregloPersonal[i].getApellido()
                    + ", DNI: " + arregloPersonal[i].getDNI()
                    + ", Direccion: " + arregloPersonal[i].getDireccion()
                    + ", Telefono: " + arregloPersonal[i].getTelefono());
        }
    }

    private void redimensionar() {
        Personal[] nuevoArreglo = new Personal[arregloPersonal.length * 2];
        System.arraycopy(arregloPersonal, 0, nuevoArreglo, 0, arregloPersonal.length);
        arregloPersonal = nuevoArreglo;
    }
}
