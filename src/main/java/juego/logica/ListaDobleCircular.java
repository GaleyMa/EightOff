package juego.logica;
import java.util.Random;

public class ListaDobleCircular<T> {

    private class NodoDoble {
        T dato;
        NodoDoble siguiente;
        NodoDoble anterior;

        NodoDoble(T dato) {
            this.dato = dato;
            this.siguiente = null;
            this.anterior = null;
        }
    }

    private NodoDoble cabeza;
    private int tamaño;

    public ListaDobleCircular() {
        this.cabeza = null;
        this.tamaño = 0;
    }

    public void agregar(T elemento) {
        NodoDoble nuevoNodo = new NodoDoble(elemento);

        if (cabeza == null) {
            cabeza = nuevoNodo;
            nuevoNodo.siguiente = nuevoNodo;
            nuevoNodo.anterior = nuevoNodo;
        } else {
            NodoDoble ultimo = cabeza.anterior;

            ultimo.siguiente = nuevoNodo;
            nuevoNodo.anterior = ultimo;
            nuevoNodo.siguiente = cabeza;
            cabeza.anterior = nuevoNodo;
        }
        tamaño++;
    }

    public void agregarAlInicio(T elemento) {
        agregar(elemento);
        cabeza = cabeza.anterior; // Mover la cabeza al nuevo nodo
    }

    public boolean eliminar(T elemento) {
        if (cabeza == null) {
            return false;
        }

        NodoDoble actual = cabeza;
        do {
            if (actual.dato.equals(elemento)) {
                eliminarNodo(actual);
                return true;
            }
            actual = actual.siguiente;
        } while (actual != cabeza);

        return false;
    }


    public T eliminarEnIndice(int indice) {
        if (indice < 0 || indice >= tamaño) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }

        NodoDoble nodo = obtenerNodo(indice);
        T dato = nodo.dato;
        eliminarNodo(nodo);
        return dato;
    }

    private void eliminarNodo(NodoDoble nodo) {
        if (tamaño == 1) {
            // Único nodo en la lista
            cabeza = null;
        } else {
            nodo.anterior.siguiente = nodo.siguiente;
            nodo.siguiente.anterior = nodo.anterior;

            // Si eliminamos la cabeza, mover la cabeza al siguiente
            if (nodo == cabeza) {
                cabeza = nodo.siguiente;
            }
        }
        tamaño--;
    }

    public T obtener(int indice) {
        if (indice < 0 || indice >= tamaño) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        return obtenerNodo(indice).dato;
    }

    public T obtenerPrimero() {
        return cabeza == null ? null : cabeza.dato;
    }

    public T obtenerUltimo() {
        return cabeza == null ? null : cabeza.anterior.dato;
    }

    private NodoDoble obtenerNodo(int indice) {
        NodoDoble actual;

        if (indice < tamaño / 2) {
            actual = cabeza;
            for (int i = 0; i < indice; i++) {
                actual = actual.siguiente;
            }
        } else {
            actual = cabeza.anterior;
            for (int i = tamaño - 1; i > indice; i--) {
                actual = actual.anterior;
            }
        }

        return actual;
    }


    public boolean estaVacia() {
        return tamaño == 0;
    }


    public int getTamaño() {
        return tamaño;
    }

    public void limpiar() {
        cabeza = null;
        tamaño = 0;
    }

    public void barajar() {
        if (tamaño <= 1) {
            return;
        }

        Random random = new Random();

        // Convertir temporalmente a array para facilitar el barajado
        @SuppressWarnings("unchecked")
        T[] elementos = (T[]) new Object[tamaño];

        NodoDoble actual = cabeza;
        for (int i = 0; i < tamaño; i++) {
            elementos[i] = actual.dato;
            actual = actual.siguiente;
        }

        // Algoritmo Fisher-Yates
        for (int i = tamaño - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            // Intercambiar elementos[i] con elementos[j]
            T temp = elementos[i];
            elementos[i] = elementos[j];
            elementos[j] = temp;
        }

        // Reconstruir la lista circular con el orden barajado
        actual = cabeza;
        for (int i = 0; i < tamaño; i++) {
            actual.dato = elementos[i];
            actual = actual.siguiente;
        }
    }


    public void barajar(long seed) {
        if (tamaño <= 1) {
            return;
        }

        Random random = new Random(seed);

        @SuppressWarnings("unchecked")
        T[] elementos = (T[]) new Object[tamaño];

        NodoDoble actual = cabeza;
        for (int i = 0; i < tamaño; i++) {
            elementos[i] = actual.dato;
            actual = actual.siguiente;
        }

        for (int i = tamaño - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            T temp = elementos[i];
            elementos[i] = elementos[j];
            elementos[j] = temp;
        }

        actual = cabeza;
        for (int i = 0; i < tamaño; i++) {
            actual.dato = elementos[i];
            actual = actual.siguiente;
        }
    }


    public void rotarAdelante() {
        if (cabeza != null) {
            cabeza = cabeza.siguiente;
        }
    }


    public void rotarAtras() {
        if (cabeza != null) {
            cabeza = cabeza.anterior;
        }
    }

    public T[] toArray() {
        if (estaVacia()) {
            return (T[]) new Object[0];
        }

        T[] array = (T[]) new Object[tamaño];
        NodoDoble actual = cabeza;
        for (int i = 0; i < tamaño; i++) {
            array[i] = actual.dato;
            actual = actual.siguiente;
        }
        return array;
    }

    @Override
    public String toString() {
        if (estaVacia()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        NodoDoble actual = cabeza;
        for (int i = 0; i < tamaño; i++) {
            sb.append(actual.dato);
            if (i < tamaño - 1) {
                sb.append(", ");
            }
            actual = actual.siguiente;
        }
        sb.append("]");
        return sb.toString();
    }
}