/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author cesar
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import util.ConexionDB;

public class NivelDAO {

   
    public Map<Integer, int[]> obtenerReglasNiveles() {
        Map<Integer, int[]> niveles = new HashMap<>();
        String sql = "SELECT numero_nivel, tiempo_base_segundos, pedidos_para_subir FROM nivel";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int nivel = rs.getInt("numero_nivel");
                int tiempo = rs.getInt("tiempo_base_segundos");
                int metaPedidos = rs.getInt("pedidos_para_subir");
                
                // Guardamos el tiempo y la meta en un arreglo
                niveles.put(nivel, new int[]{tiempo, metaPedidos});
            }

        } catch (Exception e) {
            System.err.println("Error al obtener reglas de niveles:");
            e.printStackTrace();
        }

        return niveles;
    }
}