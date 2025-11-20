package juego.logica;
/**
 * Enumeración que representa los cuatro palos de una baraja francesa.
 */
public enum Palo {
    CORAZONES("ROJO", "♥"),
    DIAMANTES("ROJO", "♦"),
    TREBOLES("NEGRO", "♣"),
    PICAS("NEGRO", "♠");

    private final String color;
    private final String simbolo;

    Palo(String color, String simbolo) {
        this.color = color;
        this.simbolo = simbolo;
    }

    public String getColor() {
        return color;
    }
    public String getSimbolo() {
        return simbolo;
    }
    public boolean esRojo() {
        return color.equals("ROJO");
    }
    public boolean esNegro() {
        return color.equals("NEGRO");
    }
}