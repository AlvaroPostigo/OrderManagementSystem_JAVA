package cola;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
//Operaciones principales del TAD Cola (FIFO) para gestionar elementos en orden de llegada.
public interface ColaInterface {

    boolean estaVacia();

    void encolar(Object elemento);

    Object desencolar();

    Object frente();
}
