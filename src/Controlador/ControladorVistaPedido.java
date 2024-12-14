/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.*;
import Vista.*;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.util.Date;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;

public class ControladorVistaPedido {

    private VistaPedido vista;
    private ArregloPedido arregloP;
    private ArregloConsumibles arregloC;
    private ArregloReserva arregloR;

    public ControladorVistaPedido(VistaPedido vista, ArregloPedido arregloP, ArregloConsumibles arregloC, ArregloReserva arregloR) {
        this.vista = vista;
        this.arregloP = arregloP;
        this.arregloC = arregloC;
        this.arregloR = arregloR;

        this.vista.getPedidoAgregar().addActionListener(e -> agregarPedido());
        this.vista.getPedidoEliminar().addActionListener(e -> eliminarPedido());
        this.vista.getPedidoEditar().addActionListener(e -> editarPedido());
        this.vista.getPedidoSalir().addActionListener(e -> volver());

        vista.getPedidoTabla().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        vista.getPedidoTabla().setRowSelectionAllowed(true);

        vista.getPedidoTabla().setColumnSelectionAllowed(false); // Desactiva la selección de columnas

        cargarTabla();
        agregarListenerTabla();
        cargarOpcionesComboBox();
        agregarListenersComboBox();

    }

    private void agregarPedido() {
        try {
            // Validación de campos
            String DNI = vista.getPedidoIdCliente().getText();
            int nHabitacion = Integer.parseInt(vista.getPedidoHabitacion().getText());
            String bebidaSeleccionada = (String) vista.getPedidoBebida().getSelectedItem();
            String comidaSeleccionada = (String) vista.getPedidoComida().getSelectedItem();

            if (DNI.isEmpty()) {
                throw new DatosInvalidosException("El DNI no puede estar vacío.");
            }

            // Autenticar reserva
            if (!autenticarReserva(DNI, nHabitacion)) {
                throw new DatosInvalidosException("No existe una reserva con esos datos.");
            }

            // Creación del pedido
            int nuevoId = Integer.parseInt(DNI);
            Pedido nuevoPedido = new Pedido(nuevoId, nHabitacion, new Date());

            // Agregar consumibles si se han seleccionado
            if (bebidaSeleccionada != null) {
                Bebida bebida = (Bebida) arregloC.buscarConsumiblePorNombre(bebidaSeleccionada);
                if (bebida != null) {
                    nuevoPedido.agregarConsumible(bebida);
                }
            }

            if (comidaSeleccionada != null) {
                Comida comida = (Comida) arregloC.buscarConsumiblePorNombre(comidaSeleccionada);
                if (comida != null) {
                    nuevoPedido.agregarConsumible(comida);
                }
            }

            // Verificar que al menos un consumible haya sido agregado
            boolean tieneConsumibles = false;
            for (Consumible consumible : nuevoPedido.getConsumibles()) {
                if (consumible != null) {
                    tieneConsumibles = true;
                    break;
                }
            }

            if (!tieneConsumibles) {
                throw new DatosInvalidosException("Debe seleccionar al menos un consumible.");
            }

            // Agregar el pedido al modelo
            arregloP.agregarPedido(nuevoPedido);

            JOptionPane.showMessageDialog(vista, "Pedido agregado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            limpiarCampos();
            cargarTabla();
        } catch (DatosInvalidosException e) {
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "La habitación debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al agregar el pedido: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarPedido() {
        try {
            int filaSeleccionada = vista.getPedidoTabla().getSelectedRow();
            if (filaSeleccionada == -1) {
                throw new DatosInvalidosException("Debe seleccionar una fila para eliminar.");
            }

            // Obtener el ID del pedido desde la tabla
            int idPedido = Integer.parseInt((String) vista.getPedidoTabla().getValueAt(filaSeleccionada, 0));

            // Eliminar el pedido del arreglo
            arregloP.eliminarPedido(idPedido);

            JOptionPane.showMessageDialog(vista, "Pedido eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarTabla(); // Actualizar la tabla después de la eliminación
        } catch (DatosInvalidosException e) {
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al eliminar el pedido: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarPedido() {
        try {
            int filaSeleccionada = vista.getPedidoTabla().getSelectedRow();
            if (filaSeleccionada == -1) {
                throw new DatosInvalidosException("Debe seleccionar un pedido para editar.");
            }

            // Obtener el ID del pedido desde la tabla
            int idPedido = Integer.parseInt(vista.getPedidoTabla().getValueAt(filaSeleccionada, 0).toString());

            // Buscar el pedido en el arreglo
            Pedido pedido = arregloP.buscarPedido(idPedido);
            if (pedido == null) {
                throw new DatosInvalidosException("Pedido no encontrado.");
            }

            // Obtener los nuevos valores de los campos de texto
            int nuevaHabitacion = Integer.parseInt(vista.getPedidoHabitacion().getText());
            String nuevaBebida = (String) vista.getPedidoBebida().getSelectedItem();
            String nuevaComida = (String) vista.getPedidoComida().getSelectedItem();

            // Validar datos
            if (nuevaBebida == null && nuevaComida == null) {
                throw new DatosInvalidosException("Debe seleccionar al menos un consumible.");
            }

            // Actualizar datos del pedido
            pedido.setHabitacion(nuevaHabitacion);
            pedido.limpiarConsumibles();

            // Agregar los nuevos consumibles seleccionados
            if (nuevaBebida != null) {
                Bebida bebida = (Bebida) arregloC.buscarConsumiblePorNombre(nuevaBebida);
                if (bebida != null) {
                    pedido.agregarConsumible(bebida);
                }
            }

            if (nuevaComida != null) {
                Comida comida = (Comida) arregloC.buscarConsumiblePorNombre(nuevaComida);
                if (comida != null) {
                    pedido.agregarConsumible(comida);
                }
            }

            // Actualizar la tabla
            cargarTabla();
            JOptionPane.showMessageDialog(vista, "Pedido editado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // Limpiar campos
            limpiarCampos();

        } catch (DatosInvalidosException e) {
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "La habitación debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al editar el pedido: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean autenticarReserva(String DNI, int nHabitacion) {
        Reserva[] reservas = arregloR.getReservas();
        for (int i = 0; i < arregloR.getIndice(); i++) {
            Reserva reserva = reservas[i];
            if (reserva.getCliente().getDni().equals(DNI) && reserva.getHabitacion().getId_habitacion() == nHabitacion) {
                return true;
            }
        }
        return false;
    }

    private void volver() {
        MenuRecep vistaRecep = new MenuRecep();
        PersonalArreglo personalArreglo = new PersonalArreglo();
        HabitacionArreglo habitaciones = new HabitacionArreglo();
        ControladorRecep controladorRecep = new ControladorRecep(vistaRecep, personalArreglo, habitaciones, arregloP, arregloC, arregloR);
        vistaRecep.setLocationRelativeTo(null);
        vistaRecep.setVisible(true);
        vista.dispose();
    }

    private void cargarTabla() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); // Formato de fecha
        Pedido[] pedidos = arregloP.getArregloPedidos();
        String[][] data = new String[arregloP.getIndice()][5];

        for (int i = 0; i < arregloP.getIndice(); i++) {
            Pedido p = pedidos[i];
            data[i][0] = String.valueOf(p.getId());
            data[i][1] = String.valueOf(p.getHabitacion());
            data[i][2] = dateFormat.format(p.getFechaPedido()); // Formatear la fecha
            data[i][3] = String.valueOf(p.getMontoTotal());

            StringBuilder detalles = new StringBuilder();
            Consumible[] consumibles = p.getConsumibles();
            if (consumibles != null) {
                for (Consumible consumible : consumibles) {
                    if (consumible != null) {
                        detalles.append(consumible.getNombreConsumible()).append(" ");
                    }
                }
            }

            data[i][4] = detalles.toString().trim();
        }
        vista.getPedidoTabla().setModel(new DefaultTableModel(
                data,
                new String[]{"ID", "Habitacion", "Fecha", "Monto Total", "Detalles"}
        ));
    }

    private void agregarListenerTabla() {
        // Añadimos un listener para la selección de filas
        vista.getPedidoTabla().getSelectionModel().addListSelectionListener(e -> {
            // Verificamos que la selección no sea vacía
            if (!e.getValueIsAdjusting()) {
                int filaSeleccionada = vista.getPedidoTabla().getSelectedRow();
                if (filaSeleccionada != -1) {
                    // Obtener los valores de la fila seleccionada
                    String idPedido = (String) vista.getPedidoTabla().getValueAt(filaSeleccionada, 0);
                    String habitacion = (String) vista.getPedidoTabla().getValueAt(filaSeleccionada, 1);
                    String fecha = (String) vista.getPedidoTabla().getValueAt(filaSeleccionada, 2);
                    String montoTotal = (String) vista.getPedidoTabla().getValueAt(filaSeleccionada, 3);
                    String detalles = (String) vista.getPedidoTabla().getValueAt(filaSeleccionada, 4);

                    // Rellenar los campos de la vista con los valores de la fila seleccionada
                    vista.getPedidoIdCliente().setText(idPedido);
                    vista.getPedidoHabitacion().setText(habitacion);
                    vista.getPedidoMonto().setText(montoTotal);

                    // Para los consumibles (bebida/comida), establecer el seleccionado en base a los detalles
                    // Por ejemplo, si hay una bebida o comida en los detalles, seleccionarla en el combo box
                    // Asumiendo que los detalles están separados por un espacio
                    String[] consumibles = detalles.split(" ");
                    if (consumibles.length > 0) {
                        for (String consumible : consumibles) {
                            if (vista.getPedidoBebida().getItemCount() > 0 && vista.getPedidoBebida().getSelectedItem().equals(consumible)) {
                                vista.getPedidoBebida().setSelectedItem(consumible);
                            } else if (vista.getPedidoComida().getItemCount() > 0 && vista.getPedidoComida().getSelectedItem().equals(consumible)) {
                                vista.getPedidoComida().setSelectedItem(consumible);
                            }
                        }
                    }
                }
            }
        });
    }

    private void cargarOpcionesComboBox() {
        DefaultComboBoxModel<String> modeloBebidas = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> modeloComidas = new DefaultComboBoxModel<>();

        for (int i = 0; i < arregloC.getIndice(); i++) {
            Consumible consumible = arregloC.getArregloConsumibles()[i];
            if (consumible instanceof Bebida) {
                modeloBebidas.addElement(consumible.getNombreConsumible());
            } else if (consumible instanceof Comida) {
                modeloComidas.addElement(consumible.getNombreConsumible());
            }
        }

        vista.getPedidoBebida().setModel(modeloBebidas);
        vista.getPedidoComida().setModel(modeloComidas);
    }

    private void limpiarCampos() {
        vista.getPedidoIdCliente().setText("");
        vista.getPedidoHabitacion().setText("");
        vista.getPedidoBebida().setSelectedIndex(-1);
        vista.getPedidoComida().setSelectedIndex(-1);
    }

    private void agregarListenersComboBox() {
        vista.getPedidoBebida().addActionListener(e -> actualizarMontoPedido());
        vista.getPedidoComida().addActionListener(e -> actualizarMontoPedido());
    }

    private void actualizarMontoPedido() {
        double montoTotal = 0;

        // Obtener el precio de la bebida seleccionada
        String bebidaSeleccionada = (String) vista.getPedidoBebida().getSelectedItem();
        if (bebidaSeleccionada != null) {
            Bebida bebida = (Bebida) arregloC.buscarConsumiblePorNombre(bebidaSeleccionada);
            if (bebida != null) {
                montoTotal += bebida.getPrecio();
            }
        }

        // Obtener el precio de la comida seleccionada
        String comidaSeleccionada = (String) vista.getPedidoComida().getSelectedItem();
        if (comidaSeleccionada != null) {
            Comida comida = (Comida) arregloC.buscarConsumiblePorNombre(comidaSeleccionada);
            if (comida != null) {
                montoTotal += comida.getPrecio();
            }
        }

        // Actualizar el monto total en el JLabel
        vista.getPedidoMonto().setText(String.valueOf(montoTotal));

        // Asegurarse de que el JLabel se actualice correctamente
        SwingUtilities.invokeLater(() -> {
            vista.getPedidoMonto().revalidate();
            vista.getPedidoMonto().repaint();
        });
    }

}
