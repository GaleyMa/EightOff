package juego.logica;

public class Carta implements Comparable<Carta> {

    private final Palo palo;
    private final Rango rango;
    private boolean bocaArriba;


    public Carta(Palo palo, Rango rango) {
        if (palo == null || rango == null) {
            throw new IllegalArgumentException("Palo y rango no pueden ser null");
        }
        this.palo = palo;
        this.rango = rango;
        this.bocaArriba = false;
    }


    public Palo getPalo() {
        return palo;
    }

    public Rango getRango() {
        return rango;
    }

    public int getValor() {
        return rango.getValor();
    }

    public boolean isBocaArriba() {
        return bocaArriba;
    }

    public void voltear() {
        bocaArriba = !bocaArriba;
    }

    public void ponerBocaArriba() {
        bocaArriba = true;
    }


    public void ponerBocaAbajo() {
        bocaArriba = false;
    }

    @Override
    public int compareTo(Carta otra) {
        // Primero comparar por rango
        int comparacionRango = Integer.compare(this.getValor(), otra.getValor());
        if (comparacionRango != 0) {
            return comparacionRango;
        }
        // Si los rangos son iguales, comparar por palo
        return this.palo.compareTo(otra.palo);
    }


    public boolean esConsecutiva(Carta otra) {
        return this.getValor() == otra.getValor() - 1;
    }


    public boolean puedePonerseSobre(Carta sobre) {
        if (sobre == null) {
            return true;
        }
        return this.esConsecutiva(sobre);
    }

    public boolean puedeIrEnFundacion(Carta superior) {
        if (superior == null) {
            return this.rango == Rango.AS;
        }

        return this.palo == superior.palo &&
                this.getValor() == superior.getValor() + 1;
    }

    public boolean mismoColor(Carta otra) {
        return this.palo.getColor().equals(otra.palo.getColor());
    }

    public boolean mismoPalo(Carta otra) {
        return this.palo == otra.palo;
    }

    @Override
    public String toString() {
        return rango.getSimbolo() + palo.getSimbolo();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Carta otra = (Carta) obj;
        return palo == otra.palo && rango == otra.rango;
    }

    @Override
    public int hashCode() {
        return 31 * palo.hashCode() + rango.hashCode();
    }
}
