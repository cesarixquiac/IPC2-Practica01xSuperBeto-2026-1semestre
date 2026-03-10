/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import util.ConexionDB;

/**
 *
 * @author cesar
 */
public class InventarioDAO {


    public List<Object[]> obtenerMenuSucursal(int idSucursal) {
        List<Object[]> menu = new ArrayList<>();
        
        String sql = """
                SELECT p.id_producto, p.nombre, p.descripcion, p.precio_base,
                       COALESCE(inv.activo, 0) as activo
                FROM producto p
                LEFT JOIN inventario_sucursal inv 
                  ON p.id_producto = inv.id_producto AND inv.id_sucursal = ?
                WHERE p.activo_global = 1
                """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setInt(1, idSucursal);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_producto");
                    String nombre = rs.getString("nombre");
                    String desc = rs.getString("descripcion");
                    double precio = rs.getDouble("precio_base");
                    boolean activo = rs.getBoolean("activo");
                    
                    String estadoTexto = activo ? "ACTIVO" : "INACTIVO";
                    
                    // Agregamos el precio al arreglo
                    menu.add(new Object[]{id, nombre, desc, precio, estadoTexto});
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener menú de la sucursal:");
            e.printStackTrace();
        }

        return menu;
    }

    public void cambiarEstadoProducto(int idSucursal, int idProducto, boolean nuevoEstado) {
        String sql = """
                INSERT INTO inventario_sucursal (id_sucursal, id_producto, activo)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE activo = ?
                """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setInt(1, idSucursal);
            ps.setInt(2, idProducto);
            ps.setBoolean(3, nuevoEstado);
            ps.setBoolean(4, nuevoEstado); 
            
            ps.executeUpdate();
            
        } catch (Exception e) {
            System.err.println("Error al cambiar estado del producto:");
            e.printStackTrace();
        }
    }


    public boolean crearProducto(String nombre, String descripcion, double precioBase) {
        String sql = "INSERT INTO producto (nombre, descripcion, precio_base, activo_global) VALUES (?, ?, ?, 1)";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setDouble(3, precioBase);
            
            int filas = ps.executeUpdate();
            return filas > 0;
            
        } catch (Exception e) {
            System.err.println("Error al crear producto:");
            e.printStackTrace();
            return false;
        }
    }


    public boolean modificarProducto(int idProducto, String nombre, String descripcion, double precioBase) {
        String sql = "UPDATE producto SET nombre = ?, descripcion = ?, precio_base = ? WHERE id_producto = ?";
        
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setDouble(3, precioBase);
            ps.setInt(4, idProducto);
            
            int filas = ps.executeUpdate();
            return filas > 0;
            
        } catch (Exception e) {
            System.err.println("Error al modificar producto:");
            e.printStackTrace();
            return false;
        }
    }
}