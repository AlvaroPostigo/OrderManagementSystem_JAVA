package modelo;

import java.time.LocalDate;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
/*
Representa un pedido registrado en el sistema, contiene información sobre el cliente, la fecha de creación,
su estado actual y una lista limitada de productos asociados.
 */
public class Pedido {

    //Lìmite de productos permitidos en un pedido. 
    public static final int MAX_PRODUCTOS = 20;

    //Atributos
    private String id;
    private String cliente;
    private LocalDate fecha;
    private String estado;
    private Producto[] productos;
    private int cantidadProductos;

    //Constructor
    public Pedido(String id, String cliente, LocalDate fecha, String estado) {
        this.id = id;
        this.cliente = cliente;
        this.fecha = fecha;
        this.estado = estado;
        this.productos = new Producto[MAX_PRODUCTOS];
        this.cantidadProductos = 0;
    }

    //Getters and Setters
    public String getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public int getCantidadProductos() {
        return cantidadProductos;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    //Agrega un producto al pedido si existe espacio disponible.
    public boolean agregarProducto(Producto producto) {
        if (cantidadProductos >= MAX_PRODUCTOS || producto == null) {
            return false;
        }
        productos[cantidadProductos] = producto;
        cantidadProductos++;
        return true;
    }

    /*Elimina un producto del pedido según su id, Se realiza una búsqueda secuencial, y al eliminar el producto se desplazan
    los elementos restantes para mantener el arreglo compacto.*/
    public Producto eliminarProductoPorId(String idProducto) {
        if (idProducto == null || cantidadProductos == 0) {
            return null;
        }
        for (int i = 0; i < cantidadProductos; i++) {
            Producto p = productos[i];
            if (p != null && idProducto.equals(p.getId())) {
                Producto eliminado = p;
                for (int j = i; j < cantidadProductos - 1; j++) {
                    productos[j] = productos[j + 1];
                }
                productos[cantidadProductos - 1] = null;
                cantidadProductos--;
                return eliminado;
            }
        }
        return null;
    }

    // Devuelve una copia de los productos almacenados para proteger el arreglo interno de modificaciones externas.
    public Producto[] getProductos() {
        if (cantidadProductos == 0) {
            return new Producto[0];
        }
        Producto[] copia = new Producto[cantidadProductos];
        for (int i = 0; i < cantidadProductos; i++) {
            copia[i] = productos[i];
        }
        return copia;
    }

    @Override
    public String toString() {
        return "Pedido " + id + " - " + cliente + " - " + fecha + " - " + estado;
    }
}
