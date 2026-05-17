package modelo;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
/*
Representa una acción realizada sobre un pedido dentro del sistema.
Cada acción que modifica un pedido queda registrada
como un objeto de esta clase, permitiendo revertir operaciones como
reación, eliminación, cambio de estado o modificación de productos.
 */
public class Accion {

    //Identificadores de los tipos de acción registrados en el sistema.
    public static final String TIPO_CREAR = "CREAR";
    public static final String TIPO_ELIMINAR = "ELIMINAR";
    public static final String TIPO_CAMBIO_ESTADO = "CAMBIO_ESTADO";
    public static final String TIPO_AGREGAR_PRODUCTO = "AGREGAR_PRODUCTO";
    public static final String TIPO_ELIMINAR_PRODUCTO = "ELIMINAR_PRODUCTO";

    //Atributos 
    private String idPedido;
    private String descripcion;

    private String tipo;
    private String estadoAnterior;
    private Pedido pedidoBackup;
    private Producto productoAfectado;

    //Constructor básico que crea una acción sin tipo explícito.
    public Accion(String idPedido, String descripcion) {
        this.idPedido = idPedido;
        this.descripcion = descripcion;
        this.tipo = null;
    }

    //Constructor que crea una acción con un tipo específico.
    public Accion(String idPedido, String descripcion, String tipo) {
        this.idPedido = idPedido;
        this.descripcion = descripcion;
        this.tipo = tipo;
    }

    //Crea una acción correspondiente a la creación de un pedido.
    public static Accion crearAccionCrear(Pedido p) {
        return new Accion(p.getId(), "Crear pedido", TIPO_CREAR);
    }

    //Crea una acción correspondiente a la eliminación de un pedido y incluye una copia del pedido para poder restaurarlo.
    public static Accion crearAccionEliminar(Pedido p) {
        Accion a = new Accion(p.getId(), "Eliminar pedido", TIPO_ELIMINAR);
        a.pedidoBackup = p;
        return a;
    }

    //Crea una acción correspondiente a un cambio de estado en un pedido.
    public static Accion crearAccionCambioEstado(Pedido p, String estadoAnterior) {
        Accion a = new Accion(p.getId(),
                "Cambiar estado a " + p.getEstado(),
                TIPO_CAMBIO_ESTADO);
        a.estadoAnterior = estadoAnterior;
        return a;
    }

    //Crea una acción correspondiente a agregar un producto al pedido.
    public static Accion crearAccionAgregarProducto(Pedido p, Producto prod) {
        Accion a = new Accion(p.getId(),
                "Agregar producto " + prod.getId(),
                TIPO_AGREGAR_PRODUCTO);
        a.productoAfectado = new Producto(
                prod.getId(),
                prod.getNombre(),
                prod.getDescripcion(),
                prod.getPrecio()
        );
        return a;
    }

    //Crea una acción correspondiente a eliminar un producto del pedido.
    public static Accion crearAccionEliminarProducto(Pedido p, Producto prod) {
        Accion a = new Accion(p.getId(),
                "Eliminar producto " + prod.getId(),
                TIPO_ELIMINAR_PRODUCTO);
        a.productoAfectado = new Producto(
                prod.getId(),
                prod.getNombre(),
                prod.getDescripcion(),
                prod.getPrecio()
        );
        return a;
    }

    //Getters and Setters
    public String getIdPedido() {
        return idPedido;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public Pedido getPedidoBackup() {
        return pedidoBackup;
    }

    public Producto getProductoAfectado() {
        return productoAfectado;
    }

    @Override
    public String toString() {
        return "[" + idPedido + "] " + descripcion;
    }
}
