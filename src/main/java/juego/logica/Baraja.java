package juego.logica;

public class Baraja {

    private ListaDobleCircular<Carta> cartas;


    public Baraja() {
        cartas = new ListaDobleCircular<>();
        crearBarajaCompleta();
    }

    private void crearBarajaCompleta() {
        for (Palo palo : Palo.values()) {
            for (Rango rango : Rango.values()) {
                Carta carta = new Carta(palo, rango);
                cartas.agregar(carta);
            }
        }
    }

    public void barajar() {
        cartas.barajar();
    }


    public void barajar(long seed) {
        cartas.barajar(seed);
    }

    public Carta repartir() {
        if (estaVacia()) {
            throw new IllegalStateException("No quedan cartas en la baraja");
        }
        return cartas.eliminarEnIndice(0);
    }


    public boolean estaVacia() {
        return cartas.estaVacia();
    }


    public int cartasRestantes() {
        return cartas.getTamaño();
    }

    public void reiniciar() {
        cartas.limpiar();
        crearBarajaCompleta();
    }

    public Carta[] verCartas() {
        return cartas.toArray();
    }

    @Override
    public String toString() {
        return "Baraja{" + cartasRestantes() + " cartas restantes}";
    }
}