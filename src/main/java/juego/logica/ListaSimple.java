package juego.logica;

public class ListaSimple<T> {

    private class Nodo {
        T dato;
        Nodo siguiente;

        Nodo(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    private Nodo cabeza;
    private Nodo cola;  // Nueva referencia al último nodo
    private int tamaño;


    public ListaSimple() {
        this.cabeza = null;
        this.cola = null;
        this.tamaño = 0;
    }


    public void agregar(T elemento) {
        Nodo nuevoNodo = new Nodo(elemento);

        if (cabeza == null) {
            cabeza = nuevoNodo;
            cola = nuevoNodo;
        } else {
            cola.siguiente = nuevoNodo;
            cola = nuevoNodo;
        }
        tamaño++;
    }


    public void agregarAlInicio(T elemento) {
        Nodo nuevoNodo = new Nodo(elemento);
        nuevoNodo.siguiente = cabeza;
        cabeza = nuevoNodo;

        if (cola == null) {
            cola = nuevoNodo;
        }
        tamaño++;
    }


    public T eliminar(int indice) {
        if (indice < 0 || indice >= tamaño) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }

        T dato;
        if (indice == 0) {
            dato = cabeza.dato;
            cabeza = cabeza.siguiente;
            if (cabeza == null) {
                cola = null;
            }
        } else {
            Nodo anterior = obtenerNodo(indice - 1);
            dato = anterior.siguiente.dato;
            anterior.siguiente = anterior.siguiente.siguiente;

            // Actualizar cola si eliminamos el último elemento
            if (anterior.siguiente == null) {
                cola = anterior;
            }
        }
        tamaño--;
        return dato;
    }

    public T eliminarUltimo() {
        if (estaVacia()) {
            throw new IllegalStateException("La lista está vacía");
        }
        return eliminar(tamaño - 1);
    }

    public T eliminarPrimero() {
        if (estaVacia()) {
            throw new IllegalStateException("La lista está vacía");
        }
        return eliminar(0);
    }


    public T obtener(int indice) {
        if (indice < 0 || indice >= tamaño) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        return obtenerNodo(indice).dato;
    }


    public T obtenerUltimo() {
        return cola == null ? null : cola.dato;
    }

    public T obtenerPrimero() {
        return cabeza == null ? null : cabeza.dato;
    }

    public boolean contiene(T elemento) {
        Nodo actual = cabeza;
        while (actual != null) {
            if (actual.dato.equals(elemento)) {
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    public boolean estaVacia() {
        return tamaño == 0;
    }

    public int getTamaño() {
        return tamaño;
    }

    public void limpiar() {
        cabeza = null;
        cola = null;
        tamaño = 0;
    }

    private Nodo obtenerNodo(int indice) {
        Nodo actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.siguiente;
        }
        return actual;
    }

    public T[] toArray() {
        T[] array = (T[]) new Object[tamaño];
        Nodo actual = cabeza;
        int i = 0;
        while (actual != null) {
            array[i++] = actual.dato;
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
        Nodo actual = cabeza;
        while (actual != null) {
            sb.append(actual.dato);
            if (actual.siguiente != null) {
                sb.append(", ");
            }
            actual = actual.siguiente;
        }
        sb.append("]");
        return sb.toString();
    }
}