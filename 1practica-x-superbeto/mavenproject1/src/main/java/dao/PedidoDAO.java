/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author cesar
 */


import model.Producto;
import util.ConexionDB;

import java.sql.*;
import java.util.List;

public class PedidoDAO {

    public int crearPedido(int idPartida, int tiempoLimite, List<Producto> productos) {

        Connection conn = null;

        try {

            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            String sqlPedido = """
                    INSERT INTO pedido
                    (id_partida, id_estado_actual, tiempo_limite_segundos, fecha_creacion)
                    VALUES (?, 1, ?, NOW())
                    """;

            PreparedStatement psPedido = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);

            psPedido.setInt(1, idPartida);
            psPedido.setInt(2, tiempoLimite);

            psPedido.executeUpdate();

            ResultSet rs = psPedido.getGeneratedKeys();

            int idPedido = 0;

            if (rs.next()) {
                idPedido = rs.getInt(1);
            }

            String sqlDetalle = """
                    INSERT INTO detalle_pedido
                    (id_pedido, id_producto, cantidad)
                    VALUES (?, ?, ?)
                    """;

            PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle);

            for (Producto p : productos) {

                psDetalle.setInt(1, idPedido);
                psDetalle.setInt(2, p.getIdProducto());
                psDetalle.setInt(3, 1);

                psDetalle.executeUpdate();
            }

            String sqlHistorial = """
                    INSERT INTO historial_estado
                    (id_pedido, id_estado, fecha_cambio)
                    VALUES (?, 1, NOW())
                    """;

            PreparedStatement psHistorial = conn.prepareStatement(sqlHistorial);
            psHistorial.setInt(1, idPedido);
            psHistorial.executeUpdate();

            conn.commit();

            return idPedido;

        } catch (Exception e) {

            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();
        }

        return -1;
    }


    public void actualizarEstadoPedido(int idPedido, int idEstado) {

        Connection conn = null;

        try {

            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            String sqlUpdate = """
                    UPDATE pedido
                    SET id_estado_actual = ?
                    WHERE id_pedido = ?
                    """;

            PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
            psUpdate.setInt(1, idEstado);
            psUpdate.setInt(2, idPedido);
            psUpdate.executeUpdate();

            String sqlHistorial = """
                    INSERT INTO historial_estado
                    (id_pedido, id_estado, fecha_cambio)
                    VALUES (?, ?, NOW())
                    """;

            PreparedStatement psHistorial = conn.prepareStatement(sqlHistorial);
            psHistorial.setInt(1, idPedido);
            psHistorial.setInt(2, idEstado);
            psHistorial.executeUpdate();

            conn.commit();

        } catch (Exception e) {

            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();
        }
    }


   
    public void finalizarPedido(int idPedido, int idEstado, int puntos) {

        Connection conn = null;

        try {

            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            String sql = """
                    UPDATE pedido
                    SET id_estado_actual = ?,
                        fecha_finalizacion = NOW(),
                        puntos_obtenidos = ?
                    WHERE id_pedido = ?
                    """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idEstado);
            ps.setInt(2, puntos);
            ps.setInt(3, idPedido);
            ps.executeUpdate();

            String sqlHistorial = """
                    INSERT INTO historial_estado
                    (id_pedido, id_estado, fecha_cambio)
                    VALUES (?, ?, NOW())
                    """;

            PreparedStatement psHistorial = conn.prepareStatement(sqlHistorial);
            psHistorial.setInt(1, idPedido);
            psHistorial.setInt(2, idEstado);
            psHistorial.executeUpdate();

            conn.commit();

        } catch (Exception e) {

            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();
        }
    }
    
    public void finalizarPedido(int idPedido, int idEstado, int puntos, boolean bonoAplicado) {
        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            String sql = """
                    UPDATE pedido
                    SET id_estado_actual = ?,
                        fecha_finalizacion = NOW(),
                        puntos_obtenidos = ?,
                        bono_eficiencia_aplicado = ?
                    WHERE id_pedido = ?
                    """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idEstado);
            ps.setInt(2, puntos);
            ps.setBoolean(3, bonoAplicado); 
            ps.setInt(4, idPedido);
            ps.executeUpdate();

            String sqlHistorial = """
                    INSERT INTO historial_estado
                    (id_pedido, id_estado, fecha_cambio)
                    VALUES (?, ?, NOW())
                    """;

            PreparedStatement psHistorial = conn.prepareStatement(sqlHistorial);
            psHistorial.setInt(1, idPedido);
            psHistorial.setInt(2, idEstado);
            psHistorial.executeUpdate();

            conn.commit();
        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}

