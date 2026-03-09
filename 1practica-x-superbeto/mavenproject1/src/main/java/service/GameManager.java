/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;



/**
 *
 * @author cesar
 */

import dao.PartidaDAO;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;
import model.Usuario;
import util.PedidoGenerator;
import util.Sesion;
import view.GameFrame;

public class GameManager {

    private Usuario jugador;
    private int tiempoRestante;

    private GameFrame gameFrame;

    private ScheduledExecutorService scheduler;

    private PedidoGenerator pedidoGenerator;

    public GameManager(Usuario jugador, GameFrame gameFrame) {

        this.jugador = jugador;
        this.gameFrame = gameFrame;

        this.tiempoRestante = 180;
    }

    public void iniciarPartida() {

        System.out.println("Partida iniciada para: " + jugador.getNombre());

        PartidaDAO partidaDAO = new PartidaDAO();

        int idJugador = 1;
        int idSucursal = 1;
        int nivel = 1;

        int idPartida = partidaDAO.crearPartida(idJugador,idSucursal,nivel);

        System.out.println("Partida creada con ID: " + idPartida);

        pedidoGenerator = new PedidoGenerator(gameFrame);
        pedidoGenerator.setIdPartida(idPartida);
        pedidoGenerator.iniciarGeneracion();

        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {

            tiempoRestante--;

            SwingUtilities.invokeLater(() -> {
                gameFrame.actualizarTiempo(tiempoRestante);
            });

            if (tiempoRestante <= 0) {

                finalizarPartida();
            }

        },1,1,TimeUnit.SECONDS);
    }

    public void finalizarPartida(){

        System.out.println("Partida finalizada");

        if(pedidoGenerator != null){
            pedidoGenerator.detener();
        }

        if(scheduler != null){
            scheduler.shutdown();
        }

        SwingUtilities.invokeLater(() -> {

            gameFrame.limpiarPedidos();
            gameFrame.mostrarResumenFinal();

        });

        guardarResultado();
    }

    private void guardarResultado(){

        System.out.println("Guardando resultado en base de datos...");
        System.out.println("Jugador: " + jugador.getNombre());

    }
    
        public void revisarPedidosVencidos(){

        System.out.println("Revisando pedidos vencidos...");

    }
}
