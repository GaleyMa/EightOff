package juego.logica;

/**
 * Representa un movimiento en el juego Eight Off.
 */
public class Movimiento {
    private Carta carta;
    private TipoZona origen;
    private TipoZona destino;
    private int indiceOrigen;
    private int indiceDestino;
    private int secuenciaMovida;

    public Movimiento(Carta carta, TipoZona origen, TipoZona destino,
                      int indiceOrigen, int indiceDestino) {
        this.carta = carta;
        this.origen = origen;
        this.destino = destino;
        this.indiceOrigen = indiceOrigen;
        this.indiceDestino = indiceDestino;
        this.secuenciaMovida = 1;
    }

    public Carta getCarta() { return carta; }
    public TipoZona getOrigen() { return origen; }
    public TipoZona getDestino() { return destino; }
    public int getIndiceOrigen() { return indiceOrigen; }
    public int getIndiceDestino() { return indiceDestino; }
    public int getSecuenciaMovida() { return secuenciaMovida; }


    public void setSecuenciaMovida(int secuenciaMovida) {
        this.secuenciaMovida = secuenciaMovida;
    }

    @Override
    public String toString() {
        return String.format("Movimiento: %s de %s[%d] a %s[%d] (%d cartas)",
                carta, origen, indiceOrigen, destino, indiceDestino, secuenciaMovida);
    }
}