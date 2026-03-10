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

        String sql = """
                INSERT INTO partida
                (id_usuario, id_sucursal, id_nivel_alcanzado, puntaje_total, estado, fecha_inicio)
                VALUES (?, ?, ?, 0, 'ACTIVA', NOW())
                """;
                
        Connection conn = null;

        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false); 
            
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idSucursal);
            stmt.setInt(3, nivel);

            stmt.executeUpdate();
            
            conn.commit(); 

            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }

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

   
    public void finalizarPartida(int idPartida, int puntajeFinal, int nivelAlcanzado){

        if (idPartida <= 0) {
            System.err.println("Error: ID de partida inválido (" + idPartida + "). No se actualizó la BD.");
            return;
        }

       
        String sql = """
                UPDATE partida
                SET puntaje_total = ?,
                    id_nivel_alcanzado = ?,
                    fecha_fin = NOW(),
                    estado = 'FINALIZADA'
                WHERE id_partida = ?
                """;

        Connection conn = null;

        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false); 

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, puntajeFinal);
            ps.setInt(2, nivelAlcanzado); // Asignamos el nivel a la consulta
            ps.setInt(3, idPartida);

            int filasAfectadas = ps.executeUpdate();
            
            if(filasAfectadas > 0) {
                conn.commit(); 
                System.out.println("BD: Partida " + idPartida + " finalizada correctamente con " + puntajeFinal + " puntos en Nivel " + nivelAlcanzado);
            } else {
                System.err.println("BD: No se encontró la partida con ID " + idPartida);
            }

        } catch(Exception e) {
            try {
                if (conn != null) conn.rollback(); 
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}
