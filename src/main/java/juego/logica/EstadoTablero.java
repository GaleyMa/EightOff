package juego.logica;

import java.io.Serializable;

public class EstadoTablero implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String[] celdasLibres;
    private final String[][] columnas;
    private final String[][] fundaciones;
    private final int numeroMovimientos;

    public EstadoTablero(EightOffJuego juego) {
        this.celdasLibres = serializarCeldasLibres(juego);
        this.columnas = serializarColumnas(juego);
        this.fundaciones = serializarFundaciones(juego);
        this.numeroMovimientos = juego.getNumeroMovimientos();
    }

    private String[] serializarCeldasLibres(EightOffJuego juego) {
        String[] celdas = new String[EightOffJuego.Config.NUM_CELDAS_LIBRES];
        Carta[] celdasLogicas = juego.getCeldasLibres();
        for (int i = 0; i < celdasLogicas.length; i++) {
            celdas[i] = (celdasLogicas[i] != null) ?
                    celdasLogicas[i].getRango().getSimbolo() + celdasLogicas[i].getPalo().name().charAt(0) :
                    null;
        }
        return celdas;
    }

    private String[][] serializarColumnas(EightOffJuego juego) {
        String[][] columnasSerializadas = new String[EightOffJuego.Config.NUM_COLUMNAS][];
        for (int i = 0; i < EightOffJuego.Config.NUM_COLUMNAS; i++) {
            ListaSimple<Carta> columna = juego.getColumna(i);
            columnasSerializadas[i] = new String[columna.getTamaño()];
            for (int j = 0; j < columna.getTamaño(); j++) {
                Carta carta = columna.obtener(j);
                columnasSerializadas[i][j] = carta.getRango().getSimbolo() + carta.getPalo().name().charAt(0);
            }
        }
        return columnasSerializadas;
    }

    private String[][] serializarFundaciones(EightOffJuego juego) {
        String[][] fundacionesSerializadas = new String[EightOffJuego.Config.NUM_FUNDACIONES][];
        for (int i = 0; i < EightOffJuego.Config.NUM_FUNDACIONES; i++) {
            ListaSimple<Carta> fundacion = juego.getFundacion(i);
            fundacionesSerializadas[i] = new String[fundacion.getTamaño()];
            for (int j = 0; j < fundacion.getTamaño(); j++) {
                Carta carta = fundacion.obtener(j);
                fundacionesSerializadas[i][j] = carta.getRango().getSimbolo() + carta.getPalo().name().charAt(0);
            }
        }
        return fundacionesSerializadas;
    }

    // Getters
    public String[] getCeldasLibres() { return celdasLibres; }
    public String[][] getColumnas() { return columnas; }
    public String[][] getFundaciones() { return fundaciones; }
    public int getNumeroMovimientos() { return numeroMovimientos; }
}