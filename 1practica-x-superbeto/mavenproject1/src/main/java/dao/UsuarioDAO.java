/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import java.sql.*;
import model.Rol;
import model.Sucursal;
import model.Usuario;
import util.ConexionDB;


/**
 *
 * @author cesar
 */
public class UsuarioDAO {

    public Usuario login(String username, String password) {

        if (username == null || username.isEmpty() ||
            password == null || password.isEmpty()) {
            return null;
        }

        String sql = """
            SELECT u.id_usuario, u.username, u.nombre, u.activo,
                   r.id_rol, r.nombre AS rol_nombre,
                   s.id_sucursal, s.nombre AS sucursal_nombre
            FROM usuario u
            INNER JOIN rol r ON u.id_rol = r.id_rol
            LEFT JOIN sucursal s ON u.id_sucursal = s.id_sucursal
            WHERE u.username = ? AND u.password = ? AND u.activo = 1
        """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setUsername(rs.getString("username"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setActivo(rs.getBoolean("activo"));

                // Rol
                Rol rol = new Rol(
                        rs.getInt("id_rol"),
                        rs.getString("rol_nombre")
                );
                usuario.setRol(rol);

                // Sucursal (puede ser null en super admin)
                int idSucursal = rs.getInt("id_sucursal");
                if (!rs.wasNull()) {
                    Sucursal sucursal = new Sucursal(
                            idSucursal,
                            rs.getString("sucursal_nombre")
                    );
                    usuario.setSucursal(sucursal);
                }

                return usuario;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}