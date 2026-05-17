package modelo;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
/*
Representa una tarea asociada a un pedido dentro del sistema, describe una acción pendiente relacionada con un pedido, como su
preparación, empaquetado o envío. Estas tareas se gestionan mediante el TAD Cola, lo que garantiza que se procesen en el orden en que fueron generadas.
 */
public class Tarea {

    //Atributos
    private String idPedido;
    private String descripcion;

    //Constructor
    public Tarea(String idPedido, String descripcion) {
        this.idPedido = idPedido;
        this.descripcion = descripcion;
    }

    //Getters and Setters
    public String getIdPedido() {
        return idPedido;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return "Pedido " + idPedido + ": " + descripcion;
    }
}
