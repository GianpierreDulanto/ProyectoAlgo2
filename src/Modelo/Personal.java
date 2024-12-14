package Modelo;

/**
 *
 * @author estefano
 */
import java.util.Random;

public class Personal {

    private static int ultimoId = 0;
    private int id;
    private String nombre;
    private String apellido;
    private String DNI;
    private String telefono;
    private String direccion;
    private String usuario;
    private String contraseña;
    private String rol;

    public Personal(String nom, String ape, String direccion, String dni, String telefono, String rol) {
        this.nombre = nom;
        this.apellido = ape;
        this.direccion = direccion;
        this.DNI = dni;
        this.telefono = telefono;
        this.id = generarId();
        this.usuario = generarUser();
        this.contraseña = generarContrasenia();
        this.rol = rol;
    }

    public Personal(String nom, String ape, String direccion, String dni, String telefono) {
        this.nombre = nom;
        this.apellido = ape;
        this.direccion = direccion;
        this.DNI = dni;
        this.telefono = telefono;
        this.id = generarId();
        this.usuario = generarUser();
        this.contraseña = generarContrasenia();
    }

    public Personal(String dni, String contraseña) {
        this.DNI = dni;
        this.contraseña = contraseña;
    }

    private String generarUser() {
        String nombreCorto = nombre.length() >= 3 ? nombre.substring(0, 3).toLowerCase() : nombre.toLowerCase();
        return nombreCorto + apellido.toLowerCase();
    }

    private String generarContrasenia() {
        Random random = new Random();
        int numeroAleatorio = random.nextInt(100000);
        return usuario + "+" + String.format("%04d", numeroAleatorio);
    }

    public void agregarCliente(Cliente cliente) {
        ClienteArreglo arregloC = new ClienteArreglo();
        arregloC.agregar(cliente);
    }

    public void eliminarCliente(String dni) {
        ClienteArreglo arregloC = new ClienteArreglo();
        arregloC.eliminar(dni);
    }

    public Personal buscarPersonal(int id) {
        PersonalArreglo arregloP = new PersonalArreglo();
        return arregloP.buscarPersonal(id);
    }

    public void actualizarPersonal(Personal personal) {
        PersonalArreglo arregloP = new PersonalArreglo();
        arregloP.actualizarPersonal(personal);
    }

    private int generarId() {
        ultimoId++;
        return ultimoId;
    }

    public static void setUltimoId(int id) {
        ultimoId = id;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDNI() {
        return DNI;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setDNI(String DNI) {
        this.DNI = DNI;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getRol() {
        return rol;
    }

    @Override
    public String toString() {
        return rol + "," + id + "," + usuario + "," + contraseña + "," + nombre + "," + apellido + "," + DNI + "," + telefono + "," + direccion;
    }
}
