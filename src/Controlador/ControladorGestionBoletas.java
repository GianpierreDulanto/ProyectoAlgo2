package Controlador;

/**
 *
 * @author USER
 */
import Modelo.HabitacionArreglo;
import Modelo.PersonalArreglo;
import Modelo.ArregloConsumibles;
import Modelo.ArregloPedido;
import Modelo.ArregloReserva;
import javax.swing.*;
import java.io.*;
import Vista.*;
import java.nio.file.*;

public class ControladorGestionBoletas {
    private final GestionBoletas vista; // Referencia a la vista

    public ControladorGestionBoletas(GestionBoletas vista) {
        this.vista = vista;

        // Asignar acción al botón "BuscarBoletas"
        this.vista.getBuscarBoletas().addActionListener(e -> seleccionarYMostrarBoleta());

        // Asignar acción al botón "Volver"
        this.vista.getVolver().addActionListener(e -> volverAlMenuRecepcion());
    }

    // Método para seleccionar el archivo de boleta
    private void seleccionarYMostrarBoleta() {
        try {
            // Definir la carpeta de destino
            String carpetaBase = System.getProperty("user.dir"); // Carpeta donde se ejecuta el proyecto
            String carpetaDestino = carpetaBase + File.separator + "Boletas"; // Subcarpeta 'Boletas'
            Files.createDirectories(Paths.get(carpetaDestino)); // Crear la carpeta si no existe

            // Configurar el JFileChooser para que abra en la carpeta destino
            JFileChooser fileChooser = new JFileChooser(carpetaDestino);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setDialogTitle("Seleccionar Boleta");

            int seleccion = fileChooser.showOpenDialog(null); // Mostrar cuadro de diálogo
            if (seleccion == JFileChooser.APPROVE_OPTION) {
                File archivoSeleccionado = fileChooser.getSelectedFile();
                mostrarBoleta(archivoSeleccionado);
            } else {
                JOptionPane.showMessageDialog(null, "No se seleccionó ninguna boleta.");
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(vista, "Error al acceder a la carpeta de boletas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    // Método para mostrar el contenido de la boleta en el JTextArea
    private void mostrarBoleta(File archivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            StringBuilder contenidoBoleta = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                contenidoBoleta.append(linea).append("\n");
            }

            // Mostrar el contenido en el JTextArea existente
            JTextArea areaTexto = vista.getBoletas();
            areaTexto.setText(contenidoBoleta.toString());
            areaTexto.setCaretPosition(0); // Volver al inicio del texto

            JOptionPane.showMessageDialog(null, "Boleta cargada con éxito.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error al leer la boleta: " + ex.getMessage());
        }
    }

    // Método para volver al menú de recepciones
    private void volverAlMenuRecepcion() {
        try {
            MenuRecep menuRecep = new MenuRecep(); // Crear instancia del menú de recepciones
            new ControladorRecep(menuRecep, new PersonalArreglo(), "Usuario", new HabitacionArreglo(), new ArregloPedido(), new ArregloConsumibles(), new ArregloReserva());
            menuRecep.setLocationRelativeTo(null); // Centrar la ventana
            menuRecep.setVisible(true); // Mostrar la ventana
            vista.dispose(); // Cerrar la vista actual
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al regresar al menú: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
