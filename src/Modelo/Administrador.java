package Modelo;

/*
 * @author estefano
 */
public class Administrador extends Personal {

    public Administrador(String nom, String ape, String direccion, String dni, String telefono) {
        super(nom, ape, direccion, dni, telefono, "Administrador");
    }

    public Administrador(String dni, String contraseña) {
        super(dni, contraseña);
    }

    public void agregarPersonal(Personal personal) {
        PersonalArreglo arregloP = new PersonalArreglo();
        arregloP.agregar(personal);
    }

    public void eliminarPersonal(int id) {
        PersonalArreglo arregloP = new PersonalArreglo();
        arregloP.eliminar(id);
    }
}
