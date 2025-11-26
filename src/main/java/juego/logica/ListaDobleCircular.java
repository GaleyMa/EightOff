
package juego.logica;


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
    private NodoDoble cursor;
    private int tamaño;

    public ListaDobleCircular() {
        this.cabeza = null;
        this.cursor = null;
        this.tamaño = 0;
    }

    public void agregar(T elemento) {
        NodoDoble nuevoNodo = new NodoDoble(elemento);

        if (cabeza == null) {
            cabeza = nuevoNodo;
            nuevoNodo.siguiente = nuevoNodo;
            nuevoNodo.anterior = nuevoNodo;
            cursor = nuevoNodo;
        } else {
            NodoDoble ultimo = cabeza.anterior;

            ultimo.siguiente = nuevoNodo;
            nuevoNodo.anterior = ultimo;
            nuevoNodo.siguiente = cabeza;
            cabeza.anterior = nuevoNodo;

            cursor = nuevoNodo;
        }
        tamaño++;
    }


    public boolean puedeDeshacer() {

        return cursor != null && cursor.anterior != cursor;
    }

    public boolean puedeRehacer() {
        return cursor != null && cursor.siguiente != cursor;
    }

    public T deshacer() {
        if (!puedeDeshacer()) return null;

        cursor = cursor.anterior;
        return cursor.dato;
    }

    public T rehacer() {
        if (!puedeRehacer()) return null;

        cursor = cursor.siguiente;
        return cursor.dato;
    }

    public T irPrimero() {
        if (cabeza == null) return null;
        // Ir al primer elemento (más antiguo)
        cursor = cabeza;
        return cursor.dato;
    }

    public T irUltimo() {
        if (cabeza == null) return null;
        // Ir al último elemento (más reciente)
        cursor = cabeza.anterior;
        return cursor.dato;
    }

    public int getPosicionActual() {
        if (cursor == null) return 0;

        // Contar desde el inicio hasta el cursor
        int posicion = 1;
        NodoDoble actual = cabeza;

        while (actual != cursor) {
            posicion++;
            actual = actual.siguiente;
            // Protección contra ciclos infinitos
            if (posicion > tamaño) {
                break;
            }
        }

        return posicion;
    }

    public int getTamaño() {
        return tamaño;
    }

    public void truncarDesdeActual() {
        if (cursor == null) return;

        cabeza = cursor;
        cabeza.anterior = cursor;
        cabeza.siguiente = cursor;

        tamaño = 1;
        NodoDoble actual = cabeza.siguiente;
        while (actual != cabeza && tamaño < 100) {
            tamaño++;
            actual = actual.siguiente;
        }
    }


    public void agregarAlInicio(T elemento) {
        agregar(elemento);
        cabeza = cabeza.anterior;
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
            cabeza = null;
            cursor = null;
        } else {
            nodo.anterior.siguiente = nodo.siguiente;
            nodo.siguiente.anterior = nodo.anterior;

            if (nodo == cabeza) {
                cabeza = nodo.siguiente;
            }
            if (nodo == cursor) {
                cursor = nodo.anterior;
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

    public void limpiar() {
        cabeza = null;
        cursor = null;
        tamaño = 0;
    }


    @Override
    public String toString() {
        if (estaVacia()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        NodoDoble actual = cabeza;
        for (int i = 0; i < tamaño; i++) {
            if (actual == cursor) {
                sb.append("[").append(actual.dato).append("]");
            } else {
                sb.append(actual.dato);
            }
            if (i < tamaño - 1) {
                sb.append(", ");
            }
            actual = actual.siguiente;
        }
        sb.append("]");
        return sb.toString();
    }
}