package cola;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
//Nodo utilizado en la implementación enlazada de la cola.
public class NodoCola {

    Object elemento;
    NodoCola siguiente;

    NodoCola(Object elemento) {
        this.elemento = elemento;
    }
}
