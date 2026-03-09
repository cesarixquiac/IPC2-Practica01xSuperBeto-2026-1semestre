/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author cesar
 */
public class ScoreManager {

    public static final int PEDIDO_COMPLETADO = 100;
    public static final int BONO_EFICIENCIA = 50;
    public static final int PEDIDO_CANCELADO = -30;
    public static final int PEDIDO_NO_ENTREGADO = -50;

    public int calcularPuntajePedido(boolean completado,
                                     boolean eficiente,
                                     boolean cancelado,
                                     boolean noEntregado) {

        int puntos = 0;

        if (completado) {
            puntos += PEDIDO_COMPLETADO;

            if (eficiente) {
                puntos += BONO_EFICIENCIA;
            }
        }

        if (cancelado) {
            puntos += PEDIDO_CANCELADO;
        }

        if (noEntregado) {
            puntos += PEDIDO_NO_ENTREGADO;
        }

        return puntos;
    }
    
    public boolean esEficiente(int tiempoLimite, int tiempoUsado){

    return tiempoUsado <= (tiempoLimite / 2);

}
}
