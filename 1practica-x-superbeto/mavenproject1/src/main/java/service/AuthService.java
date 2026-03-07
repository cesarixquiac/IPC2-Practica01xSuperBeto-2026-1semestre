/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;
import dao.UsuarioDAO;
import model.Usuario;

/**
 *
 * @author cesar
 */
public class AuthService {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario autenticar(String username, String password) {
        return usuarioDAO.login(username, password);
    }
}