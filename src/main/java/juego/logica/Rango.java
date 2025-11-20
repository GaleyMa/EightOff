package juego.logica;

public enum Rango {
    AS("a", 1),
    DOS("2", 2),
    TRES("3", 3),
    CUATRO("4", 4),
    CINCO("5", 5),
    SEIS("6", 6),
    SIETE("7", 7),
    OCHO("8", 8),
    NUEVE("9", 9),
    DIEZ("10", 10),
    JOTA("j", 11),
    REINA("q", 12),
    REY("k", 13);  // Asegúrate de que REY esté definido

    private final String simbolo;
    private final int valor;

    Rango(String simbolo, int valor) {
        this.simbolo = simbolo;
        this.valor = valor;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public int getValor() {
        return valor;
    }
}