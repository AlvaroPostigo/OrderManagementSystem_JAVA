package servicio;

import java.util.ArrayList;
import listaEnlazada.ListaUsuario;
import modelo.Usuario;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */
//Servicio para el Login
public class LoginServicio {

    private final ListaUsuario usuarios;

    //Constructor
    public LoginServicio() {
        usuarios = new ListaUsuario();

        usuarios.agregar(new Usuario("admin", "admin", "ADMIN"));

        usuarios.agregar(new Usuario("vendedor", "1234", "USUARIO"));
    }

    //Valida las credenciales de inicio de sesión.
    public Usuario validar(String user, String pass) {
        return usuarios.buscar(user, pass);
    }
}
