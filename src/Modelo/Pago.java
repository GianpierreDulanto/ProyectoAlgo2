package Modelo;

import java.util.Date;

/**
 *
 * @author estef
 */
public class Pago {

    int contador;
    private int idPago;
    private int monto;
    private Date fecha;
    private Reserva reserva;
    private boolean pagado;

    public Pago(int monto, Date fecha, Reserva reserva, boolean pagado) {
        this.idPago = generarId();
        this.monto = monto;
        this.fecha = fecha;
        this.reserva = reserva;
        this.pagado = pagado;
    }

    public boolean procesarPago(int Dinero) {

        return (Dinero >= monto) ? this.pagado = true : false;
    }

    public boolean cancelarPago() {
        return this.pagado = false;
    }

    public String obtenerRecibo(Cliente cliente, Pago pago) {
        return "Datos del Cliente: "
                + "DNI: " + cliente.getDni() + " Nombre: "
                + " Datos del pago: " + "ID:" + pago.getIdPago() + " Monto: " + pago.getMonto() + " Fecha" + String.valueOf(pago.getFecha());

    }

    private int generarId() {
        contador++;
        return contador;

    }

    public int getIdPago() {
        return idPago;
    }

    public int getMonto() {
        return monto;
    }

    public Date getFecha() {
        return fecha;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setMonto(int monto) {
        this.monto = monto;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

}
