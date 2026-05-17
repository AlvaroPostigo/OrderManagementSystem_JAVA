/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package listaEnlazada;

import modelo.Usuario;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
public class ListaUsuario {

    private static class NodoUsuario {

        Usuario usuario;
        NodoUsuario siguiente;

        NodoUsuario(Usuario usuario) {
            this.usuario = usuario;
        }
    }

    private NodoUsuario cabeza;

    //Inserta un usuario al inicio de la lista.
    public void agregar(Usuario usuario) {
        NodoUsuario nuevo = new NodoUsuario(usuario);
        nuevo.siguiente = cabeza;
        cabeza = nuevo;
    }

    //Busca un usuario por nombre y contraseña.
    public Usuario buscar(String nombre, String contrasena) {
        NodoUsuario aux = cabeza;
        while (aux != null) {
            Usuario u = aux.usuario;
            if (u.getUsuario().equals(nombre)
                    && u.getContrasena().equals(contrasena)) {
                return u;
            }
            aux = aux.siguiente;
        }
        return null;
    }

    //Indica si la lista está vacía.
    public boolean estaVacia() {
        return cabeza == null;
    }
}
