package pila;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
//Nodo utilizado en la implementación enlazada de la pila.
public class NodoPila {

    Object elemento;
    NodoPila siguiente;

    NodoPila(Object elemento) {
        this.elemento = elemento;
    }
}
