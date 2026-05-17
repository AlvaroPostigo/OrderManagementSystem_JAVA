package arbol;

import modelo.Pedido;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
/*
 Implementación del TAD Árbol Binario de Búsqueda (BST) para objetos Pedido
 Este árbol mantiene los pedidos ordenados por su identificador (id) y ofrece
 operaciones para: Insertar pedidos, Buscar un pedido por su id, Recorrer el árbol en orden (inorden) para listar los pedidos
 ordenados, Listar pedidos filtrados por cliente o por estado y Eliminar un pedido por id. Esta estructura se utiliza como contenedor
 principal para gestionar los pedidos de la tienda.
 */
public class ArbolBSTImpl implements ArbolInterface {

    /**
     * Referencia al nodo raíz del árbol. Puede ser null si el árbol está vacío.
     */
    private NodoArbol raiz;

    /*
    Inserta un nuevo pedido en el árbol, si ya existe un nodo con el mismo id, se actualiza el pedido almacenado
    en ese nodo.
     */
    @Override
    public void insertar(Pedido pedido) {
        raiz = insertarRec(raiz, pedido);
    }

    /*
    Inserta recursivamente un pedido a partir de un nodo dado.
    Se compara el id del pedido con el id almacenado en el nodo actual para
    decidir si se debe ir al subárbol izquierdo, derecho o actualizar el
    nodo.
     */
    private NodoArbol insertarRec(NodoArbol nodo, Pedido pedido) {
        // Si el nodo es null, se crea un nuevo nodo con el pedido.
        if (nodo == null) {
            return new NodoArbol(pedido);
        }

        // Comparación de ids para mantener la propiedad de BST.
        int cmp = pedido.getId().compareTo(nodo.pedido.getId());
        if (cmp < 0) {
            nodo.izquierdo = insertarRec(nodo.izquierdo, pedido);
        } else if (cmp > 0) {
            nodo.derecho = insertarRec(nodo.derecho, pedido);
        } else {
            // Si el id es igual, se actualiza el pedido del nodo.
            nodo.pedido = pedido;
        }
        return nodo;
    }

    /*
    Busca un pedido en el árbol a partir de su id.
     */
    @Override
    public Pedido buscarPorId(String id) {
        return buscarRec(raiz, id);
    }

    /*
    Búsqueda recursiva de un pedido a partir de un nodo y un id dado.
     */
    private Pedido buscarRec(NodoArbol nodo, String id) {
        if (nodo == null || id == null) {
            return null;
        }

        int cmp = id.compareTo(nodo.pedido.getId());
        if (cmp == 0) {
            // Coincidencia exacta de id.
            return nodo.pedido;
        } else if (cmp < 0) {
            // El id buscado es menor: ir al subárbol izquierdo.
            return buscarRec(nodo.izquierdo, id);
        } else {
            // El id buscado es mayor: ir al subárbol derecho.
            return buscarRec(nodo.derecho, id);
        }
    }

    /*
    Realiza un recorrido inorden del árbol y construye una representación en texto de todos los pedidos.
    El recorrido inorden devuelve los pedidos ordenados según su id.
     */
    @Override
    public String recorridoInOrden() {
        StringBuilder sb = new StringBuilder();
        recorridoInOrdenRec(raiz, sb);
        return sb.toString();
    }

    /*
    Método recursivo auxiliar para el recorrido inorden.
     */
    private void recorridoInOrdenRec(NodoArbol nodo, StringBuilder sb) {
        if (nodo == null) {
            return;
        }

        // Primero se recorre el subárbol izquierdo.
        recorridoInOrdenRec(nodo.izquierdo, sb);

        // Luego se procesa el nodo actual.
        Pedido p = nodo.pedido;
        sb.append("Pedido ")
                .append(p.getId()).append(" - ")
                .append(p.getCliente()).append(" - ")
                .append(p.getFecha()).append(" - ")
                .append(p.getEstado()).append("\n");

        // Finalmente se recorre el subárbol derecho.
        recorridoInOrdenRec(nodo.derecho, sb);
    }

