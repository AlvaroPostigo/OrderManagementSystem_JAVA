package arbol;

import modelo.Pedido;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
//Operaciones principales del TAD Árbol BST para pedidos.
public interface ArbolInterface {

    void insertar(Pedido pedido);

    Pedido buscarPorId(String id);

    String recorridoInOrden();
}
