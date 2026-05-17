package servicio;

import listaEnlazada.ListaEnlazadaDobleImpl;
import modelo.Producto;
import persistencia.ProductosXmlRepository;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
/*
Servicio que gestiona el catálogo de productos de la aplicación.
Almacena los productos en una lista doblemente enlazada circular.
Permite agregar, buscar, actualizar y eliminar productos.
Persiste el catálogo en un archivo XML.
 */
public class ProductoServicio {

    //Atributos
    private ListaEnlazadaDobleImpl catalogo;
    private ProductosXmlRepository repo;

    //Crea el servicio de productos y carga el catálogo desde un archivo XML.
    public ProductoServicio(String rutaXml) {
        this.repo = new ProductosXmlRepository(rutaXml);
        try {
            this.catalogo = repo.cargar();
        } catch (Exception e) {
            this.catalogo = new ListaEnlazadaDobleImpl();
        }
    }

    //Agrega un nuevo producto al catálogo.
    public void agregarProducto(Producto p) {
        catalogo.insertarFinal(p);
        guardar();
    }

    //Busca un producto por su identificador.
    public Producto buscarPorId(String id) {
        Object o = catalogo.buscarPorId(id);
        if (o instanceof Producto) {
            return (Producto) o;
        }
        return null;
    }

    //Devuelve una representación en texto de todos los productos del catálogo.
    public String listarProductos() {
        return catalogo.imprimirAdelante();
    }

    //Obtiene todos los productos del catálogo en un arreglo.
    public Producto[] obtenerTodos() {
        int n = catalogo.getLongitud();
        Producto[] arr = new Producto[n];
        for (int i = 0; i < n; i++) {
            Object o = catalogo.obtenerEn(i);
            if (o instanceof Producto p) {
                arr[i] = p;
            }
        }
        return arr;
    }

    // Elimina un producto del catálogo según su id.
    public boolean eliminarPorId(String id) {
        for (int i = 0; i < catalogo.getLongitud(); i++) {
            Object o = catalogo.obtenerEn(i);
            if (o instanceof Producto p && p.getId().equals(id)) {
                catalogo.retirarEn(i);
                guardar();
                return true;
            }
        }
        return false;
    }

    // Actualiza los datos de un producto existente.
    public boolean actualizarProducto(String id,
            String nuevoNombre,
            String nuevaDescripcion,
            double nuevoPrecio) {
        for (int i = 0; i < catalogo.getLongitud(); i++) {
            Object o = catalogo.obtenerEn(i);
            if (o instanceof Producto p && p.getId().equals(id)) {
                p.setNombre(nuevoNombre);
                p.setDescripcion(nuevaDescripcion);
                p.setPrecio(nuevoPrecio);
                guardar();
                return true;
            }
        }
        return false;
    }

    //Verifica si existe un producto con el id indicado.
    public boolean existeId(String id) {
        int n = catalogo.getLongitud();
        for (int i = 0; i < n; i++) {
            Object o = catalogo.obtenerEn(i);
            if (o instanceof Producto p && p.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    //Busca un producto por su nombre, ignorando.
    public Producto buscarPorNombre(String nombre) {
        int n = catalogo.getLongitud();
        for (int i = 0; i < n; i++) {
            Object o = catalogo.obtenerEn(i);
            if (o instanceof Producto p
                    && p.getNombre() != null
                    && p.getNombre().equalsIgnoreCase(nombre)) {
                return p;
            }
        }
        return null;
    }

    // Guarda el estado actual del catálogo en el archivo XML.
    private void guardar() {
        try {
            repo.guardar(catalogo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
