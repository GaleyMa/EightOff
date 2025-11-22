package juego.logica;

public enum TipoZona {

    CELDA_LIBRE("Celda Libre"),
    TABLEAU("Tableau"),
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
