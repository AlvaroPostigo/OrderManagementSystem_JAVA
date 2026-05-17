package arbol;

import modelo.Pedido;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
/*
Nodo del árbol BST que almacena un pedido y referencias a sus hijos.
Se utiliza en las operaciones de inserción y recorrido del árbol.
 */
public class NodoArbol {

    public Pedido pedido;
    public NodoArbol izquierdo;
    public NodoArbol derecho;

    NodoArbol(Pedido pedido) {
        this.pedido = pedido;
    }
}
