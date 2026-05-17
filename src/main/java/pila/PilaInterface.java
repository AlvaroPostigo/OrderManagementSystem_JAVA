package pila;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
//Operaciones principales del TAD Pila
public interface PilaInterface {

    boolean estaVacia();

    void push(Object elemento);

    Object pop();

    Object cima();
}
