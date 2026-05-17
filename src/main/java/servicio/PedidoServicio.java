package servicio;

import arbol.ArbolBSTImpl;
import java.time.LocalDate;
import modelo.Accion;
import modelo.Pedido;
import modelo.Producto;
import modelo.Tarea;
import persistencia.PedidosXmlRepository;
import pila.PilaImpl;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
/*
Servicio que encapsula la lógica de negocio relacionada con los pedidos.
Administra los pedidos almacenados en un Árbol Binario de Búsqueda.
Registra las operaciones realizadas en una pila de acciones para permitir "deshacer".
Coordina la generación y el procesamiento de tareas.
Persiste la información de pedidos en un archivo XML. 
 */
public class PedidoServicio {

    //Atributos
    private ArbolBSTImpl arbolPedidos;
    private PedidosXmlRepository repo;
    private PilaImpl pilaAcciones;
    private final TareaServicio tareaService;

    //Crea el servicio de pedidos, cargando la información desde el archivo XML.
    public PedidoServicio(String rutaXml, TareaServicio tareaService) {
        this.repo = new PedidosXmlRepository(rutaXml);
        this.tareaService = tareaService;
        try {
            this.arbolPedidos = repo.cargar();
        } catch (Exception e) {
            this.arbolPedidos = new ArbolBSTImpl();
        }

        this.pilaAcciones = new PilaImpl();
    }

    /*Crea un nuevo pedido y lo inserta en el árbol. Si la cola de tareas está vacía, el pedido comienza en "EN_PREPARACION"
    Si ya existen tareas pendientes, el pedido comienza en "PENDIENTE"
    Además, se registra la acción en la pila y se genera una tarea asociada
     */
    public Pedido crearPedido(String id, String cliente) {
        boolean colaVaciaAntes = (tareaService == null) || !tareaService.hayTareas();
        String estadoInicial = colaVaciaAntes ? "EN_PREPARACION" : "PENDIENTE";

        Pedido p = new Pedido(id, cliente, LocalDate.now(), estadoInicial);
        arbolPedidos.insertar(p);

        // Se registra la creación en la pila de acciones.
        pilaAcciones.push(Accion.crearAccionCrear(p));

        // Se genera la tarea en la cola, si el servicio de tareas está disponible.
        if (tareaService != null) {
            tareaService.generarTarea(id, "Preparar pedido " + id);
        }

        guardar();
        return p;
    }

    //Busca un pedido por su identificador.
    public Pedido buscarPorId(String id) {
        return arbolPedidos.buscarPorId(id);
    }

