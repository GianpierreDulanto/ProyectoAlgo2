package Modelo;

public class Comida extends Consumible {

    private String tipoComida;

    public Comida(String nombreConsumible, double precio, String tipoComida) {
        super("Comida", nombreConsumible, precio);
        this.tipoComida = tipoComida;
    }

    public String getTipoComida() {
        return tipoComida;
    }
}
