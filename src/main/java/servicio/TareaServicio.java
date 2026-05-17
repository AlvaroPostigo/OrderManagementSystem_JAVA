package servicio;

import cola.ColaImpl;
import modelo.Tarea;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
/*
Servicio encargado de gestionar las tareas asociadas al procesamiento de pedidos dentro del sistema.
Este servicio es utilizado principalmente para generar y procesar tareas automáticamente conforme los pedidos
cambian de estado.
 */
public class TareaServicio {

    //Atributos
    private ColaImpl cola;

    //Inicializa el servicio creando una nueva cola vacía.
    public TareaServicio() {
        this.cola = new ColaImpl();
    }

    // Genera una nueva tarea y la agrega al final de la cola.
    public void generarTarea(String idPedido, String descripcion) {
        cola.encolar(new Tarea(idPedido, descripcion));
    }

    //Obtiene la tarea en el frente de la cola sin retirarla.
    public Tarea obtenerTareaActual() {
        Object o = cola.frente();
        if (o instanceof Tarea t) {
            return t;
        }
        return null;
    }

    //Procesa (desencola) la siguiente tarea pendiente.
    public Tarea procesarSiguienteTarea() {
        Object o = cola.desencolar();
        if (o instanceof Tarea t) {
            return t;
        }
        return null;
    }

    //Indica si existen tareas pendientes en la cola.
    public boolean hayTareas() {
        return !cola.estaVacia();
    }

    /* Devuelve todas las tareas actualmente en la cola se convierte el arreglo genérico
    devuelto por la cola en un arreglo estrictamente tipado*/
    public Tarea[] obtenerTareasEnCola() {
        Object[] arr = cola.aArreglo();
        int n = arr.length;
        Tarea[] tareas = new Tarea[n];
        int j = 0;
        for (Object o : arr) {
            if (o instanceof Tarea t) {
                tareas[j++] = t;
            }
        }
        if (j < n) {
            Tarea[] recortado = new Tarea[j];
            System.arraycopy(tareas, 0, recortado, 0, j);
            return recortado;
        }
        return tareas;
    }

}
