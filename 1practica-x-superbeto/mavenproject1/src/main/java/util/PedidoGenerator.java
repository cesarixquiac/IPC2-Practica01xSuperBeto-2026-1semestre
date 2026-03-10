/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 *
 * @author cesar
 */
import dao.ProductoDAO;
import dao.PedidoDAO;
import model.Producto;
import view.GameFrame;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import javax.swing.SwingUtilities;
import model.Pedido;

public class PedidoGenerator {
    
    private GameFrame gameFrame;
    private int idPartida;
    
    private int idSucursal; 

    private ScheduledExecutorService scheduler;

    public PedidoGenerator(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
    }

    public void setIdPartida(int idPartida) {
        this.idPartida = idPartida;
    }


    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public void iniciarGeneracion() {

        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {

            generarPedido();

        }, 5, 10, TimeUnit.SECONDS);
    }

    private void generarPedido() {
        try {
            if (gameFrame.getCantidadPedidos() >= 5) {
                System.out.println("Máximo de pedidos activos alcanzado");
                return;
            }

            Random random = new Random();
            int cantidadProductos = random.nextInt(3) + 1;
            ProductoDAO productoDAO = new ProductoDAO();
            
            List<Producto> productos = productoDAO.obtenerProductosAleatorios(cantidadProductos, idSucursal);

          
            if (productos.isEmpty()) {
                System.out.println("No hay productos activos en esta sucursal para generar pedidos.");
                return;
            }

            System.out.println("----- NUEVO PEDIDO -----");
            String pedidoTexto = "";
            for (Producto p : productos) {
                System.out.println("Producto: " + p.getNombre());
                pedidoTexto += p.getNombre() + "<br>";
            }

            PedidoDAO pedidoDAO = new PedidoDAO();

            int tiempoLimite = gameFrame.getTiempoBaseActual(); 

            int idPedido = pedidoDAO.crearPedido(idPartida, tiempoLimite, productos);

            if (idPedido > 0) {
                System.out.println("Pedido guardado en BD con ID: " + idPedido + " - Tiempo asignado: " + tiempoLimite + "s");

                String textoFinal = pedidoTexto;
                SwingUtilities.invokeLater(() -> {
                  
                    gameFrame.agregarPedido(idPedido, textoFinal, tiempoLimite);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void detener() {

        if (scheduler != null) {
            scheduler.shutdown();
        }
    }
}