package listaEnlazada;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
//Nodo utilizado en la lista doblemente enlazada circular.
public class NodoDoble {

    Object elemento;
    NodoDoble siguiente;
    NodoDoble anterior;

    NodoDoble(Object elemento) {
        this.elemento = elemento;
    }    
}
