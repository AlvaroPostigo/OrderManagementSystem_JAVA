package listaEnlazada;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
//peraciones principales del TAD Lista Doblemente Enlazada Circular.
public interface ListaDobleInterface {

    boolean estaVacia();

    void insertarInicio(Object elemento);

    void insertarFinal(Object elemento);

    void insertarEn(Object elemento, int posicion);

    Object retirarInicio();

    Object retirarFinal();

    Object retirarEn(int posicion);

    Object buscarPorId(String id);

    String imprimirAdelante();

    String imprimirAtras();

    int getLongitud();
}