    /*
    Genera una lista en texto de todos los pedidos cuyo cliente coincide con
    el nombre especificado.
     */
    public String listarPorCliente(String cliente) {
        StringBuilder sb = new StringBuilder();
        listarPorClienteRec(raiz, cliente, sb);
        return sb.toString();
    }

    /*
    Recorrido recursivo para listar los pedidos de un cliente concreto.
     */
    private void listarPorClienteRec(NodoArbol nodo, String cliente, StringBuilder sb) {
        if (nodo == null) {
            return;
        }

        listarPorClienteRec(nodo.izquierdo, cliente, sb);

        if (nodo.pedido.getCliente().equalsIgnoreCase(cliente)) {
            Pedido p = nodo.pedido;
            sb.append("Pedido ")
                    .append(p.getId()).append(" - ")
                    .append(p.getCliente()).append(" - ")
                    .append(p.getFecha()).append(" - ")
                    .append(p.getEstado()).append("\n");
        }

        listarPorClienteRec(nodo.derecho, cliente, sb);
    }

    /*
    Genera una lista en texto de todos los pedidos que se encuentran en un estado determinado.
     */
    public String listarPorEstado(String estado) {
        StringBuilder sb = new StringBuilder();
        listarPorEstadoRec(raiz, estado, sb);
        return sb.toString();
    }

    /*
    Recorrido recursivo para listar los pedidos que tienen un estado específico.
     */
    private void listarPorEstadoRec(NodoArbol nodo, String estado, StringBuilder sb) {
        if (nodo == null) {
            return;
        }

        listarPorEstadoRec(nodo.izquierdo, estado, sb);

        if (nodo.pedido.getEstado().equalsIgnoreCase(estado)) {
            Pedido p = nodo.pedido;
            sb.append("Pedido ")
                    .append(p.getId()).append(" - ")
                    .append(p.getCliente()).append(" - ")
                    .append(p.getFecha()).append(" - ")
                    .append(p.getEstado()).append("\n");
        }

        listarPorEstadoRec(nodo.derecho, estado, sb);
    }

    /**
     * Elimina del árbol el pedido cuyo id se indica. Si el nodo no tiene hijos,
     * se elimina directamente. Si tiene un solo hijo, se enlaza ese hijo en su
     * lugar. Si tiene dos hijos, se reemplaza por su sucesor inorden.
     */
    public boolean eliminarPorId(String id) {
        boolean[] eliminado = new boolean[1]; // Se usa un arreglo para poder modificar el valor dentro de la recursión.
        raiz = eliminarRec(raiz, id, eliminado);
        return eliminado[0];
    }

    /*
    Elimina recursivamente un nodo cuyo pedido tiene el id indicado.
     */
    private NodoArbol eliminarRec(NodoArbol nodo, String id, boolean[] eliminado) {
        if (nodo == null) {
            return null;
        }

        int cmp = id.compareTo(nodo.pedido.getId());
        if (cmp < 0) {
            // El id a eliminar es menor: continuar por el subárbol izquierdo.
            nodo.izquierdo = eliminarRec(nodo.izquierdo, id, eliminado);
        } else if (cmp > 0) {
            // El id a eliminar es mayor: continuar por el subárbol derecho.
            nodo.derecho = eliminarRec(nodo.derecho, id, eliminado);
        } else {
            // Se encontró el nodo a eliminar.
            eliminado[0] = true;

            // sin hijo izquierdo.
            if (nodo.izquierdo == null) {
                return nodo.derecho;
            }

            // sin hijo derecho.
            if (nodo.derecho == null) {
                return nodo.izquierdo;
            }

            // dos hijos.
            // Se busca el sucesor inorden (el menor del subárbol derecho).
            NodoArbol sucesor = nodo.derecho;
            while (sucesor.izquierdo != null) {
                sucesor = sucesor.izquierdo;
            }

            // Se copia el pedido del sucesor al nodo actual.
            nodo.pedido = sucesor.pedido;

            // Y se elimina el sucesor del subárbol derecho.
            nodo.derecho = eliminarRec(nodo.derecho, sucesor.pedido.getId(), new boolean[1]);
        }
        return nodo;
    }

    /*
    Devuelve la raíz actual del árbol.
     */
    public NodoArbol getRaiz() {
        return raiz;
    }

}
