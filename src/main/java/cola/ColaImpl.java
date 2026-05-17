package cola;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
/*
Implementación enlazada del TAD Cola, esta cola se utiliza para gestionar las
tareas asociadas a los pedidos (preparación y envío). Cada vez que se crea un
pedido o cambia su estado, se genera una tarea en esta cola, que luego será
procesada en orden estricto.
 */
public class ColaImpl implements ColaInterface {

    //Referencia al primer nodo de la cola.
    private NodoCola frente;

    //Referencia al último nodo de la cola.
    private NodoCola fin;

    /*
    Convierte todos los elementos de la cola en un arreglo.
    Este método recorre la lista enlazada y copia cada elemento en un arreglo
    de Object, facilitar la visualización de la cola en la interfaz gráfica.
     */
    public Object[] aArreglo() {
        // Primero se cuenta cuántos nodos hay en la cola.
        int n = 0;
        NodoCola actual = frente;
        while (actual != null) {
            n++;
            actual = actual.siguiente;
        }

        // Se crea el arreglo y se copian los elementos.
        Object[] arr = new Object[n];
        actual = frente;
        int i = 0;

        while (actual != null) {
            arr[i++] = actual.elemento;
            actual = actual.siguiente;
        }
        return arr;
    }

    //Verifica si la cola se encuentra vacía.
    @Override
    public boolean estaVacia() {
        return frente == null;
    }

    //Inserta un nuevo elemento al final de la cola.
    @Override
    public void encolar(Object elemento) {
        NodoCola nuevo = new NodoCola(elemento);

        if (estaVacia()) {
            frente = fin = nuevo;
        } else {
            fin.siguiente = nuevo;
            fin = nuevo;
        }
    }

    //Elimina y devuelve el elemento ubicado en el frente de la cola.
    @Override
    public Object desencolar() {
        if (estaVacia()) {
            return null;
        }

        Object e = frente.elemento;
        frente = frente.siguiente;

        // Si después de desencolar no queda ningún nodo, la cola queda vacía.
        if (frente == null) {
            fin = null;
        }

        return e;
    }

    //Devuelve el elemento del frente(head) sin retirarlo de la cola.
    @Override
    public Object frente() {
        if (estaVacia()) {
            return null;
        }
        return frente.elemento;
    }
}
