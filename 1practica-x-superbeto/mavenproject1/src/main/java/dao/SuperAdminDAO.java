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
public class SuperAdminDAO {

    public List<Object[]> obtenerRankingGlobal() {
        List<Object[]> ranking = new ArrayList<>();
        String sql = """
                SELECT u.nombre as jugador, s.nombre as sucursal, 
                       MAX(p.puntaje_total) as mejor_puntaje, 
                       MAX(p.id_nivel_alcanzado) as nivel_maximo
                FROM partida p
                JOIN usuario u ON p.id_usuario = u.id_usuario
                JOIN sucursal s ON p.id_sucursal = s.id_sucursal
                WHERE p.estado = 'FINALIZADA'
                GROUP BY u.id_usuario, s.nombre
                ORDER BY mejor_puntaje DESC
                LIMIT 10
                """;
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int posicion = 1;
            while (rs.next()) {
                ranking.add(new Object[]{posicion++, rs.getString("jugador"), rs.getString("sucursal"), rs.getInt("mejor_puntaje"), rs.getInt("nivel_maximo")});
            }
        } catch (Exception e) { e.printStackTrace(); }
        return ranking;
    }

    public Map<String, Integer> obtenerEstadisticasGlobales() {
        Map<String, Integer> estadisticas = new HashMap<>();
        String sql = """
                SELECT 
                    SUM(CASE WHEN ped.id_estado_actual = 4 THEN 1 ELSE 0 END) as entregados,
                    SUM(CASE WHEN ped.id_estado_actual = 5 THEN 1 ELSE 0 END) as cancelados,
                    SUM(CASE WHEN ped.id_estado_actual = 6 THEN 1 ELSE 0 END) as vencidos
                FROM pedido ped
                JOIN partida par ON ped.id_partida = par.id_partida
                WHERE par.estado = 'FINALIZADA'
                """;
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                estadisticas.put("ENTREGADOS", rs.getInt("entregados"));
                estadisticas.put("CANCELADOS", rs.getInt("cancelados"));
                estadisticas.put("VENCIDOS", rs.getInt("vencidos"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return estadisticas;
    }

    public List<Object[]> obtenerTodasLasSucursales() {
        List<Object[]> sucursales = new ArrayList<>();
        String sql = "SELECT id_sucursal, nombre, direccion, activa FROM sucursal";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String estado = rs.getBoolean("activa") ? "ACTIVA" : "INACTIVA";
                sucursales.add(new Object[]{rs.getInt("id_sucursal"), rs.getString("nombre"), rs.getString("direccion"), estado});
            }
        } catch (Exception e) { e.printStackTrace(); }
        return sucursales;
    }

    public boolean crearSucursal(String nombre, String direccion) {
        String sql = "INSERT INTO sucursal (nombre, direccion, activa) VALUES (?, ?, 1)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, direccion);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean modificarSucursal(int idSucursal, String nombre, String direccion) {
        String sql = "UPDATE sucursal SET nombre = ?, direccion = ? WHERE id_sucursal = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, direccion);
            ps.setInt(3, idSucursal);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    public List<Object[]> obtenerTodosLosUsuarios() {
        List<Object[]> usuarios = new ArrayList<>();
        String sql = """
            SELECT u.id_usuario, u.username, u.nombre, r.nombre as rol, s.nombre as sucursal, u.activo
            FROM usuario u
            JOIN rol r ON u.id_rol = r.id_rol
            LEFT JOIN sucursal s ON u.id_sucursal = s.id_sucursal
        """;
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String sucursal = rs.getString("sucursal") != null ? rs.getString("sucursal") : "Global";
                String estado = rs.getBoolean("activo") ? "ACTIVO" : "INACTIVA";
                usuarios.add(new Object[]{rs.getInt("id_usuario"), rs.getString("username"), rs.getString("nombre"), rs.getString("rol"), sucursal, estado});
            }
        } catch (Exception e) { e.printStackTrace(); }
        return usuarios;
    }

    public boolean crearUsuario(String username, String password, String nombre, int idRol, int idSucursal) {
        String sql = "INSERT INTO usuario (username, password, nombre, id_rol, id_sucursal, activo) VALUES (?, ?, ?, ?, ?, 1)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, nombre);
            ps.setInt(4, idRol);
            
            if (idSucursal > 0) {
                ps.setInt(5, idSucursal);
            } else {
                ps.setNull(5, java.sql.Types.INTEGER); 
            }
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public Map<String, Integer> obtenerMapaRoles() {
        Map<String, Integer> roles = new HashMap<>();
        String sql = "SELECT id_rol, nombre FROM rol WHERE id_rol != 1"; 
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                roles.put(rs.getString("nombre"), rs.getInt("id_rol"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return roles;
    }

    public Map<String, Integer> obtenerMapaSucursales() {
        Map<String, Integer> sucursales = new HashMap<>();
        String sql = "SELECT id_sucursal, nombre FROM sucursal WHERE activa = 1";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                sucursales.put(rs.getString("nombre"), rs.getInt("id_sucursal"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return sucursales;
    }
    
    public boolean cambiarEstadoSucursal(int idSucursal, boolean estado) {
        String sql = "UPDATE sucursal SET activa = ? WHERE id_sucursal = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, estado);
            ps.setInt(2, idSucursal);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean modificarUsuario(int idUsuario, String nombre, int idRol, int idSucursal) {
        String sql = "UPDATE usuario SET nombre = ?, id_rol = ?, id_sucursal = ? WHERE id_usuario = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, idRol);
            
            if (idSucursal > 0) {
                ps.setInt(3, idSucursal);
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            
            ps.setInt(4, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public boolean cambiarEstadoUsuario(int idUsuario, boolean estado) {
        String sql = "UPDATE usuario SET activo = ? WHERE id_usuario = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, estado);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }
    
   
   

    public List<Object[]> obtenerParametrosGlobales() {
        List<Object[]> parametros = new ArrayList<>();
      
        String sql = "SELECT id_nivel, numero_nivel, tiempo_base_segundos, pedidos_para_subir, puntos_para_subir FROM nivel ORDER BY numero_nivel ASC";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                parametros.add(new Object[]{
                    rs.getInt("id_nivel"), 
                    rs.getInt("numero_nivel"), 
                    rs.getInt("tiempo_base_segundos"),
                    rs.getInt("pedidos_para_subir"),
                    rs.getInt("puntos_para_subir")
                });
            }
        } catch (Exception e) { 
            System.err.println("Error al obtener parámetros:");
            e.printStackTrace(); 
        }
        return parametros;
    }

  
    public boolean modificarNivel(int idNivel, int tiempoBase, int pedidosSubir, int puntosSubir) {
        String sql = "UPDATE nivel SET tiempo_base_segundos = ?, pedidos_para_subir = ?, puntos_para_subir = ? WHERE id_nivel = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tiempoBase);
            ps.setInt(2, pedidosSubir);
            ps.setInt(3, puntosSubir);
            ps.setInt(4, idNivel);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { 
            e.printStackTrace(); 
            return false; 
        }
    }
}
