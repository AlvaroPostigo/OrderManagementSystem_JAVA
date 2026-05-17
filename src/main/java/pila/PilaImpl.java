package pila;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
/*
Implementación enlazada del TAD Pila, su uso principal en la aplicación es
gestionar el historial de acciones para permitir la operación de "deshacer".
 */
public class PilaImpl implements PilaInterface {

    // Referencia al nodo ubicado en la cima de la pila.
    private NodoPila cima;

    // Indica si la pila no contiene elementos.
    @Override
    public boolean estaVacia() {
        return cima == null;
    }

    //Inserta un nuevo elemento en la cima(head) de la pila.
    @Override
    public void push(Object elemento) {
        NodoPila nuevo = new NodoPila(elemento);
        nuevo.siguiente = cima;
        cima = nuevo;
    }

    //Elimina y devuelve el elemento ubicado en la cima(head) y retorna el elemento retirado 
    @Override
    public Object pop() {
        if (estaVacia()) {
            return null;
        }
        Object e = cima.elemento;
        cima = cima.siguiente;
        return e;
    }

    //Devuelve el elemento en la cima(head) sin eliminarlo.
    @Override
    public Object cima() {
        if (estaVacia()) {
            return null;
        }
        return cima.elemento;
    }
}
