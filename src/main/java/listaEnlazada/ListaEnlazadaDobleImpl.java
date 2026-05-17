package listaEnlazada;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
/*
Implementación del TAD Lista Doblemente Enlazada Circular, esta lista se
utiliza para gestionar el catálogo de productos, permitiendo operaciones de
inserción, eliminación, búsqueda y recorrido.
 */
public class ListaEnlazadaDobleImpl implements ListaDobleInterface {

    //Referencia al primer nodo de la lista.
    private NodoDoble cabeza;

    //Referencia al último nodo de la lista.
    private NodoDoble cola;

    //Cantidad de elementos almacenados.
    private int longitud;

    //Crea una lista vacía.
    public ListaEnlazadaDobleImpl() {
        cabeza = null;
        cola = null;
        longitud = 0;
    }

    // Indica si la lista está vacía.
    @Override
    public boolean estaVacia() {
        return longitud == 0;
    }

    //Inserta un elemento al inicio de la lista.
    @Override
    public void insertarInicio(Object elemento) {
        NodoDoble nuevo = new NodoDoble(elemento);

        if (estaVacia()) {
            cabeza = cola = nuevo;
            // Se establece la circularidad de un solo nodo.
            nuevo.siguiente = nuevo;
            nuevo.anterior = nuevo;
        } else {
            nuevo.siguiente = cabeza;
            nuevo.anterior = cola;
            cabeza.anterior = nuevo;
            cola.siguiente = nuevo;
            cabeza = nuevo;
        }
        longitud++;
    }

    //Inserta un elemento al final de la lista.
    @Override
    public void insertarFinal(Object elemento) {
        NodoDoble nuevo = new NodoDoble(elemento);

        if (estaVacia()) {
            cabeza = cola = nuevo;
            nuevo.siguiente = nuevo;
            nuevo.anterior = nuevo;
        } else {
            nuevo.anterior = cola;
            nuevo.siguiente = cabeza;
            cola.siguiente = nuevo;
            cabeza.anterior = nuevo;
            cola = nuevo;
        }
        longitud++;
    }

    //Inserta un elemento en una posición específica.
    @Override
    public void insertarEn(Object elemento, int posicion) {
        if (posicion <= 0 || estaVacia()) {
            insertarInicio(elemento);
        } else if (posicion >= longitud) {
            insertarFinal(elemento);
        } else {
            NodoDoble actual = cabeza;

            for (int i = 0; i < posicion; i++) {
                actual = actual.siguiente;
            }

            NodoDoble nuevo = new NodoDoble(elemento);
            NodoDoble anterior = actual.anterior;

            nuevo.anterior = anterior;
            nuevo.siguiente = actual;
            anterior.siguiente = nuevo;
            actual.anterior = nuevo;

            longitud++;
        }
    }

    // Elimina y devuelve el primer elemento de la lista.
    @Override
    public Object retirarInicio() {
        if (estaVacia()) {
            return null;
        }

        Object e = cabeza.elemento;

        if (longitud == 1) {
            cabeza = cola = null;
        } else {
            cabeza = cabeza.siguiente;
            cabeza.anterior = cola;
            cola.siguiente = cabeza;
        }

        longitud--;
        return e;
    }

    //Elimina y devuelve el último elemento de la lista.
    @Override
    public Object retirarFinal() {
        if (estaVacia()) {
            return null;
        }

        Object e = cola.elemento;

        if (longitud == 1) {
            cabeza = cola = null;
        } else {
            cola = cola.anterior;
            cola.siguiente = cabeza;
            cabeza.anterior = cola;
        }

        longitud--;
        return e;
    }

    // Elimina el elemento ubicado en la posición indicada.
    @Override
    public Object retirarEn(int posicion) {
        if (estaVacia()) {
            return null;
        }
        if (posicion <= 0) {
            return retirarInicio();
        }
        if (posicion >= longitud - 1) {
            return retirarFinal();
        }

        NodoDoble actual = cabeza;
        for (int i = 0; i < posicion; i++) {
            actual = actual.siguiente;
        }

        NodoDoble ant = actual.anterior;
        NodoDoble sig = actual.siguiente;

        ant.siguiente = sig;
        sig.anterior = ant;

        longitud--;

        return actual.elemento;
    }

    //Busca un elemento mediante reflexión, invocando getId().
    @Override
    public Object buscarPorId(String id) {
        if (estaVacia() || id == null) {
            return null;
        }

        NodoDoble actual = cabeza;
        int recorridos = 0;

        while (recorridos < longitud) {
            try {
                java.lang.reflect.Method m
                        = actual.elemento.getClass().getMethod("getId");
                Object valor = m.invoke(actual.elemento);

                if (valor != null && valor.toString().equals(id)) {
                    return actual.elemento;
                }
            } catch (Exception e) {
                // Si el elemento no tiene getId(), no se puede buscar por id.
                return null;
            }

            actual = actual.siguiente;
            recorridos++;
        }
        return null;
    }

    //Recorre la lista de inicio a fin y la muestra.
    @Override
    public String imprimirAdelante() {
        StringBuilder sb = new StringBuilder();

        if (estaVacia()) {
            return sb.toString();
        }

        NodoDoble actual = cabeza;
        int recorridos = 0;

        while (recorridos < longitud) {
            sb.append(actual.elemento).append("\n");
            actual = actual.siguiente;
            recorridos++;
        }

        return sb.toString();
    }

    //Recorre la lista desde el final hacia el inicio y la muestra.
    @Override
    public String imprimirAtras() {
        StringBuilder sb = new StringBuilder();

        if (estaVacia()) {
            return sb.toString();
        }

        NodoDoble actual = cola;
        int recorridos = 0;

        while (recorridos < longitud) {
            sb.append(actual.elemento).append("\n");
            actual = actual.anterior;
            recorridos++;
        }

        return sb.toString();
    }

    //Retorna la cantidad de elementos almacenados.
    @Override
    public int getLongitud() {
        return longitud;
    }

    /*
    Devuelve el elemento ubicado en la posición indicada.
    se usa para obtener elementos de manera directa.
     */
    public Object obtenerEn(int posicion) {
        if (posicion < 0 || posicion >= longitud || estaVacia()) {
            return null;
        }

        NodoDoble actual = cabeza;

        for (int i = 0; i < posicion; i++) {
            actual = actual.siguiente;
        }

        return actual.elemento;
    }
}
