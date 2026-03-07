/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

/**
 *
 * @author cesar
 */
public class DatabaseTest {

    public static void main(String[] args) {

        try {
            Connection conn = ConexionDB.getConnection();

            if (conn != null && !conn.isClosed()) {

                DatabaseMetaData meta = conn.getMetaData();

                System.out.println("==================================");
                System.out.println("CONEXIÓN EXITOSA");
                System.out.println("Base de datos: " + meta.getURL());
                System.out.println("Usuario: " + meta.getUserName());
                System.out.println("Motor: " + meta.getDatabaseProductName());
                System.out.println("Versión: " + meta.getDatabaseProductVersion());
                System.out.println("==================================");

            } else {
                System.out.println("No se pudo establecer conexión.");
            }

        } catch (Exception e) {
            System.out.println("Error en la prueba de conexión.");
            e.printStackTrace();
        }
    }
}
