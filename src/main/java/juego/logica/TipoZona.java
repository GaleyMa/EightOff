package juego.logica;

public enum TipoZona {
    /**
     * Celdas libres: 8 espacios temporales para almacenar cartas
     */
    CELDA_LIBRE("Celda Libre"),

    /**
     * Tableau: 8 columnas de juego donde se colocan las cartas
     */
    TABLEAU("Tableau"),

    /**
     * Fundación: 4 pilas donde se apilan las cartas por palo en orden ascendente
     */
    FUNDACION("Fundación");

    private final String nombre;

    TipoZona(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
