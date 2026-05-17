package modelo;

/**
 *
 * @author Alvaro Zahid Postigo Chumacero
 */

//Representa un usuario del sistema con credenciales y rol.
public class Usuario {

    private String usuario;
    private String contrasena;
    private String rol;

    public Usuario(String usuario, String contrasena, String rol) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    //Devuelve el nombre de usuario.
    public String getUsuario() {
        return usuario;
    }

    //Devuelve la contraseña 
    public String getContrasena() {
        return contrasena;
    }

    //Devuelve el rol asignado al usuario 
    public String getRol() {
        return rol;
    }
}