    /*
    Cambia el estado de un pedido, además de registrar la acción en la pila y persistir el cambio, si el nuevo
    estado es "ENVIADO" y hay tareas pendientes en la cola, se intenta
    promover el siguiente pedido a "EN_PREPARACION"
     */
    public void cambiarEstado(String idPedido, String nuevoEstado) {
        Pedido p = buscarPorId(idPedido);
        if (p != null) {
            String estadoAnterior = p.getEstado();
            p.setEstado(nuevoEstado);

            // Registrar el cambio de estado para poder deshacerlo.
            pilaAcciones.push(Accion.crearAccionCambioEstado(p, estadoAnterior));
            guardar();

            // Si el pedido se marca como ENVIADO, se intenta promover al siguiente pedido.
            if ("ENVIADO".equalsIgnoreCase(nuevoEstado) && tareaService != null) {
                try {
                    if (tareaService.hayTareas()) {
                        Tarea siguiente = tareaService.obtenerTareaActual();
                        if (siguiente != null) {
                            Pedido pSig = buscarPorId(siguiente.getIdPedido());
                            if (pSig != null
                                    && !"ENVIADO".equalsIgnoreCase(pSig.getEstado())
                                    && !"COMPLETADO".equalsIgnoreCase(pSig.getEstado())) {

                                pSig.setEstado("EN_PREPARACION");
                                // Acción solo informativa
                                pilaAcciones.push(new Accion(
                                        pSig.getId(),
                                        "Cambiar estado a EN_PREPARACION"));
                                guardar();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
    Agrega un producto a un pedido específico.
    Si el producto se agrega correctamente, se registra la acción en la pila
    para poder deshacerla posteriormente.
     */
    public void agregarProductoAPedido(String idPedido, Producto producto) {
        Pedido p = buscarPorId(idPedido);
        if (p != null && p.agregarProducto(producto)) {
            pilaAcciones.push(Accion.crearAccionAgregarProducto(p, producto));
            guardar();
        }
    }

    //Elimina un producto concreto de un pedido.
    public boolean eliminarProductoDePedido(String idPedido, String idProducto) {
        Pedido p = buscarPorId(idPedido);
        if (p == null) {
            return false;
        }
        Producto eliminado = p.eliminarProductoPorId(idProducto);
        if (eliminado != null) {
            pilaAcciones.push(Accion.crearAccionEliminarProducto(p, eliminado));
            guardar();
            return true;
        }
        return false;
    }

    //Lista todos los pedidos utilizando el recorrido inorden del árbol.
    public String listarPedidosOrdenados() {
        return arbolPedidos.recorridoInOrden();
    }

    //Lista los pedidos asociados a un cliente concreto.
    public String listarPedidosPorCliente(String cliente) {
        return arbolPedidos.listarPorCliente(cliente);
    }

    //Lista los pedidos que se encuentran en un estado específico.
    public String listarPedidosPorEstado(String estado) {
        return arbolPedidos.listarPorEstado(estado);
    }

    /*
    Deshace la última acción registrada en la pila.
    Eliminar un pedido recién creado.
    Restaurar un pedido eliminado.
    Revertir un cambio de estado.
    Deshacer el agregado o la eliminación de un producto.
     */
    public String deshacerUltimaAccion() {
        Object o = pilaAcciones.pop();
        if (!(o instanceof Accion)) {
            return "No hay acciones que deshacer.";
        }
        Accion a = (Accion) o;

        if (a.getTipo() == null) {
            return "Acción registrada (sin datos para deshacer): " + a.toString();
        }

        String tipo = a.getTipo();

        if (Accion.TIPO_CREAR.equals(tipo)) {
            arbolPedidos.eliminarPorId(a.getIdPedido());
            guardar();
            return "Se deshizo la creación del pedido " + a.getIdPedido();

        } else if (Accion.TIPO_ELIMINAR.equals(tipo)) {
            Pedido respaldo = a.getPedidoBackup();
            if (respaldo != null) {
                arbolPedidos.insertar(respaldo);
                guardar();
                return "Se restauró el pedido eliminado " + respaldo.getId();
            }
            return "No fue posible restaurar el pedido eliminado.";

        } else if (Accion.TIPO_CAMBIO_ESTADO.equals(tipo)) {
            Pedido p = buscarPorId(a.getIdPedido());
            if (p != null && a.getEstadoAnterior() != null) {
                p.setEstado(a.getEstadoAnterior());
                guardar();
                return "Se revirtió el estado del pedido " + p.getId()
                        + " a " + a.getEstadoAnterior();
            }
            return "No fue posible revertir el cambio de estado.";

        } else if (Accion.TIPO_AGREGAR_PRODUCTO.equals(tipo)) {
            Pedido p = buscarPorId(a.getIdPedido());
            if (p != null && a.getProductoAfectado() != null) {
                p.eliminarProductoPorId(a.getProductoAfectado().getId());
                guardar();
                return "Se deshizo el agregado del producto "
                        + a.getProductoAfectado().getId()
                        + " al pedido " + p.getId();
            }
            return "No fue posible deshacer el agregado de producto.";

        } else if (Accion.TIPO_ELIMINAR_PRODUCTO.equals(tipo)) {
            Pedido p = buscarPorId(a.getIdPedido());
            if (p != null && a.getProductoAfectado() != null) {
                p.agregarProducto(a.getProductoAfectado());
                guardar();
                return "Se deshizo la eliminación del producto "
                        + a.getProductoAfectado().getId()
                        + " del pedido " + p.getId();
            }
            return "No fue posible deshacer la eliminación de producto.";
        }

        return "No se reconoce el tipo de acción para deshacer.";
    }

    //Guarda el estado actual del árbol de pedidos en el archivo XML.
    private void guardar() {
        try {
            repo.guardar(arbolPedidos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Elimina un pedido del árbol por su identificador. 
    public boolean eliminarPedido(String id) {
        Pedido p = buscarPorId(id);
        boolean eliminado = arbolPedidos.eliminarPorId(id);
        if (eliminado && p != null) {
            pilaAcciones.push(Accion.crearAccionEliminar(p));
            guardar();
        }
        return eliminado;
    }

    //Verifica si existe un pedido con el id indicado.
    public boolean existeId(String id) {
        return arbolPedidos.buscarPorId(id) != null;
    }

}
