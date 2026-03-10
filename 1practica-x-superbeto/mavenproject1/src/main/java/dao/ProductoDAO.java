/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Producto;
import util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author cesar
 */
public class ProductoDAO {

 
    public List<Producto> obtenerProductosAleatorios(int cantidad, int idSucursal) {

        List<Producto> productos = new ArrayList<>();


        String sql = """
            SELECT p.id_producto, p.nombre
            FROM producto p
            JOIN inventario_sucursal inv ON p.id_producto = inv.id_producto
            WHERE inv.id_sucursal = ? AND inv.activo = 1 AND p.activo_global = 1
            ORDER BY RAND()
            LIMIT ?
        """;

        try {

            Connection conn = ConexionDB.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

       
            ps.setInt(1, idSucursal);
            ps.setInt(2, cantidad);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Producto p = new Producto();
                p.setIdProducto(rs.getInt("id_producto"));
                p.setNombre(rs.getString("nombre"));

                productos.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return productos;
    }
}