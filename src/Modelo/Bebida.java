package Modelo;

public class Bebida extends Consumible {

    private String tipoBebida;

    public Bebida(String nombreConsumible, double precio, String tipoBebida) {
        super("Bebida", nombreConsumible, precio);
        this.tipoBebida = tipoBebida;
    }

    public String getTipoBebida() {
        return tipoBebida;
    }
}
