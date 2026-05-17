package modelo;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
/*
Representa un producto que puede ser añadido a un pedido, cada producto cuenta con un identificador, un nombre, una
descripción y un precio. Esta clase se utiliza tanto para la gestión del catálogo como para las operaciones relacionadas con
los pedidos.
 */
public class Producto {

    //Atributos
    private String id;
    private String nombre;
    private String descripcion;
    private double precio;

    //Constructor 
    public Producto(String id, String nombre, String descripcion, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    //Getters and Setters
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return id + " - " + nombre + " (" + precio + ")";
    }
}
