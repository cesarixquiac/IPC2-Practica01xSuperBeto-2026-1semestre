/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import util.ConexionDB;
import java.sql.*;

/**
 *
 * @author cesar
 */
public class PartidaDAO {

public int crearPartida(int idUsuario, int idSucursal, int nivel) {

    String sql = "INSERT INTO partida (id_usuario, id_sucursal, id_nivel_alcanzado, puntaje_total, estado) VALUES (?, ?, ?, 0, 'ACTIVA')";

    try (Connection conn = ConexionDB.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        stmt.setInt(1, idUsuario);
        stmt.setInt(2, idSucursal);
        stmt.setInt(3, nivel);

        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();

        if (rs.next()) {
            return rs.getInt(1);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return -1;
}
}
