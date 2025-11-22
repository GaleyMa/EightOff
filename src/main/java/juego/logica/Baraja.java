package juego.logica;

import java.util.Random;

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
        if (cartas.estaVacia() || cartas.getTamaño() == 1) {
            return;
        }

        // Crear un array temporal con las cartas
        Carta[] cartasArray = new Carta[cartas.getTamaño()];

        // Extraer todas las cartas de la lista
        for (int i = 0; i < cartasArray.length; i++) {
            cartasArray[i] = cartas.eliminarEnIndice(0);
        }

        // Barajar usando el algoritmo Fisher-Yates
        Random random = new Random();
        for (int i = cartasArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // Intercambiar cartas[i] con cartas[j]
            Carta temp = cartasArray[i];
            cartasArray[i] = cartasArray[j];
            cartasArray[j] = temp;
        }

        // Volver a agregar las cartas barajadas a la lista
        for (Carta carta : cartasArray) {
            cartas.agregar(carta);
        }
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

    @Override
    public String toString() {
        return "Baraja{" + cartasRestantes() + " cartas restantes}";
    }
}