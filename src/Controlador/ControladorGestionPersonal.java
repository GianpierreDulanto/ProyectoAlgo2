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
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

public class ControladorGestionPersonal {
    private GestionPersonal vista;
    private PersonalArreglo modelo;
    private ControladorAdm controladorAdm;

    public ControladorGestionPersonal(GestionPersonal vista , PersonalArreglo modelo, ControladorAdm controladorAdm) {
        this.vista = vista;
        this.modelo = modelo;
        this.controladorAdm = controladorAdm;
        this.vista.getAgregarButton().addActionListener(e -> agregarPersonal());
        this.vista.getElminarButton().addActionListener(e -> eliminarPersonal());
        this.vista.getVolverButton().addActionListener(e -> volver());

        cargarTabla();
        agregarListenerTabla();
    }

    private void cargarTabla() {
        Personal[] personalArray = modelo.getPersonal();
        String[][] data = new String[modelo.getIndice()][5];

        for (int i = 0; i < modelo.getIndice(); i++) {
            Personal p = personalArray[i];
            data[i][0] = p.getNombre() + " " + p.getApellido();
            data[i][1] = p.getDNI();
            data[i][2] = p.getRol();
            data[i][3] = p.getUsuario();
            data[i][4] = p.getContraseña();
        }
        vista.getPersonalTable().setModel(new DefaultTableModel(
            data,
            new String[] {"Nombres y Apellidos", "DNI", "Ocupación", "Usuario", "Contraseña"}
        ));
    }

    private void volver() {
        try {
            vista.dispose(); // Cerrar la ventana actual de pagos
            
            // Si tenemos una referencia al controlador anterior, usamos su método
            if (controladorAdm != null) {
                controladorAdm.mostrarMenuAdm();
            } else {
                // Si no tenemos la referencia, creamos una nueva instancia del menú
                MenuAdm nuevaVista = new MenuAdm();
                nuevaVista.setLocationRelativeTo(null);
                nuevaVista.setVisible(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, 
                "Error al volver al menú principal: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void agregarPersonal() {
        try {
            String nombre = vista.getNombreTextField().getText();
            String apellido = vista.getApellidoTextField().getText();
            String direccion = vista.getDireccionTextField().getText();
            String dni = vista.getDNITextField().getText();
            String telefono = vista.getTelefonoTextField().getText();
            String rol = (String) vista.getRolComboBox().getSelectedItem();

            if (nombre.isEmpty() || apellido.isEmpty() || direccion.isEmpty() || dni.isEmpty() || telefono.isEmpty()) {
                throw new DatosInvalidosException("Todos los campos deben estar llenos.");
            }

            Personal personal;
            if ("Administrador".equalsIgnoreCase(rol)) {
                personal = new Administrador(nombre, apellido, direccion, dni, telefono);
            } else {
                personal = new Recepcionista(nombre, apellido, direccion, dni, telefono);
            }

            modelo.agregar(personal);
            JOptionPane.showMessageDialog(vista, "Personal agregado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            limpiarCampos();
            cargarTabla();
        } catch (DatosInvalidosException e) {
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al agregar personal: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    

    
    private void eliminarPersonal() {
        try {
            int selectedRow = vista.getPersonalTable().getSelectedRow();

            if (selectedRow == -1) {
                throw new PersonalNoSeleccionadoException("Seleccione un personal a eliminar.");
            }

            String dni = (String) vista.getPersonalTable().getValueAt(selectedRow, 1);
            Personal personal = modelo.buscarPersonalPorDNI(dni);

            if (personal != null) {
                modelo.eliminar(personal.getId());
                JOptionPane.showMessageDialog(vista, "Personal eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                cargarTabla();
            } else {
                throw new PersonalNoEncontradoException("Personal no encontrado.");
            }
        } catch (PersonalNoSeleccionadoException | PersonalNoEncontradoException e) {
            JOptionPane.showMessageDialog(vista, e.getMessage(), "Advertencia", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al eliminar personal: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    private void agregarListenerTabla() {
        DefaultTableModel tableModel = (DefaultTableModel) vista.getPersonalTable().getModel();
        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();

            if (e.getType() == TableModelEvent.UPDATE && column != -1) {
                String valor = (String) tableModel.getValueAt(row, column);
                Personal personal = modelo.getPersonal()[row];

                switch (column) {
                    case 0 -> {
                        String[] nombres = valor.split(" ");
                        personal.setNombre(nombres[0]);
                        if (nombres.length > 1) {
                            personal.setApellido(nombres[1]);
                        }
                    }
                    case 1 -> personal.setDNI(valor);
                    case 2 -> personal.setRol(valor);
                    case 3 -> personal.setUsuario(valor);
                    case 4 -> personal.setContraseña(valor);
                }
                modelo.actualizarPersonal(personal);
                modelo.guardarEnArchivo();
            }
        });
    }

    private void actualizarPersonal() {
        String dni = vista.getDNITextField().getText();
        Personal personal = modelo.buscarPersonalPorDNI(dni);
        if (personal != null) {
            personal.setNombre(vista.getNombreTextField().getText());
            personal.setApellido(vista.getApellidoTextField().getText());
            personal.setDireccion(vista.getDireccionTextField().getText());
            personal.setTelefono(vista.getTelefonoTextField().getText());
            modelo.actualizarPersonal(personal);
            JOptionPane.showMessageDialog(vista, "Personal actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(vista, "Personal no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        limpiarCampos();
        cargarTabla();
    }

    private void limpiarCampos() {
        vista.getNombreTextField().setText("");
        vista.getApellidoTextField().setText("");
        vista.getDireccionTextField().setText("");
        vista.getDNITextField().setText("");
        vista.getTelefonoTextField().setText("");
    }

}
