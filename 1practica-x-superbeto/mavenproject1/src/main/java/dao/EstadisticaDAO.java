/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.ConexionDB;
/**
 *
 * @author cesar
 */
public class EstadisticaDAO {


    public List<Object[]> obtenerRankingPorSucursal(int idSucursal) {
        List<Object[]> ranking = new ArrayList<>();
        
    
        String sql = """
                SELECT u.nombre, MAX(p.puntaje_total) as mejor_puntaje, MAX(p.id_nivel_alcanzado) as nivel_maximo
                FROM partida p
                JOIN usuario u ON p.id_usuario = u.id_usuario
                WHERE p.id_sucursal = ? AND p.estado = 'FINALIZADA'
                GROUP BY u.id_usuario
                ORDER BY mejor_puntaje DESC
                LIMIT 10
                """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setInt(1, idSucursal);
            
            try (ResultSet rs = ps.executeQuery()) {
                int posicion = 1;
                while (rs.next()) {
                    String jugador = rs.getString("nombre");
                    int puntaje = rs.getInt("mejor_puntaje");
                    int nivel = rs.getInt("nivel_maximo");
                    
                    ranking.add(new Object[]{posicion, jugador, puntaje, nivel});
                    posicion++;
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener el ranking de la sucursal:");
            e.printStackTrace();
        }

        return ranking;
    }


    public Map<String, Integer> obtenerEstadisticasSucursal(int idSucursal) {
        Map<String, Integer> estadisticas = new HashMap<>();
        
        String sql = """
                SELECT 
                    SUM(CASE WHEN ped.id_estado_actual = 4 THEN 1 ELSE 0 END) as entregados,
                    SUM(CASE WHEN ped.id_estado_actual = 5 THEN 1 ELSE 0 END) as cancelados,
                    SUM(CASE WHEN ped.id_estado_actual = 6 THEN 1 ELSE 0 END) as vencidos
                FROM pedido ped
                JOIN partida par ON ped.id_partida = par.id_partida
                WHERE par.id_sucursal = ? AND par.estado = 'FINALIZADA'
                """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setInt(1, idSucursal);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    estadisticas.put("ENTREGADOS", rs.getInt("entregados"));
                    estadisticas.put("CANCELADOS", rs.getInt("cancelados"));
                    estadisticas.put("VENCIDOS", rs.getInt("vencidos"));
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener estadísticas de la sucursal:");
            e.printStackTrace();
        }

        return estadisticas;
    }
}