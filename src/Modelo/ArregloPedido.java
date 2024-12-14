package Modelo;

import java.io.*;
import java.util.Scanner;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ArregloPedido {

    private int indice;
    private Pedido[] arregloPedidos;

    public ArregloPedido() {
        this.indice = 0;
        this.arregloPedidos = new Pedido[100];
        cargarDesdeArchivo();
    }

    public int getIndice() {
        return indice;
    }

    public Pedido[] getArregloPedidos() {
        return arregloPedidos;
    }

    public void agregarPedido(Pedido pedido) {
        if (indice == arregloPedidos.length) {
            redimensionar();
        }
        arregloPedidos[indice] = pedido;
        indice++;
        guardarEnArchivo();
    }

    public Pedido buscarPedido(int id) {
        for (int i = 0; i < indice; i++) {
            if (arregloPedidos[i].getId() == id) {
                return arregloPedidos[i];
            }
        }
        return null;
    }

    public void eliminarPedido(int id) {
        for (int i = 0; i < indice; i++) {
            if (arregloPedidos[i].getId() == id) {
                for (int j = i; j < indice - 1; j++) {
                    arregloPedidos[j] = arregloPedidos[j + 1];
                }
                arregloPedidos[--indice] = null;
                guardarEnArchivo();
                break;
            }
        }
    }

    public void mostrarPedidos() {
        for (int i = 0; i < indice; i++) {
            System.out.println("ID: " + arregloPedidos[i].getId()
                    + ", Fecha: " + arregloPedidos[i].getFechaPedido()
                    + ", Monto Total: " + arregloPedidos[i].getMontoTotal());
        }
    }

    public void guardarEnArchivo() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Formato de fecha
        try (PrintWriter writer = new PrintWriter(new FileWriter("pedidos.txt"))) {
            for (int i = 0; i < indice; i++) {
                Pedido pedido = arregloPedidos[i];
                StringBuilder consumiblesStr = new StringBuilder();

                for (Consumible consumible : pedido.getConsumibles()) {
                    if (consumible != null) {
                        consumiblesStr.append(consumible.getClass().getSimpleName())
                                .append(",")
                                .append(consumible.getNombreConsumible().replace(",", "\\,"))
                                .append(",")
                                .append(consumible.getPrecio())
                                .append(",")
                                .append(consumible instanceof Bebida ? ((Bebida) consumible).getTipoBebida() : ((Comida) consumible).getTipoComida())
                                .append(";");
                    }
                }

                writer.println(pedido.getId() + ","
                        + pedido.getHabitacion() + ","
                        + dateFormat.format(pedido.getFechaPedido()) + ","
                        + pedido.getMontoTotal() + ","
                        + consumiblesStr.toString());
            }
        } catch (IOException e) {
            System.err.println("Error al guardar en archivo 'pedidos.txt': " + e.getMessage());
        }
    }

    public void cargarDesdeArchivo() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        File archivo = new File("pedidos.txt");
        if (!archivo.exists()) {
            System.out.println("El archivo de pedidos no existe. Se creará uno nuevo al guardar.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",", 5); // Divide en 5 partes, los consumibles se toman completos en el último índice
                if (datos.length < 5) {
                    System.out.println("Línea malformada, ignorada: " + linea);
                    continue;
                }

                int id = Integer.parseInt(datos[0]);
                int nHabitacion = Integer.parseInt(datos[1]);
                Date fechaPedido = dateFormat.parse(datos[2]);
                double montoTotal = Double.parseDouble(datos[3]);
                Pedido pedido = new Pedido(id, nHabitacion, fechaPedido, montoTotal);

                // Procesar consumibles
                String[] consumiblesDatos = datos[4].split(";");
                for (String consumibleDato : consumiblesDatos) {
                    if (!consumibleDato.isEmpty()) {
                        String[] consumibleInfo = consumibleDato.split(",");
                        if (consumibleInfo.length < 4) {
                            System.out.println("Consumible malformado en línea: " + linea);
                            continue;
                        }

                        String tipo = consumibleInfo[0];
                        String nombre = consumibleInfo[1].replace("\\,", ","); // Desescapar comas
                        double precio = Double.parseDouble(consumibleInfo[2]);
                        String extra = consumibleInfo[3];

                        if ("Bebida".equals(tipo)) {
                            Bebida bebida = new Bebida(nombre, precio, extra);
                            pedido.agregarConsumible(bebida);
                        } else if ("Comida".equals(tipo)) {
                            Comida comida = new Comida(nombre, precio, extra);
                            pedido.agregarConsumible(comida);
                        }
                    }
                }

                arregloPedidos[indice++] = pedido; // Agregar el pedido al arreglo
            }
        } catch (IOException | ParseException e) {
            System.err.println("Error al cargar el archivo 'pedidos.txt': " + e.getMessage());
        }
    }

    private void redimensionar() {
        Pedido[] nuevoArreglo = new Pedido[arregloPedidos.length * 2];
        System.arraycopy(arregloPedidos, 0, nuevoArreglo, 0, arregloPedidos.length);
        arregloPedidos = nuevoArreglo;
    }

    public Pedido buscarPedidoPorId(int id) {
        for (int i = 0; i < indice; i++) {
            if (arregloPedidos[i].getId() == id) {
                return arregloPedidos[i];
            }
        }
        return null;
    }

}
