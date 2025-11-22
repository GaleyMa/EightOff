package juego.logica;

public class SnapshotMovimiento {
    private final EstadoTablero estadoTablero;
    private final ListaSimple<String> cartasInvolucradas; // Usar ListaSimple
    private final String descripcion;


    public SnapshotMovimiento(EightOffJuego juego, String descripcion, ListaSimple<Carta> cartas) {
        this.estadoTablero = new EstadoTablero(juego);
        this.descripcion = descripcion;
        this.cartasInvolucradas = new ListaSimple<>();

        // Serializar cartas involucradas para resaltado
        if (cartas != null) {
            for (int i = 0; i < cartas.getTamaño(); i++) {
                Carta carta = cartas.obtener(i);
                cartasInvolucradas.agregar(carta.getRango().getSimbolo() + carta.getPalo().name().charAt(0));
            }
        }
    }

    // Getters
    public EstadoTablero getEstadoTablero() { return estadoTablero; }
    public ListaSimple<String> getCartasInvolucradas() { return cartasInvolucradas; } // Cambiado a ListaSimple
    public String getDescripcion() { return descripcion; }
}