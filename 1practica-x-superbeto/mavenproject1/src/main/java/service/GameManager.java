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

    private int idPartida;

    private boolean partidaActiva = false;

    public GameManager(Usuario jugador, GameFrame gameFrame) {

        this.jugador = jugador;
        this.gameFrame = gameFrame;

        this.tiempoRestante = 180;
    }

    public void iniciarPartida() {

        System.out.println("Partida iniciada para: " + jugador.getNombre());

        PartidaDAO partidaDAO = new PartidaDAO();

        int idJugador = jugador.getIdUsuario(); 
        int idSucursal = jugador.getIdSucursal(); 
        int nivel = 1;

        idPartida = partidaDAO.crearPartida(idJugador, idSucursal, nivel);
        
        if (idPartida == -1) {
            System.err.println("Error: No se pudo crear la partida en la base de datos.");
        }

        gameFrame.setIdPartida(idPartida);

        partidaActiva = true;

        System.out.println("Partida creada con ID: " + idPartida);

       pedidoGenerator = new PedidoGenerator(gameFrame);
        pedidoGenerator.setIdPartida(idPartida);
        
    
        pedidoGenerator.setIdSucursal(idSucursal); 
        
        pedidoGenerator.iniciarGeneracion();
        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {

            if(!partidaActiva){
                return;
            }

            tiempoRestante--;

            SwingUtilities.invokeLater(() -> {
                gameFrame.actualizarTiempo(tiempoRestante);
            });

            if (tiempoRestante <= 0) {
                finalizarPartida();
            }

        }, 1, 1, TimeUnit.SECONDS);
    }

    public void finalizarPartida() {

        if(!partidaActiva){
            return;
        }

        partidaActiva = false;

        System.out.println("Partida finalizada");

        if (pedidoGenerator != null) {
            pedidoGenerator.detener();
        }

        if (scheduler != null) {
            scheduler.shutdown();
        }

        
        int puntajeFinal = gameFrame.getPuntaje();
        int nivelFinal = gameFrame.getNivelActual();

        SwingUtilities.invokeLater(() -> {
            gameFrame.finalizarPartida();
        });

        guardarResultado(puntajeFinal, nivelFinal);
    }

    private void guardarResultado(int puntajeFinal, int nivelFinal) {

        System.out.println("Guardando resultado en base de datos...");
        System.out.println("Jugador: " + jugador.getNombre());

        PartidaDAO partidaDAO = new PartidaDAO();

        partidaDAO.finalizarPartida(idPartida, puntajeFinal, nivelFinal);

        System.out.println("Partida guardada con puntaje: " + puntajeFinal + " | Nivel Alcanzado: " + nivelFinal);
    }

    public GameFrame getGameFrame() {
        return gameFrame;
    }
}
