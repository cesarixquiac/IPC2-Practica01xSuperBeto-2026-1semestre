/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package timer;

/**
 *
 * @author cesar
 */
import java.util.Timer;
import java.util.TimerTask;
import service.GameManager;

public class GameTimer {

    private int tiempo = 180;

    private Timer timer = new Timer();

    private GameManager manager;

    public GameTimer(GameManager manager){
        this.manager = manager;
    }

    public void iniciar(){

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                tiempo--;

                System.out.println("Tiempo restante: " + tiempo);

                manager.revisarPedidosVencidos();

                if(tiempo <= 0){

                    System.out.println("Partida finalizada");

                    timer.cancel();
                }
            }

        },1000,1000);

    }

}
