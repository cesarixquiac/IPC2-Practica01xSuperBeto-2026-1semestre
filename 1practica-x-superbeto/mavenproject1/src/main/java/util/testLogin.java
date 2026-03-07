/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import dao.UsuarioDAO;
import model.Usuario;

/**
 *
 * @author cesar
 */
public class testLogin {

    public static void main(String[] args) {

        UsuarioDAO usuarioDAO = new UsuarioDAO();

        Usuario usuario = usuarioDAO.login("admin1", "1234");

        if (usuario != null) {
            System.out.println("Login exitoso");
            System.out.println("Bienvenido: " + usuario.getNombre());
            System.out.println("Rol: " + usuario.getRol().getNombre());

            if (usuario.getSucursal() != null) {
                System.out.println("Sucursal: " + usuario.getSucursal().getNombre());
            }

        } else {
            System.out.println("Usuario o contraseña incorrectos");
        }
    }
}
